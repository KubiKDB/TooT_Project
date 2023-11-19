package com.daniel.toot;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity implements ChatListener {
    EditText chat_message;
    ImageView attach_btn, send_btn, audio_pause, cancel_audio_button, send_audio_button, play_button_audio;
    ConstraintLayout attach_field, image_viewer, video_viewer, audio_record, audio_send_container;
    ImageView image_viewer_back, video_viewer_back;

    ImageView play_button_video;
    Chronometer playback_timer_video, audio_timer, playback_timer_audio;
    SeekBar seekbar_video, seekbar_audio;
    TextView video_length, audio_length;

    ImageView file_attach, gallery_attach, camera_attach, record_attach;

    RecyclerView chat_view;

    File file;
    List<File> CList;
    ChatAdapter CAdapter;

    VideoView message_video;

    private boolean isRecording = false;
    private MediaRecorder mediaRecorder;
    private File outputFile = null;
    private MediaPlayer mediaPlayer = null;
    private boolean isPlayingAudio = false;
    private Handler seekbarHandlerAudio;
    private Runnable updateSeekbarAudio;
    private boolean resumeAudio = false;
    private long elapsedMillis = 0;
    private SoundPool mSound1, mSound2;
    private final int mMelody = 1;
    private int mPlay;

    public static boolean isText;
    public static boolean isFile;
    public static boolean isImage;
    public static boolean isVideo;
    public static boolean isAudio;

    boolean isPlaying = false;
    long timePaused = 0;
    Handler seekbarHandler = new Handler();
    Runnable updateSeekbar;

    public static String selectedImagePath;
    public static String selectedVideoPath;

    long seekbar_time = 0;

    private static final int PICKFILE_REQUEST_CODE = 10;
    private static final int PICKIMAGE_REQUEST_CODE = 11;
    private static final int CAMERA_REQUEST_CODE = 12;

    long seekbar_video_time = 0;

    private boolean resumeVideo = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mSound1 = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        mSound1.load(this, R.raw.audio_record_sound, 1);
        mSound2 = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        mSound2.load(this, R.raw.audio_record_sound, 1);

        play_button_video = findViewById(R.id.play_button_video);
        playback_timer_video = findViewById(R.id.playback_timer_video);
        seekbar_video = findViewById(R.id.seekbar_video);
        video_length = findViewById(R.id.video_length);

        audio_record = findViewById(R.id.audio_record);
        audio_send_container = findViewById(R.id.audio_send_container);
        audio_pause = findViewById(R.id.audio_pause);
        audio_timer = findViewById(R.id.timer);
        cancel_audio_button = findViewById(R.id.cancel_audio_button);
        send_audio_button = findViewById(R.id.send_audio_button);
        play_button_audio = findViewById(R.id.play_button);
        playback_timer_audio = findViewById(R.id.playback_timer_audio);
        audio_length = findViewById(R.id.audio_length);
        seekbar_audio = findViewById(R.id.seekbar_audio);


        chat_message = findViewById(R.id.message_enter);

        attach_field = findViewById(R.id.attach_field);

        message_video = findViewById(R.id.message_video_view);

        attach_btn = findViewById(R.id.attach_btn);
        send_btn = findViewById(R.id.send_btn);
        chat_view = findViewById(R.id.chat_recycler);

        image_viewer = findViewById(R.id.image_viewer);
        video_viewer = findViewById(R.id.video_viewer);

        image_viewer_back = findViewById(R.id.image_viewer_back);
        video_viewer_back = findViewById(R.id.video_viewer_back);

        file_attach = findViewById(R.id.file_attach);
        gallery_attach = findViewById(R.id.gallery_attach);
        camera_attach = findViewById(R.id.camera_attach);
        record_attach = findViewById(R.id.record_attach);

        image_viewer.setOnClickListener(view -> {
        });
        video_viewer.setOnClickListener(view -> {
        });

        send_btn.setOnClickListener(view -> {
            file = new File(String.valueOf(chat_message.getText()));
            chat_message.setText("");
            CList.add(CList.size(), file);
            CAdapter.notifyItemInserted(CList.size());
            chat_view.smoothScrollToPosition(CList.size());
            isText = true;
            isAudio = false;
            isFile = false;
            isImage = false;
            isVideo = false;
        });

        //TODO send multiple files
        file_attach.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, PICKFILE_REQUEST_CODE);
            }
        });

        gallery_attach.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, PICKIMAGE_REQUEST_CODE);
            }
        });

        camera_attach.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), CameraXActivity.class);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, CAMERA_REQUEST_CODE);
            }
        });

        record_attach.setOnClickListener(view -> {
            audio_record.setVisibility(View.VISIBLE);
            attach_field.setVisibility(View.GONE);
            AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
            mPlay = mSound2.play(mMelody, (float) 0.5, (float) 0.5, 1, 0, 1);
            try {
                Thread.sleep(400);
            } catch (InterruptedException ignored) {
            }
            startRecording();
            isRecording = true;
        });

        audio_record.setOnClickListener(view -> {
        });
        audio_send_container.setOnClickListener(view -> {
        });

        audio_pause.setOnClickListener(view -> {
            AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
            mPlay = mSound1.play(mMelody, (float) 0.3, (float) 0.3, 1, 0, 1);
            stopRecording();
            audio_length.setText(audio_timer.getText());
            audio_record.setVisibility(View.GONE);
            audio_send_container.setVisibility(View.VISIBLE);

            playback_timer_audio.setBase(SystemClock.elapsedRealtime());

            mediaPlayer = MediaPlayer.create(this, Uri.parse(outputFile.getAbsolutePath()));
        });

        play_button_audio.setOnClickListener(view -> {
            if (isPlaying) {
                pauseAudio();
                isPlaying = false;
            } else {
                if (resumeAudio) {
                    resumeAudio();
                } else {
                    resumeAudio(outputFile.getAbsolutePath());
                }

                isPlaying = true;
            }
        });

        seekbar_audio.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    mediaPlayer.seekTo(i);
                    seekbar_audio.setMax(mediaPlayer.getDuration());

