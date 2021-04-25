package com.example.myapplication;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class VideoPlayerActivity extends AppCompatActivity {

    private VideoView videoView;
    private int position;
    private List<Video> videoList;
    private ImageView playSign;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_video_player);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
                , WindowManager.LayoutParams.FLAG_FULLSCREEN);
        playSign = findViewById(R.id.play_sign);
        playSign.setAlpha(0f);
        Intent intent = getIntent();
        videoView = findViewById(R.id.videoView);
        Bundle bundle = intent.getExtras();
        videoList = bundle.getParcelableArrayList("videolist");
        position = bundle.getInt("position");
        videoView.setVideoURI(Uri.parse(videoList.get(position).getVideoUrl()));
        videoView.start();
        videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoView.isPlaying()) {
                    videoView.pause();
                    playSign.setAlpha(1f);
                } else {
                    videoView.start();
                    playSign.setAlpha(0f);
                }
            }
        });
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                playSign.setAlpha(1f);
                videoView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (videoView.isPlaying()) {
                            videoView.pause();
                            playSign.setAlpha(1f);
                        } else {
                            videoView.start();
                            playSign.setAlpha(0f);
                        }
                    }
                });
            }
        });
    }
}