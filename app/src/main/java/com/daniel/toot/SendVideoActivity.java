package com.daniel.toot;

import static com.daniel.toot.ChatActivity.selectedVideoPath;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

public class SendVideoActivity extends AppCompatActivity {
    VideoView send_video;
    ImageView send_reject, send_accept, play_button_video;
    Chronometer playback_timer_video;
    SeekBar seekbar_video;
    TextView video_length;

    boolean isPlaying = false;
    long timePaused = 0;
    Handler seekbarHandler = new Handler();
    Runnable updateSeekbar;
    public static boolean isSendVideo = false;
    private boolean resumeVideo = false;

    long seekbar_video_time = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_video);
        send_video = findViewById(R.id.request_send_video);
        send_reject = findViewById(R.id.send_reject_video);
        send_accept = findViewById(R.id.send_accept_video);
        play_button_video = findViewById(R.id.play_button_video);
        playback_timer_video = findViewById(R.id.playback_timer_video);
        seekbar_video = findViewById(R.id.seekbar_video);
        video_length = findViewById(R.id.video_length);


        send_video.setVideoPath(selectedVideoPath);
        send_video.seekTo(1);
        video_length.setText(VideoXActivity.timer_string);

        ChatAdapter.video_width = send_video.getWidth();
        ChatAdapter.video_height = send_video.getHeight();

        play_button_video.setOnClickListener(view -> {
            if (isPlaying) {
                pauseVideo();
                isPlaying = false;
            } else {
                if (resumeVideo) {
                    resumeVideo();
                } else {
                    resumeVideo(selectedVideoPath);
                }

                isPlaying = true;
            }
        });
        seekbar_video.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    send_video.seekTo(i);
                    seekbar_video.setMax(send_video.getDuration());

                    seekbar_video_time = i;

                    resumeVideo();
                    resumeVideo = true;
                    isPlaying = true;
                    send_video.setOnCompletionListener(mediaPlayer -> {
                        playback_timer_video.stop();
                        playback_timer_video.setBase(SystemClock.elapsedRealtime());
                        play_button_video.setImageDrawable(getDrawable(R.drawable.play_white));
                        isPlaying = false;
                        resumeVideo = false;
                        seekbar_video.setProgress(0);
                        send_video.seekTo(0);
                        send_video.pause();
                        seekbarHandler.removeCallbacks(updateSeekbar);
                    });
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                seekbarHandler.removeCallbacks(updateSeekbar);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekbarHandler.postDelayed(updateSeekbar, 1000);
            }
        });

        send_video.setOnCompletionListener(mediaPlayer -> {
            playback_timer_video.stop();
            playback_timer_video.setBase(SystemClock.elapsedRealtime());
            play_button_video.setImageDrawable(getDrawable(R.drawable.play_white));
            isPlaying = false;
            resumeVideo = false;
            seekbar_video.setProgress(0);
            mediaPlayer.seekTo(0);
            mediaPlayer.pause();
            seekbarHandler.removeCallbacks(updateSeekbar);
        });
        send_accept.setOnClickListener(view -> {
            Intent intent = new Intent();
            isSendVideo = true;
            setResult(RESULT_OK, intent);
            finish();
        });
        send_reject.setOnClickListener(view -> {
            finish();
        });

    }

    private void pauseVideo() {
        send_video.pause();
        playback_timer_video.stop();
        timePaused = SystemClock.elapsedRealtime() - playback_timer_video.getBase();
        seekbarHandler.removeCallbacks(updateSeekbar);
        play_button_video.setImageDrawable(getDrawable(R.drawable.play_white));
    }

    private void resumeVideo() {
        send_video.start();
        if (seekbar_video_time == 0) {
            playback_timer_video.setBase(SystemClock.elapsedRealtime() - timePaused);
            timePaused = 0;
        } else {
            playback_timer_video.setBase(SystemClock.elapsedRealtime() - seekbar_video_time);
            seekbar_video_time = 0;
        }

        playback_timer_video.start();
        play_button_video.setImageDrawable(getDrawable(R.drawable.ic_baseline_pause_24));
        updateRunnableVideo();
        seekbarHandler.postDelayed(updateSeekbar, 0);
    }

    private void resumeVideo(String path) {
        send_video.start();
        isPlaying = true;
        resumeVideo = true;
        seekbar_video.setMax(send_video.getDuration());
        playback_timer_video.start();
        play_button_video.setImageDrawable(getDrawable(R.drawable.ic_baseline_pause_24));
        playback_timer_video.setBase(SystemClock.elapsedRealtime());
        updateRunnableVideo();
        seekbarHandler.postDelayed(updateSeekbar, 0);
        send_video.setOnCompletionListener(mediaPlayer -> {
            playback_timer_video.stop();
            playback_timer_video.setBase(SystemClock.elapsedRealtime());
            play_button_video.setImageDrawable(getDrawable(R.drawable.play_white));
            isPlaying = false;
            resumeVideo = false;
            seekbar_video.setProgress(0);
            send_video.seekTo(0);
            send_video.pause();
            seekbarHandler.removeCallbacks(updateSeekbar);
        });
    }

    private void updateRunnableVideo() {
        seekbarHandler = new Handler();
        updateSeekbar = new Runnable() {
            @Override
            public void run() {
                seekbar_video.setProgress(send_video.getCurrentPosition());
                seekbarHandler.postDelayed(this, 100);
            }
        };
    }
}