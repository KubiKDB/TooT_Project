package com.daniel.toot;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.core.VideoCapture;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

public class VideoXActivity extends AppCompatActivity {
    private final int REQUEST_CODE_PERMISSIONS = 1011;
    private final int REQUEST_CODE_STORAGE_PRM = 1111;
    private final String[] REQUIRED_PERMISSIONS = new String[]{
            "android.permission.CAMERA",
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.RECORD_AUDIO"
    };
    private static final int REQUEST_CODE_SELECT_IMAGE = 1;
    private ImageButton
            take_photo_video,
            record_video_video,
            change_camera_video;
    public static boolean change = false;
    private PreviewView mPreviewView;
    boolean isRecording = false;
    ListenableFuture<ProcessCameraProvider> cpf;
    ProcessCameraProvider pcp;
    private VideoCapture videoCapture;
    private Chronometer chronometer;
    public static String timer_string;
    private SoundPool mSound1, mSound2;
    private final int mMelody = 1;
    private int mPlay;

    private Executor getExecutor() {
        return ContextCompat.getMainExecutor(this);
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_record_layout);
        mSound1 = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        mSound1.load(this, R.raw.video_sound, 1);
        mSound2 = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        mSound2.load(this, R.raw.record_sound, 1);
        mPreviewView = findViewById(R.id.preview_video);
        take_photo_video = findViewById(R.id.take_photo_video);
        change_camera_video = findViewById(R.id.change_camera_video);
        record_video_video = findViewById(R.id.record_video_video);
        chronometer = findViewById(R.id.chronometer_video);
        record_video_video.setOnClickListener(view -> {
            if (!isRecording) {
                recordVideo();
            } else {
                AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
                mPlay = mSound1.play(mMelody, (float) 0.3, (float) 0.3, 1, 0, 1);
                chronometer.stop();
                record_video_video.setBackgroundResource(R.drawable.ic_video);
                isRecording = false;
                timer_string = String.valueOf(chronometer.getText());
                videoCapture.stopRecording();
            }
            isRecording = !isRecording;
        });

        change_camera_video.setOnClickListener(view -> {
            if (!change) {
                pcp.unbindAll();

                Preview preview = new Preview.Builder()
                        .build();
                preview.setSurfaceProvider(mPreviewView.getSurfaceProvider());

                videoCapture = new VideoCapture.Builder()
                        .setVideoFrameRate(30)
                        .build();

                CameraSelector selector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                        .build();

                pcp.bindToLifecycle(VideoXActivity.this, selector, preview, videoCapture);
            } else {
                pcp.unbindAll();

                Preview preview = new Preview.Builder()
                        .build();
                preview.setSurfaceProvider(mPreviewView.getSurfaceProvider());

                videoCapture = new VideoCapture.Builder()
                        .setVideoFrameRate(30)
                        .build();

                CameraSelector selector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build();

                pcp.bindToLifecycle(VideoXActivity.this, selector, preview, videoCapture);
            }
            change = !change;
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_PERMISSIONS);
        }

        cpf = ProcessCameraProvider.getInstance(this);
        cpf.addListener(() -> {
            try {
                pcp = cpf.get();
                startCamera();
            } catch (ExecutionException | InterruptedException e) {
            }
        }, ContextCompat.getMainExecutor(this));

        take_photo_video.setOnClickListener(view -> {
            Intent intent = new Intent();
            setResult(RESULT_CANCELED, intent);
            finish();
        });
    }

    @SuppressLint("RestrictedApi")
    private void recordVideo() {
        ContentValues contentValues = new ContentValues();
        record_video_video.setBackgroundResource(R.drawable.ic_video_recording);
        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        mPlay = mSound1.play(mMelody, (float) 0.5, (float) 0.5, 1, 0, 1);
        try {
            Thread.sleep(500);
        } catch (InterruptedException ignored) {}

        if (Build.VERSION.SDK_INT >= 29) {
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH,
                    Environment.DIRECTORY_PICTURES + "/TooT");
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4");
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
        chronometer.setVisibility(View.VISIBLE);
        videoCapture.startRecording(
                new VideoCapture.OutputFileOptions.Builder(
                        getContentResolver(),
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        contentValues
                ).build(),
                getExecutor(),
                new VideoCapture.OnVideoSavedCallback() {
                    @Override
                    public void onVideoSaved(@NonNull VideoCapture.OutputFileResults outputFileResults) {
//                        Toast.makeText(VideoXActivity.this, "Video Saved", Toast.LENGTH_SHORT).show();
                        ChatActivity.selectedVideoPath = getPathFromUri(outputFileResults.getSavedUri());
                        Intent intent = new Intent(getApplicationContext(), SendVideoActivity.class);
                        startActivityForResult(intent, 1001);
                    }

                    @Override
                    public void onError(int videoCaptureError, @NonNull String message, @Nullable Throwable cause) {
//                        Toast.makeText(VideoXActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        chronometer.setOnChronometerTickListener(chronometer -> {
            if (chronometer.getText().toString().equals("00:15")) {
                videoCapture.stopRecording();
                chronometer.stop();
                record_video_video.setBackgroundResource(R.drawable.ic_video);
                isRecording = false;
                timer_string = String.valueOf(chronometer.getText());
                Intent intent = new Intent(getApplicationContext(), SendVideoActivity.class);
                startActivityForResult(intent, 1010);
            }
        });
    }

        @SuppressLint("RestrictedApi")
    private void startCamera() {
        pcp.unbindAll();

        Preview preview = new Preview.Builder()
                .build();
        preview.setSurfaceProvider(mPreviewView.getSurfaceProvider());

        videoCapture = new VideoCapture.Builder()
//                .setVideoFrameRate(30)
//                .setTargetResolution(new Size(2160, 3840))
                .build();

        CameraSelector selector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        pcp.bindToLifecycle(this, selector, preview, videoCapture);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            recordVideo();
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

    private Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    public static final int MEDIA_TYPE_AUDIO = 3;

    public static File getOutputMediaFile(int type) {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "TooT");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_" + timeStamp + ".mp4");
        } else if (type == MEDIA_TYPE_AUDIO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "AUD_" + timeStamp + ".3gp");
        } else {
            return null;
        }
        return mediaFile;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        }
        if (requestCode == 1001 && resultCode == RESULT_CANCELED) {
            Intent intent = new Intent();
            setResult(RESULT_CANCELED, intent);
            recreate();
        }
    }
}