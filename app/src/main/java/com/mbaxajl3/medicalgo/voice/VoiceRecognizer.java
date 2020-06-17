package com.mbaxajl3.medicalgo.voice;

public abstract class VoiceRecognizer {
    public abstract void start();

    public abstract void stop();

    public abstract void restart();

    public abstract void cleanup();

    public abstract void handleTranscript();
}
