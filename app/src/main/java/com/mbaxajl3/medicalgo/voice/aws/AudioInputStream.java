package com.mbaxajl3.medicalgo.voice.aws;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.media.audiofx.NoiseSuppressor;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

public class AudioInputStream extends InputStream {

    private final static String TAG = "AudioInputStream";
    private static final int MIC_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int MIC_CHANNEL_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private static final int MIC_SOURCE = MediaRecorder.AudioSource.VOICE_RECOGNITION;
    private static final int CHUNK_SIZE_SAMPLES = 1280;
    private static final int BYTES_PER_SAMPLE = 2;
    private AudioRecord audioRecord;

    public void startRecording(int sampleRate) {
        if (audioRecord == null) {
            audioRecord =
                    new AudioRecord(
                            MIC_SOURCE,
                            sampleRate,
                            MIC_CHANNELS,
                            MIC_CHANNEL_ENCODING,
                            CHUNK_SIZE_SAMPLES * BYTES_PER_SAMPLE);
        }
        // enable noise reduction
        if (NoiseSuppressor.isAvailable()) {
            Log.v(TAG, "noise reduction available");
            NoiseSuppressor.create(audioRecord.getAudioSessionId());
        }

        audioRecord.startRecording();
    }

    @Override
    public int read() throws IOException {
        byte[] tmp = {0};
        read(tmp, 0, 1);
        return tmp[0];
    }

    public int read(byte[] audioData, int offset, int length) {
        return audioRecord.read(audioData, offset, length);
    }

    public void close() {
        if (audioRecord != null && audioRecord.getState() == AudioRecord.STATE_INITIALIZED) {
            audioRecord.stop();
            audioRecord.release();
        }

        try {
            super.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
