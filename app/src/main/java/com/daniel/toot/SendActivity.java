package com.daniel.toot;

import static com.daniel.toot.ChatActivity.selectedImagePath;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

public class SendActivity extends AppCompatActivity {
    ImageView send_image, send_reject, send_accept;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);
        send_image = findViewById(R.id.request_send_photo);
        send_reject = findViewById(R.id.send_reject);
        send_accept = findViewById(R.id.send_accept);

        send_image.setImageBitmap(BitmapFactory.decodeFile(selectedImagePath));
        send_accept.setOnClickListener(view -> {
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        });
        send_reject.setOnClickListener(view -> {
            finish();
        });

    }
}