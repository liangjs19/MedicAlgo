package com.mbaxajl3.medicalgo.voice;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.mbaxajl3.medicalgo.Factory;
import com.mbaxajl3.medicalgo.SpeechService;
import com.mbaxajl3.medicalgo.VariableChangeListener;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Objects;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import edu.cmu.pocketsphinx.SpeechRecognizerSetup;

public class SphinxRecognizer extends VoiceRecognizer implements
        RecognitionListener {
    private static final String TAG = "SphinxRecognizer";
    private static final String KWS_SEARCH = "wakeup";
    /* Keyword we are looking for to activate recognition */
    private static final String KEYPHRASE = "okay trachy";
    private Context context;
    /* Recognition object */
    private SpeechRecognizer recognizer;
    private VariableChangeListener variableChangeListener;
    private SpeechService speechService;

    public SphinxRecognizer(Context context) {
        this.context = context;
        speechService = Factory.getSpeechService();
    }

    private void setupRecognizer(File assetsDir) throws IOException {
        recognizer = SpeechRecognizerSetup.defaultSetup()
                .setAcousticModel(new File(assetsDir, "en-us-ptm"))
                .setDictionary(new File(assetsDir, "cmudict-en-us.dict"))
                // Disable this line if you don't want recognizer to save raw
                // audio files to app's storage
                //.setRawLogDir(assetsDir)
                .getRecognizer();

        recognizer.addListener(this);
        // Create keyword-activation search.
        recognizer.addKeyphraseSearch(KWS_SEARCH, KEYPHRASE);
        // Create your custom grammar-based search
    }

    @Override
    public void start() {
        stop();
        Log.v(TAG, "started");
        new SetupTask(this, context).execute();
    }

    @Override
    public void stop() {
        if (recognizer != null) {
            recognizer.cancel();
            recognizer.shutdown();
        }
    }

    @Override
    public void restart() {
        cleanup();
        start();
    }

    @Override
    public void cleanup() {
        stop();
        recognizer = null;
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

    private void switchSearch(String searchName) {
        if (recognizer == null) {
            return;
        }
        recognizer.stop();
        Log.v(TAG, "switch search");
        recognizer.startListening(searchName);
    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onEndOfSpeech() {
        Log.v(TAG, "end of speech");
    }

    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        if (hypothesis == null)
            return;

        String text = hypothesis.getHypstr();
        if (variableChangeListener != null) {
            variableChangeListener.onVariableChanged(text);
        }
        Log.v(TAG, text);
        stop();
    }

    @Override
    public void onResult(Hypothesis hypothesis) {
        if (hypothesis == null)
            return;
        String text = hypothesis.getHypstr();
    }

    @Override
    public void onError(Exception e) {
        Log.e(TAG, Objects.requireNonNull(e.getMessage()));
    }

    @Override
    public void onTimeout() {
        Log.v(TAG, "on timeout");
    }

    public VariableChangeListener getVariableChangeListener() {
        return variableChangeListener;
    }

    public void setVariableChangeListener(VariableChangeListener variableChangeListener) {
        this.variableChangeListener = variableChangeListener;
    }

    private static class SetupTask extends AsyncTask<Void, Void, Exception> {
        WeakReference<SphinxRecognizer> sphinxRecognizerWeakReference;
        WeakReference<Context> contextWeakReference;

        SetupTask(SphinxRecognizer sr, Context context) {
            this.sphinxRecognizerWeakReference = new WeakReference<>(sr);
            this.contextWeakReference = new WeakReference<>(context);
        }

        @Override
        protected Exception doInBackground(Void... params) {
            try {
                Assets assets = new Assets(this.contextWeakReference.get());
                File assetDir = assets.syncAssets();
                sphinxRecognizerWeakReference.get().setupRecognizer(assetDir);
            } catch (IOException e) {
                return e;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Exception result) {
            if (result != null) {
            } else {
                sphinxRecognizerWeakReference.get().switchSearch(KWS_SEARCH);
            }
        }
    }
}
