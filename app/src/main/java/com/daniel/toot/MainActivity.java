package com.daniel.toot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.daniel.toot.database.ContactsDatabase;
import com.daniel.toot.database.MessagesDatabase;
import com.daniel.toot.entities.Contacts;
import com.daniel.toot.entities.Messages;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ContactsListener{
    private static final int PERMISSION_CODE = 1;
    RecyclerView contact_view;
    List<Contacts> CList;
    ContactsAdapter CAdapter;

    ImageView profile_pic;

    public static List<Messages> message_list;

    public static Contacts contact;

    public static boolean firstCreate;

    private final String[] permissions = {
            android.Manifest.permission.RECORD_AUDIO,
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.CAMERA"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this, permissions, PERMISSION_CODE);

        profile_pic = findViewById(R.id.profile_pic);
        contact_view = findViewById(R.id.contact_view);

        @SuppressLint("StaticFieldLeak")
        class GetTask extends AsyncTask<Void, Void, List<Contacts>> {
            @Override
            protected List<Contacts> doInBackground(Void... voids) {
                return ContactsDatabase.getDatabase(getApplicationContext()).contactsDao().getAll();
            }

            @Override
            protected void onPostExecute(List<Contacts> contacts_arr) {
                super.onPostExecute(contacts_arr);
                if (!contacts_arr.isEmpty()) {
                    contact = contacts_arr.get(0);
                    firstCreate = false;
                } else {
                    contact = new Contacts();
                    firstCreate = true;
                }
            }
        }

        new GetTask().execute();

        @SuppressLint("StaticFieldLeak")
        class GetMessagesTask extends AsyncTask<Void, Void, List<Messages>> {
            @Override
            protected List<Messages> doInBackground(Void... voids) {
                return MessagesDatabase.getDatabase(getApplicationContext()).messagesDao().getAll();
            }

            @Override
            protected void onPostExecute(List<Messages> message_arr) {
                super.onPostExecute(message_arr);
                if (!message_arr.isEmpty()) {
                    message_list = message_arr;
                } else {
                    message_list = new ArrayList<>();
                }
            }
        }

        new GetMessagesTask().execute();

        profile_pic.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
            startActivity(intent);
        });

        contact_view.setLayoutManager(
                new StaggeredGridLayoutManager(5, StaggeredGridLayoutManager.VERTICAL)
        );
        CList = new ArrayList<>();
        CAdapter = new ContactsAdapter(CList, this, getApplicationContext());
        contact_view.setAdapter(CAdapter);
        for (int i = 0; i < 10; i++) {
            CList.add(0, new Contacts());
            CAdapter.notifyItemInserted(0);
            contact_view.smoothScrollToPosition(0);
        }
    }

    @Override
    public void onContactClicked(Contacts contact, int position) {
        //TODO onClick
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        //TODO bundle with info
        startActivity(intent);
    }


//    // Function to check and request permission
//    public void checkPermission(String permission, int requestCode)
//    {
//        // Checking if permission is not granted
//        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_DENIED) {
//            ActivityCompat.requestPermissions(MainActivity.this, new String[] { permission }, requestCode);
//        }
//    }
//
//    // This function is called when user accept or decline the permission.
//// Request Code is used to check which permission called this function.
//// This request code is provided when user is prompt for permission.
//    @Override
//    public void onRequestPermissionsResult(int requestCode,
//                                           @NonNull String[] permissions,
//                                           @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }

}