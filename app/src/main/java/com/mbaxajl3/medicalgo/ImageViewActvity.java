package com.mbaxajl3.medicalgo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.github.piasy.biv.indicator.progresspie.ProgressPieIndicator;
import com.github.piasy.biv.view.BigImageView;
import com.mbaxajl3.medicalgo.models.Algorithm;

public class ImageViewActvity extends AppCompatActivity {
    private static final String TAG = "ImageViewActvity";
    private SpeechService speechService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_view_actvity);
        Toolbar toolbar = findViewById(R.id.algorithmImageToolbar);
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);
        try {
            Intent intent = getIntent();
            Algorithm algorithm = (Algorithm) intent.getSerializableExtra("algorithm");
            getSupportActionBar()
                    .setTitle(algorithm.getName());
            loadImage(algorithm.getImage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        speechService = Factory.getSpeechService();
        speechService.setImageViewerContext(this);
    }

    @Override
    public void onStart() {
        Log.v(TAG, "onStart");
        super.onStart();
        Activities.setCurrentActivity(Activities.ImageViewActivity);
    }

    @Override
    public void onResume() {
        Log.v(TAG, "onResume");
        super.onResume();
    }


    @Override
    public void onPause() {
        Log.v(TAG, "onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.v(TAG, "onStop");
        speechService.setImageViewerContext(null);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.v(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish(); // close this activity as oppose to navigating up
        return false;
    }

    private void loadImage(String uri) {
        BigImageView bigImageView = findViewById(R.id.algorithmImage);
        bigImageView.setProgressIndicator(new ProgressPieIndicator());
        Log.v(TAG, "loading image " + uri);
        bigImageView.showImage(Uri.parse(uri));
    }

}
