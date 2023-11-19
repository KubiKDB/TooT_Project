package com.daniel.toot;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Size;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
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

public class CameraXActivity extends AppCompatActivity {
    private final int REQUEST_CODE_PERMISSIONS = 1011;
    private final int REQUEST_CODE_STORAGE_PRM = 1111;
    private final String[] REQUIRED_PERMISSIONS = new String[]{
            "android.permission.CAMERA",
            "android.permission.WRITE_EXTERNAL_STORAGE"};
    private static final int REQUEST_CODE_SELECT_IMAGE = 1;
    private ImageButton
            take_photo,
            record_video,
            change_camera;
    public static boolean change = false;
    private PreviewView mPreviewView;
    ImageCapture imageCapture;
    ListenableFuture<ProcessCameraProvider> cpf;
    ProcessCameraProvider pcp;

    private Executor getExecutor() {
        return ContextCompat.getMainExecutor(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_capture_layout);
        mPreviewView = findViewById(R.id.preview);
        take_photo = findViewById(R.id.take_photo);
        change_camera = findViewById(R.id.change_camera);
        record_video = findViewById(R.id.record_video);
        record_video.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), VideoXActivity.class);
            startActivityForResult(intent, 1001);
        });

        change_camera.setOnClickListener(view -> {
            if (!change) {
                pcp.unbindAll();

                Preview preview = new Preview.Builder()
                        .build();
                preview.setSurfaceProvider(mPreviewView.getSurfaceProvider());

                imageCapture = new ImageCapture.Builder()
                        .setTargetResolution(new Size(1080, 1920))
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .build();

                CameraSelector selector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                        .build();

                pcp.bindToLifecycle(CameraXActivity.this, selector, preview, imageCapture);
            } else {
                pcp.unbindAll();

                Preview preview = new Preview.Builder()
                        .build();
                preview.setSurfaceProvider(mPreviewView.getSurfaceProvider());

                imageCapture = new ImageCapture.Builder()
                        .setTargetResolution(new Size(2160, 3840))
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .build();

                CameraSelector selector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build();

                pcp.bindToLifecycle(CameraXActivity.this, selector, preview, imageCapture);
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

        take_photo.setOnClickListener(view -> {
            capturePhoto();
        });
    }

    private void capturePhoto() {
        ContentValues contentValues = new ContentValues();
        File dir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "TooT");

        if (Build.VERSION.SDK_INT >= 29) {
            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH,
                    Environment.DIRECTORY_PICTURES + "/TooT");
        }

        imageCapture.takePicture(
                new ImageCapture.OutputFileOptions.Builder(
                        getContentResolver(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        contentValues
                ).build(),
                getExecutor(),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        ChatActivity.selectedImagePath = getPathFromUri(outputFileResults.getSavedUri());
//                        Toast.makeText(getApplicationContext(), "Photo saved", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), SendActivity.class);
                        startActivityForResult(intent, 1001);
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
//                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void startCamera() {
        pcp.unbindAll();
        Preview preview = new Preview.Builder()
//                        .setTargetResolution(new Size(1920,1080))
                .build();
        preview.setSurfaceProvider(mPreviewView.getSurfaceProvider());

        imageCapture = new ImageCapture.Builder()
                .setTargetResolution(new Size(2160, 3840))
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build();

        CameraSelector selector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        try {
            pcp.bindToLifecycle(this, selector, preview, imageCapture);
        } catch (Exception e){}
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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
