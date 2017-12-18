package com.hyyas.ouy.movies;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;


public class PlayerActivity extends AppCompatActivity {
    private Handler handler = new Handler();
    MediaController mediaController;
    VideoView videoView;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullScreen();
        setContentView(R.layout.activity_player);
        init();




        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {

                Toast.makeText(PlayerActivity.this, "The movie is Prepared !", Toast.LENGTH_SHORT).show();
                playing();
            }
        });
    }

    protected void init() {
        Intent intent = getIntent();
        int id = intent.getIntExtra("id", -1);
        if (id < 0) {
            this.finish();
        }
        Uri uri = Uri.parse(Movies.getMovieById(id).getUrl());
        mediaController=new MediaController(this);
        videoView = (VideoView) this.findViewById(R.id.videoView);
        progressBar = (ProgressBar) this.findViewById(R.id.progressBar);
        loading();
        videoView.setMediaController(mediaController);
        videoView.setVideoURI(uri);
        videoView.start();
        videoView.requestFocus();
    }

    protected void fullScreen() {
        Window window = getWindow();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        window.setFlags(flag, flag);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    protected void loading() {
        progressBar.setVisibility(View.VISIBLE);
        videoView.setAlpha(0);

    }

    protected void playing() {
        progressBar.setVisibility(View.INVISIBLE);
        videoView.setAlpha(1);
    }



}
