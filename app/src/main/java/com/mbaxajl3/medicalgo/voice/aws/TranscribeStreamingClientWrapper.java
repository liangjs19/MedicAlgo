/*
 * Copyright 2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.mbaxajl3.medicalgo.voice.aws;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CompletableFuture;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.regions.providers.DefaultAwsRegionProviderChain;
import software.amazon.awssdk.services.transcribestreaming.TranscribeStreamingAsyncClient;
import software.amazon.awssdk.services.transcribestreaming.model.AudioStream;
import software.amazon.awssdk.services.transcribestreaming.model.LanguageCode;
import software.amazon.awssdk.services.transcribestreaming.model.MediaEncoding;
import software.amazon.awssdk.services.transcribestreaming.model.StartStreamTranscriptionRequest;

/**
 * This wraps the TranscribeStreamingAsyncClient with easier to use methods for quicker integration with the GUI. This
 * also provides examples on how to handle the various exceptions that can be thrown and how to implement a request
 * stream for input to the streaming service.
 */
public class TranscribeStreamingClientWrapper {
    private static final int SAMPLE_RATE_US = 16000;
    private static final int SAMPLE_RATE_GB = 8000;
    private TranscribeStreamingRetryClient client;
    private AudioStreamPublisher requestStream;
    private AudioInputStream audioInputStream;
    private static AwsCredentialsProvider credentialsProvider;

    public TranscribeStreamingClientWrapper(String accessKey, String secretKey) {
        setCredentials(accessKey, secretKey);
        client = new TranscribeStreamingRetryClient(getClient());
    }

    public static TranscribeStreamingAsyncClient getClient() {
        Region region = getRegion();
        String endpoint = "https://transcribestreaming." + region.toString().toLowerCase().replace('_', '-') + ".amazonaws.com";
        try {
            return TranscribeStreamingAsyncClient.builder()
                    .credentialsProvider(getCredentials())
                    .endpointOverride(new URI(endpoint))
                    .region(region)
                    .build();
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid URI syntax for endpoint: " + endpoint);
        }

    }

    /**
     * Get region from default region provider chain, default to PDX (us-west-2)
     */
    private static Region getRegion() {
        Region region;
        try {
            region = new DefaultAwsRegionProviderChain().getRegion();
        } catch (SdkClientException e) {
            region = Region.EU_WEST_1;
        }

        return region;
    }

    /**
     * @return AWS credentials to be used to connect to Transcribe service. This example uses the default credentials
     * provider, which looks for environment variables (AWS_ACCESS_KEY_ID and AWS_SECRET_ACCESS_KEY) or a credentials
     * file on the system running this program.
     */
    private static AwsCredentialsProvider getCredentials() {
        return credentialsProvider;
    }

    public void setCredentials(String accessKeyId, String secretAccessKey) {
        if (accessKeyId.isEmpty()) {
            accessKeyId = "a";
        }

        if (secretAccessKey.isEmpty()) {
            secretAccessKey = "a";
        }

        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(
                accessKeyId, secretAccessKey);
        credentialsProvider = StaticCredentialsProvider.create(awsCreds);
    }

    /**
     * Start real-time speech recognition. Transcribe streaming java client uses Reactive-streams interface.
     * For reference on Reactive-streams: https://github.com/reactive-streams/reactive-streams-jvm
     *
     * @param responseHandler StartStreamTranscriptionResponseHandler that determines what to do with the response
     *                        objects as they are received from the streaming service
     */
    public CompletableFuture<Void> startTranscription(StreamTranscriptionBehavior responseHandler) {
        if (requestStream != null) {
            throw new IllegalStateException("Stream is already open");
        }
        requestStream = new AudioStreamPublisher(getStreamFromMic());

        return client.startStreamTranscription(
                //Request parameters. Refer to API documentation for details.
                getRequest(SAMPLE_RATE_US),
                //AudioEvent publisher containing "chunks" of audio data to transcribe
                requestStream,
                //Defines what to do with transcripts as they arrive from the service
                responseHandler);
    }

    /**
     * Stop in-progress transcription if there is one in progress by closing the request stream
     */
    public void stopTranscription() {
        if (requestStream != null) {
            try {
                requestStream.inputStream.close();
            } catch (IOException ex) {
                System.out.println("Error stopping input stream: " + ex);
            } finally {
                requestStream = null;
            }
        }
    }

    /**
     * Close clients and streams
     */
    public void close() {
        try {
            if (requestStream != null) {
                requestStream.inputStream.close();
            }
        } catch (IOException ex) {
            System.out.println("error closing in-progress microphone stream: " + ex);
        } finally {
            client.close();
        }
    }

    /**
     * Build an input stream from a microphone if one is present.
     *
     * @return InputStream containing streaming audio from system's microphone
     * //     * @throws LineUnavailableException When a microphone is not detected or isn't properly working
     */
    private InputStream getStreamFromMic() {
        return audioInputStream;
    }

    /**
     * Build StartStreamTranscriptionRequestObject containing required parameters to open a streaming transcription
     * request, such as audio sample rate and language spoken in audio
     *
     * @param mediaSampleRateHertz sample rate of the audio to be streamed to the service in Hertz
     * @return StartStreamTranscriptionRequest to be used to open a stream to transcription service
     */
    private StartStreamTranscriptionRequest getRequest(Integer mediaSampleRateHertz) {
        return StartStreamTranscriptionRequest.builder()
                .languageCode(LanguageCode.EN_US.toString())
                .mediaEncoding(MediaEncoding.PCM)
                .mediaSampleRateHertz(mediaSampleRateHertz)
                .vocabularyName("medical")
                .build();
    }

    public void startRecording() {
        audioInputStream = new AudioInputStream();
        audioInputStream.startRecording(SAMPLE_RATE_US);
    }

    public void stopRecording() {
        if (audioInputStream != null)
            audioInputStream.close();
    }

    /**
     * AudioStreamPublisher implements audio stream publisher.
     * AudioStreamPublisher emits audio stream asynchronously in a separate thread
     */
    private static class AudioStreamPublisher implements Publisher<AudioStream> {
        private final InputStream inputStream;

        private AudioStreamPublisher(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        @Override
        public void subscribe(Subscriber<? super AudioStream> s) {
            s.onSubscribe(new ByteToAudioEventSubscription(s, inputStream));
        }
    }
}
