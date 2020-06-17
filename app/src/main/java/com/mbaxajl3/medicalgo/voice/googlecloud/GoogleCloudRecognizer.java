package com.mbaxajl3.medicalgo.voice.googlecloud;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.media.audiofx.NoiseSuppressor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.audio.CodecAndBitrate;
import com.google.audio.asr.CloudSpeechSessionParams;
import com.google.audio.asr.CloudSpeechStreamObserverParams;
import com.google.audio.asr.SpeechRecognitionModelOptions;
import com.google.audio.asr.TranscriptionResultFormatterOptions;
import com.mbaxajl3.medicalgo.Callback;
import com.mbaxajl3.medicalgo.Factory;
import com.mbaxajl3.medicalgo.SpeechService;
import com.mbaxajl3.medicalgo.Util;
import com.mbaxajl3.medicalgo.voice.VoiceRecognizer;
import com.mbaxajl3.medicalgo.voice.googlecloud.cloud.CloudSpeechSessionFactory;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.google.audio.asr.SpeechRecognitionModelOptions.SpecificModel.DICTATION_DEFAULT;
import static com.google.audio.asr.TranscriptionResultFormatterOptions.TranscriptColoringStyle.NO_COLORING;

public class GoogleCloudRecognizer extends VoiceRecognizer {
    private final static String TAG = "GoogleCloudRecognizer";
    private static final int MIC_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int MIC_CHANNEL_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private static final int MIC_SOURCE = MediaRecorder.AudioSource.VOICE_RECOGNITION;
    private static final int SAMPLE_RATE = 16000;
    private static final int CHUNK_SIZE_SAMPLES = 1280;
    private static final int BYTES_PER_SAMPLE = 2;
    private final byte[] buffer = new byte[BYTES_PER_SAMPLE * CHUNK_SIZE_SAMPLES];
    private NetworkConnectionChecker networkChecker;
    private AudioRecord audioRecord;
    // This class was intended to be used from a thread where timing is not critical (i.e. do not
    // call this in a system audio callback). Network calls will be made during all of the functions
    // that RepeatingRecognitionSession inherits from SampleProcessorInterface.
    private RepeatingRecognitionSession recognizer;

    private String language = "en-GB";
    private String apiKey = "";
    private TranscriptionResultUpdatePublisher transcriptUpdater;
    private CloudSpeechSessionFactory cssf;
    private Context context;
    private Runnable readMicData =
            () -> {
                if (audioRecord.getState() != AudioRecord.STATE_INITIALIZED) {
                    return;
                }
                recognizer.init(CHUNK_SIZE_SAMPLES);
                while (audioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING
                        && recognizer.getRepeatedSessionIsInitialized().get()) {
                    audioRecord.read(buffer, 0, CHUNK_SIZE_SAMPLES * BYTES_PER_SAMPLE);
                    recognizer.processAudioBytes(buffer);
                }

                Log.v(TAG, "is not recording");

                if (recognizer != null)
                    recognizer.stop();
            };
    private List<String> biasWords = new ArrayList<>();
    private SpeechService speechService;

    public GoogleCloudRecognizer(Context context, String language, String apiKey) {
        this.context = context;
        this.language = language;
        this.apiKey = apiKey;
        speechService = Factory.getSpeechService();
    }

    public GoogleCloudRecognizer(Context context, String language, String apiKey, List<String> biasWords) {
        this.context = context;
        this.language = language;
        this.apiKey = apiKey;
        this.biasWords = biasWords;
        speechService = Factory.getSpeechService();
    }