//                    int minutes = i / 60;
//                    int seconds = i % 60;
//
//                    // format the minutes and seconds as a string
//                    String time = String.format("%02d:%02d", minutes, seconds);

                    seekbar_time = i;

                    resumeAudio();
                    resumeAudio = true;
                    isPlaying = true;
                    mediaPlayer.setOnCompletionListener(mediaPlayer -> {
                        playback_timer_audio.stop();
                        playback_timer_audio.setBase(SystemClock.elapsedRealtime());
                        play_button_audio.setImageDrawable(getDrawable(R.drawable.play_white));
                        isPlaying = false;
                        resumeAudio = false;
                        seekbar_audio.setProgress(0);
                        mediaPlayer.seekTo(0);
                        mediaPlayer.pause();
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
        cancel_audio_button.setOnClickListener(view -> {
            audio_send_container.setVisibility(View.GONE);
        });

        send_audio_button.setOnClickListener(view -> {
            audio_send_container.setVisibility(View.GONE);
            file = outputFile;
            CList.add(CList.size(), file);
            CAdapter.notifyItemInserted(CList.size());
            chat_view.smoothScrollToPosition(CList.size());
            isImage = false;
            isVideo = false;
            isText = false;
            isAudio = true;
            isFile = false;
        });

        image_viewer_back.setOnClickListener(view -> image_viewer.setVisibility(View.GONE));
        video_viewer_back.setOnClickListener(view -> video_viewer.setVisibility(View.GONE));

        chat_message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!chat_message.getText().toString().trim().isEmpty()) {
                    attach_btn.setVisibility(View.GONE);
                    send_btn.setVisibility(View.VISIBLE);
                    attach_field.setVisibility(View.GONE);
                } else {
                    attach_btn.setVisibility(View.VISIBLE);
                    send_btn.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

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
                    message_video.seekTo(i);
                    seekbar_video.setMax(message_video.getDuration());

                    seekbar_video_time = i;

                    resumeVideo();
                    resumeVideo = true;
                    isPlaying = true;
                    message_video.setOnCompletionListener(mediaPlayer -> {
                        playback_timer_video.stop();
                        playback_timer_video.setBase(SystemClock.elapsedRealtime());
                        play_button_video.setImageDrawable(getDrawable(R.drawable.play_white));
                        isPlaying = false;
                        resumeVideo = false;
                        seekbar_video.setProgress(0);
                        message_video.seekTo(0);
                        message_video.pause();
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

        message_video.setOnCompletionListener(mediaPlayer -> {
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

        attach_btn.setOnClickListener(view -> {
            if (attach_field.getVisibility() == View.VISIBLE) {
                attach_field.setVisibility(View.GONE);
            } else if (attach_field.getVisibility() == View.GONE) {
                attach_field.setVisibility(View.VISIBLE);
            }
        });

        chat_view.setLayoutManager(
                new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        );
        CList = new ArrayList<>();
        CAdapter = new ChatAdapter(CList, this, getApplicationContext());
        chat_view.setAdapter(CAdapter);
    }

    private void startRecording() {
        audio_timer.setBase(SystemClock.elapsedRealtime());
        audio_timer.start();
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        outputFile = CameraXActivity.getOutputMediaFile(CameraXActivity.MEDIA_TYPE_AUDIO);
        mediaRecorder.setOutputFile(outputFile);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(outputFile)));

        try {
            mediaRecorder.prepare();
        } catch (IOException ignored) {
        }

        mediaRecorder.start();
    }

    private void stopRecording() {
        audio_timer.stop();
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
    }

    @Override
    public void onImageClicked(File contact, int position) {
        image_viewer.setVisibility(View.VISIBLE);
        //TODO view image
    }

    @Override
    public void onVideoClicked(File contact, int position) {
        video_viewer.setVisibility(View.VISIBLE);
        message_video.setVideoPath(selectedVideoPath);
        message_video.seekTo(1);

        playback_timer_video.setBase(SystemClock.elapsedRealtime());
        playback_timer_video.start();
        isPlaying = true;
        message_video.start();
        video_length.setText(VideoXActivity.timer_string);
        play_button_video.setImageDrawable(getDrawable(R.drawable.ic_baseline_pause_24));
        seekbar_video.setMax(Integer.parseInt(VideoXActivity.timer_string.substring(3)) * 1000);
        updateRunnableVideo();
        seekbarHandler.postDelayed(updateSeekbar, 0);
        message_video.setOnCompletionListener(mediaPlayer -> {
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
    }

    @Override
    public void onBackPressed() {
        if (image_viewer.getVisibility() == View.VISIBLE) {
            image_viewer.setVisibility(View.GONE);
        } else if (video_viewer.getVisibility() == View.VISIBLE) {
            video_viewer.setVisibility(View.GONE);
            message_video.stopPlayback();
        } else if (audio_record.getVisibility() == View.VISIBLE) {
            stopRecording();
        } else if (audio_send_container.getVisibility() == View.VISIBLE) {
            stopAudio();
        } else {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICKFILE_REQUEST_CODE) {
            if (data != null) {
                file = new File(String.valueOf(data.getData()));
                CList.add(CList.size(), file);
                CAdapter.notifyItemInserted(CList.size());
                chat_view.smoothScrollToPosition(CList.size());
                isText = false;
                isAudio = false;
                isFile = true;
                isImage = false;
                isVideo = false;
            }
        } else if (requestCode == PICKIMAGE_REQUEST_CODE) {
            if (data != null) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                        selectedImagePath = getPathFromUri(selectedImageUri);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                file = new File(String.valueOf(data.getData()));
                CList.add(CList.size(), file);
                CAdapter.notifyItemInserted(CList.size());
                chat_view.smoothScrollToPosition(CList.size());
                isText = false;
                isAudio = false;
                isFile = false;
                isImage = true;
                isVideo = false;
            }
        } else if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                file = new File(String.valueOf(data.getData()));
                CList.add(CList.size(), file);
                CAdapter.notifyItemInserted(CList.size());
                chat_view.smoothScrollToPosition(CList.size());
                isImage = true;
                isVideo = false;
                isText = false;
                isAudio = false;
                isFile = false;

            }
        }
    }

    private String getPathFromUri(Uri contentUri) {
        String filePath;
        Cursor cursor = getContentResolver().query(contentUri,
                null, null, null, null);
        if (cursor == null) {
            filePath = contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex("_data");
            filePath = cursor.getString(index);
            cursor.close();
        }
        return filePath;
    }

    private void stopAudio() {
        mediaPlayer.stop();
        seekbarHandler.removeCallbacks(updateSeekbar);
    }

    private void pauseAudio() {
        mediaPlayer.pause();
        playback_timer_audio.stop();
        elapsedMillis = SystemClock.elapsedRealtime() - playback_timer_audio.getBase();
        seekbarHandler.removeCallbacks(updateSeekbar);
        play_button_audio.setImageDrawable(getDrawable(R.drawable.play_white));
    }

    private void resumeAudio() {
        mediaPlayer.start();
        if (seekbar_time == 0) {
            playback_timer_audio.setBase(SystemClock.elapsedRealtime() - elapsedMillis);
            elapsedMillis = 0;
        } else {
            playback_timer_audio.setBase(SystemClock.elapsedRealtime() - seekbar_time);
            seekbar_time = 0;
        }

        playback_timer_audio.start();
        play_button_audio.setImageDrawable(getDrawable(R.drawable.ic_baseline_pause_24));
        updateRunnable();
        seekbarHandler.postDelayed(updateSeekbar, 0);
    }

    private void resumeAudio(String path) {
        mediaPlayer.start();
        isPlaying = true;
        resumeAudio = true;
        seekbar_audio.setMax(mediaPlayer.getDuration());
        playback_timer_audio.start();
        play_button_audio.setImageDrawable(getDrawable(R.drawable.ic_baseline_pause_24));
        updateRunnable();
        seekbarHandler.postDelayed(updateSeekbar, 0);
        mediaPlayer.setOnCompletionListener(mediaPlayer -> {
            playback_timer_audio.stop();
            playback_timer_audio.setBase(SystemClock.elapsedRealtime());
            play_button_audio.setImageDrawable(getDrawable(R.drawable.play_white));
            isPlaying = false;
            resumeAudio = false;
            seekbar_audio.setProgress(0);
            mediaPlayer.seekTo(0);
            mediaPlayer.pause();
            seekbarHandler.removeCallbacks(updateSeekbar);
        });
    }

    private void playAudio(String path) {
        mediaPlayer = new MediaPlayer();

        isPlaying = true;

        try {
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
        }

        mediaPlayer.setOnCompletionListener(mediaPlayer -> {
            playback_timer_audio.stop();
            playback_timer_audio.setBase(SystemClock.elapsedRealtime());
            play_button_audio.setImageDrawable(getDrawable(R.drawable.play_white));
            isPlaying = false;
            resumeAudio = false;
            seekbar_audio.setProgress(0);
            stopAudio();
        });

        seekbar_audio.setMax(mediaPlayer.getDuration());
        seekbarHandler = new Handler();
        updateRunnable();
        seekbarHandler.postDelayed(updateSeekbar, 0);
    }

    private void updateRunnable() {
        seekbarHandler = new Handler();
        updateSeekbar = new Runnable() {
            @Override
            public void run() {
                seekbar_audio.setProgress(mediaPlayer.getCurrentPosition());
                seekbarHandler.postDelayed(this, 100);
            }
        };
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            stopAudio();
            stopRecording();
        } catch (Exception ignored) {
        }
    }

    private void pauseVideo() {
        message_video.pause();
        playback_timer_video.stop();
        timePaused = SystemClock.elapsedRealtime() - playback_timer_video.getBase();
        seekbarHandler.removeCallbacks(updateSeekbar);
        play_button_video.setImageDrawable(getDrawable(R.drawable.play_white));
    }

    private void resumeVideo() {
        message_video.start();
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
        message_video.start();
        isPlaying = true;
        resumeVideo = true;
        seekbar_video.setMax(message_video.getDuration());
        playback_timer_video.start();
        play_button_video.setImageDrawable(getDrawable(R.drawable.ic_baseline_pause_24));
        playback_timer_video.setBase(SystemClock.elapsedRealtime());
        updateRunnableVideo();
        seekbarHandler.postDelayed(updateSeekbar, 0);
        message_video.setOnCompletionListener(mediaPlayer -> {
            playback_timer_video.stop();
            playback_timer_video.setBase(SystemClock.elapsedRealtime());
            play_button_video.setImageDrawable(getDrawable(R.drawable.play_white));
            isPlaying = false;
            resumeVideo = false;
            seekbar_video.setProgress(0);
            message_video.seekTo(0);
            message_video.pause();
            seekbarHandler.removeCallbacks(updateSeekbar);
        });
    }

    private void updateRunnableVideo() {
        seekbarHandler = new Handler();
        updateSeekbar = new Runnable() {
            @Override
            public void run() {
                seekbar_video.setProgress(message_video.getCurrentPosition());
                seekbarHandler.postDelayed(this, 100);
            }
        };
    }
}