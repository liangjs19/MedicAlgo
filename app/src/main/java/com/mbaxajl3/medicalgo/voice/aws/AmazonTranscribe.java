package com.mbaxajl3.medicalgo.voice.aws;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.mbaxajl3.medicalgo.Factory;
import com.mbaxajl3.medicalgo.R;
import com.mbaxajl3.medicalgo.SpeechService;
import com.mbaxajl3.medicalgo.Util;
import com.mbaxajl3.medicalgo.VariableChangeListener;
import com.mbaxajl3.medicalgo.models.Pair;
import com.mbaxajl3.medicalgo.voice.VoiceRecognizer;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import software.amazon.awssdk.services.transcribestreaming.model.Result;
import software.amazon.awssdk.services.transcribestreaming.model.StartStreamTranscriptionResponse;
import software.amazon.awssdk.services.transcribestreaming.model.TranscriptEvent;
import software.amazon.awssdk.services.transcribestreaming.model.TranscriptResultStream;

public class AmazonTranscribe extends VoiceRecognizer {
    private static final String TAG = "AMAZON";
    private String language = "en-GB";
    private Context context;
    private String accessKey;
    private String secretKey;
    private CompletableFuture<Void> inProgressStreamingRequest;
    private TranscribeStreamingClientWrapper client;
    private String finalTranscript = "";
    private VariableChangeListener variableChangeListener;
    private SpeechService speechService;

    public AmazonTranscribe(Context context, String accessKey, String secretKey) {
        this.context = context;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        speechService = Factory.getSpeechService();
    }

    @Override
    public void start() {
        stop();
        cleanup();
        client = new TranscribeStreamingClientWrapper(accessKey, secretKey);
        client.startRecording();
        if (inProgressStreamingRequest == null) {
            inProgressStreamingRequest = client.startTranscription(getResponseHandler());
        }
    }

    @Override
    public void stop() {
        if (client != null)
            client.stopRecording();
        if (inProgressStreamingRequest != null) {
            try {
                if (client != null)
                    client.stopTranscription();
                inProgressStreamingRequest.get();
            } catch (ExecutionException | InterruptedException e) {
                System.out.println("error closing stream");
            } finally {
                inProgressStreamingRequest = null;
            }
        }
    }

    @Override
    public void restart() {

    }

    @Override
    public void cleanup() {
        if (inProgressStreamingRequest != null) {
            inProgressStreamingRequest.completeExceptionally(new InterruptedException());
        }
        if (client != null)
            client.close();
        client = null;
    }

    @Override
    public void handleTranscript() {
        setVariableChangeListener(new VariableChangeListener() {
            @Override
            public void onVariableChanged(Object... variableThatHasChanged) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    public void run() {
                        speechService.handleRecognizedVoice(variableThatHasChanged[0].toString());
                    }
                });
            }
        });
    }

    public void setKeys(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    /**
     * A StartStreamTranscriptionResponseHandler class listens to events from Transcribe streaming service that return
     * transcriptions, and decides what to do with them. This example displays the transcripts in the GUI window, and
     * combines the transcripts together into a final transcript at the end.
     */
    private StreamTranscriptionBehavior getResponseHandler() {
        return new StreamTranscriptionBehavior() {

            //This will handle errors being returned from AWS Transcribe in your response. Here we just print the exception.
            @Override
            public void onError(Throwable e) {
                System.out.println(e.getMessage());
                Throwable cause = e.getCause();
                while (cause != null) {
                    System.out.println("Caused by: " + cause.getMessage());
                    if (Util.containsIgnoreCase(Objects.requireNonNull(cause.getMessage()), "Unable to resolve host")) {
                        speechService.getResponseStatusListener().onVariableChanged(new Pair<>(context.getString(R.string.network_error), false));
                    } else if (Util.containsIgnoreCase(Objects.requireNonNull(cause.getMessage()), "credentials") || Util.containsIgnoreCase(Objects.requireNonNull(cause.getMessage()), "token")) {
                        speechService.getResponseStatusListener().onVariableChanged(new Pair<>(context.getString(R.string.invalid_keys), false));
                    } else {
                        speechService.getResponseStatusListener().onVariableChanged(new Pair<>(context.getString(R.string.error), false));
                    }

                    Arrays.stream(cause.getStackTrace()).forEach(l -> System.out.println("  " + l));
                    if (cause.getCause() != cause) { //Look out for circular causes
                        cause = cause.getCause();
                    } else {
                        cause = null;
                    }
                }
                System.out.println("Error Occurred: " + e);

            }

            /*
            This handles each event being received from the Transcribe service. In this example we are displaying the
            transcript as it is updated, and when we receive a "final" transcript, we append it to our finalTranscript
            which is returned at the end of the microphone streaming.
             */
            @Override
            public void onStream(TranscriptResultStream event) {
                List<Result> results = ((TranscriptEvent) event).transcript().results();
                if (results.size() > 0) {
                    Result firstResult = results.get(0);
                    if (firstResult.alternatives().size() > 0 && !firstResult.alternatives().get(0).transcript().isEmpty()) {
                        String transcript = firstResult.alternatives().get(0).transcript();
                        if (!transcript.isEmpty()) {
                            System.out.println(transcript);
                            String displayText;
                            if (!firstResult.isPartial()) {
                                finalTranscript += transcript + " ";
                                displayText = finalTranscript;
                            } else {
                                displayText = finalTranscript + " " + transcript;
                            }
                            Log.v(TAG, displayText);
                            variableChangeListener.onVariableChanged(displayText);
                        }
                    }
                }
            }

            /*
            This handles the initial response from the AWS Transcribe service, generally indicating the streams have
            successfully been opened. Here we just print that we have received the initial response and do some
            UI updates.
             */
            @Override
            public void onResponse(StartStreamTranscriptionResponse r) {
                System.out.println(String.format("=== Received Initial response. Request Id: %s ===", r.requestId()));
                if (speechService != null && speechService.getResponseStatusListener() != null)
                    speechService.getResponseStatusListener().onVariableChanged(true);
            }

            /*
            This method is called when the stream is terminated without error. In our case we will use this opportunity
            to display the final, total transcript we've been aggregating during the transcription period and activates
            the save button.
             */
            @Override
            public void onComplete() {
                System.out.println("=== All records streamed successfully ===");
            }
        };
    }

    public VariableChangeListener getVariableChangeListener() {
        return variableChangeListener;
    }

    public void setVariableChangeListener(VariableChangeListener variableChangeListener) {
        this.variableChangeListener = variableChangeListener;
    }

    public String getFinalTranscript() {
        return finalTranscript;
    }

    public void setFinalTranscript(String finalTranscript) {
        this.finalTranscript = finalTranscript;
    }

    public void clearTranscript() {
        this.finalTranscript = "";
    }
}