    private void constructRepeatingRecognitionSession() {
        String currentLanguageCode = language;
        SpeechRecognitionModelOptions options =
                SpeechRecognitionModelOptions.newBuilder()
                        .setLocale(currentLanguageCode)
                        .setModel(DICTATION_DEFAULT)
                        .addAllBiasWords(biasWords)
                        .build();
        CloudSpeechSessionParams cloudParams =
                CloudSpeechSessionParams.newBuilder()
                        .setObserverParams(
                                CloudSpeechStreamObserverParams.newBuilder().setRejectUnstableHypotheses(false))
                        .setFilterProfanity(true)
                        .setEncoderParams(
                                CloudSpeechSessionParams.EncoderParams.newBuilder()
                                        .setEnableEncoder(true)
                                        .setAllowVbr(true)
                                        .setCodec(CodecAndBitrate.FLAC))
                        .build();

        if (networkChecker == null) {
            networkChecker = new NetworkConnectionChecker(context);
            networkChecker.registerNetworkCallback();
        }

        // There are lots of options for formatting the text. These can be useful for debugging
        // and visualization, but it increases the effort of reading the transcripts.
        TranscriptionResultFormatterOptions formatterOptions =
                TranscriptionResultFormatterOptions.newBuilder()
                        .setTranscriptColoringStyle(NO_COLORING)
                        .build();

        if (cssf != null) {
            cssf.cleanup();
        }
        cssf = new CloudSpeechSessionFactory(cloudParams, apiKey);

        RepeatingRecognitionSession.Builder recognizerBuilder =
                RepeatingRecognitionSession.newBuilder()
                        .setSpeechSessionFactory(cssf)
                        .setSampleRateHz(SAMPLE_RATE)
                        .setTranscriptionResultFormatter(new SafeTranscriptionResultFormatter(formatterOptions))
                        .setSpeechRecognitionModelOptions(options)
                        .setNetworkConnectionChecker(networkChecker);
        recognizer = recognizerBuilder.build();
        recognizer.registerCallback(transcriptUpdater, TranscriptionResultUpdatePublisher.ResultSource.MOST_RECENT_SEGMENT);

    }
    @Override
    public void start() {
        Log.v(TAG, "start");
        constructRepeatingRecognitionSession();
        if (audioRecord != null) {
            audioRecord.stop();
            audioRecord.release();
        }
        audioRecord =
                new AudioRecord(
                        MIC_SOURCE,
                        SAMPLE_RATE,
                        MIC_CHANNELS,
                        MIC_CHANNEL_ENCODING,
                        CHUNK_SIZE_SAMPLES * BYTES_PER_SAMPLE);

        // enable noise reduction
        if (NoiseSuppressor.isAvailable()) {
            Log.v(TAG, "noise reduction available");
            NoiseSuppressor.create(audioRecord.getAudioSessionId());
        }

        audioRecord.startRecording();
        new Thread(readMicData).start();
    }

    @Override
    public void stop() {
        Log.v(TAG, "stop");
        if (audioRecord != null) {
            audioRecord.stop();
        }
    }

    @Override
    public void restart() {
//        cleanup();
//        start();
    }

    @Override
    public void cleanup() {
        if (recognizer != null) {
            recognizer.unregisterCallback(transcriptUpdater);
            networkChecker.unregisterNetworkCallback();
            networkChecker = null;
        }
    }

    @Override
    public void handleTranscript() {
        setTranscriptUpdater((formattedTranscript, updateType) -> {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                public void run() {
                    speechService.handleRecognizedVoice(formattedTranscript.toString());
                }
            });
        });
    }

    public TranscriptionResultUpdatePublisher getTranscriptUpdater() {
        return transcriptUpdater;
    }

    public void setTranscriptUpdater(TranscriptionResultUpdatePublisher transcriptUpdater) {
        this.transcriptUpdater = transcriptUpdater;
    }

    public static void checkApi(String apiKey, Callback callback) {
        String uri = "https://speech.googleapis.com/v1/speech:recognize";
        Uri builtUri = Uri.parse(uri).buildUpon()
                .appendQueryParameter("key", apiKey)
                .build();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, builtUri.toString(), null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v(TAG, response.toString());
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO expand to handle more errors
                        try {
                            byte[] htmlBodyBytes = error.networkResponse.data;
                            String errorMessage = new String(htmlBodyBytes);
                            if (Util.containsIgnoreCase(errorMessage, "API")) {
                                callback.onSuccess(false);
                            } else {
                                callback.onSuccess(true);
                            }
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                });

        Factory.getRequestQueue().add(jsonObjectRequest);
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApiKey() {
        return apiKey;
    }

    public List<String> getBiasWords() {
        return biasWords;
    }

    public void setBiasWords(List<String> biasWords) {
        this.biasWords = biasWords;
    }
}
