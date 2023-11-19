package com.daniel.toot;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.daniel.toot.database.ContactsDatabase;
import com.naver.android.helloyako.imagecrop.view.ImageCropView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {

    ImageView facebook_icon,
            instagram_icon,
            twitter_icon,
            tiktok_icon,
            telegram_icon;

    TextView facebook_text,
            instagram_text,
            twitter_text,
            tiktok_text,
            telegram_text;

    ConstraintLayout facebook_info,
            instagram_info,
            twitter_info,
            tiktok_info,
            telegram_info;

    ImageView facebook_save,
            instagram_save,
            twitter_save,
            tiktok_save,
            telegram_save;

    EditText facebook_link_et,
            instagram_link_et, instagram_user_et,
            twitter_link_et, twitter_user_et,
            tiktok_link_et, tiktok_user_et,
            telegram_user_et;

    TextView name, bio, account_number;

    ConstraintLayout name_info, bio_info;
    EditText username_et, bio_et;
    ImageView name_save, bio_save;
    ImageView gallery_profile;
    ImageView profile_photo;

    ImageCropView icv;

    ImageView cancel_crop, accept_crop;

    private static final int PICKIMAGE_REQUEST_CODE = 20;
    private String selectedProfileImagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        name = findViewById(R.id.name);
        bio = findViewById(R.id.bio_text);
        account_number = findViewById(R.id.number);
        name_info = findViewById(R.id.name_info);
        bio_info = findViewById(R.id.bio_info);
        username_et = findViewById(R.id.username_et);
        bio_et = findViewById(R.id.bio_et);
        name_save = findViewById(R.id.name_info_save_btn);
        bio_save = findViewById(R.id.bio_info_save_btn);

        icv = findViewById(R.id.imagecrop);
        cancel_crop = findViewById(R.id.cancel_crop_button);
        accept_crop = findViewById(R.id.accept_crop_button);

        gallery_profile = findViewById(R.id.gallery_profile);

        profile_photo = findViewById(R.id.profile_photo);

        facebook_icon = findViewById(R.id.facebook_profile_icon);
        instagram_icon = findViewById(R.id.instagram_profile_icon);
        twitter_icon = findViewById(R.id.twitter_profile_icon);
        tiktok_icon = findViewById(R.id.tiktok_profile_icon);
        telegram_icon = findViewById(R.id.telegram_profile_icon);

        facebook_text = findViewById(R.id.facebook_profile_text);
        instagram_text = findViewById(R.id.instagram_profile_text);
        twitter_text = findViewById(R.id.twitter_profile_text);
        tiktok_text = findViewById(R.id.tiktok_profile_text);
        telegram_text = findViewById(R.id.telegram_profile_text);

        facebook_info = findViewById(R.id.facebook_info);
        instagram_info = findViewById(R.id.instagram_info);
        twitter_info = findViewById(R.id.twitter_info);
        tiktok_info = findViewById(R.id.tiktok_info);
        telegram_info = findViewById(R.id.telegram_info);

        if(MainActivity.contact.getProfile_image() != null){
            try {
                profile_photo.setImageBitmap(BitmapFactory.decodeFile(MainActivity.contact.getProfile_image()));
            } catch (Exception e){
                profile_photo.setImageDrawable(getDrawable(R.drawable.blue_contact_icon));
                Log.e("TAG", "image error");
            }


        }

        gallery_profile.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, PICKIMAGE_REQUEST_CODE);
            }
        });

        facebook_icon.setOnClickListener(view -> {
            facebook_info.setVisibility(View.VISIBLE);
            facebook_link_et.requestFocus();
            facebook_link_et.setFocusableInTouchMode(true);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(facebook_link_et, InputMethodManager.SHOW_FORCED);
            facebook_link_et.post(() -> facebook_link_et.setSelection(facebook_link_et.getText().length()));
        });
        instagram_icon.setOnClickListener(view -> {
            instagram_info.setVisibility(View.VISIBLE);
            instagram_user_et.requestFocus();
            instagram_user_et.setFocusableInTouchMode(true);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(instagram_user_et, InputMethodManager.SHOW_FORCED);
            instagram_user_et.post(() -> instagram_user_et.setSelection(instagram_user_et.getText().length()));
        });
        twitter_icon.setOnClickListener(view -> {
            twitter_info.setVisibility(View.VISIBLE);
            twitter_user_et.requestFocus();
            twitter_user_et.setFocusableInTouchMode(true);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(twitter_user_et, InputMethodManager.SHOW_FORCED);
            twitter_user_et.post(() -> twitter_user_et.setSelection(twitter_user_et.getText().length()));
        });
        tiktok_icon.setOnClickListener(view -> {
            tiktok_info.setVisibility(View.VISIBLE);
            tiktok_user_et.requestFocus();
            tiktok_user_et.setFocusableInTouchMode(true);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(tiktok_user_et, InputMethodManager.SHOW_FORCED);
            tiktok_user_et.post(() -> tiktok_user_et.setSelection(tiktok_user_et.getText().length()));
        });
        telegram_icon.setOnClickListener(view -> {
            telegram_info.setVisibility(View.VISIBLE);
            telegram_user_et.requestFocus();
            telegram_user_et.setFocusableInTouchMode(true);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(telegram_user_et, InputMethodManager.SHOW_FORCED);
            telegram_user_et.post(() -> telegram_user_et.setSelection(telegram_user_et.getText().length()));
        });

        name.setOnClickListener(view -> {
            name_info.setVisibility(View.VISIBLE);
            username_et.requestFocus();
            username_et.setFocusableInTouchMode(true);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(username_et, InputMethodManager.SHOW_FORCED);
            username_et.post(() -> username_et.setSelection(username_et.getText().length()));
        });
        bio.setOnClickListener(view -> {
            bio_info.setVisibility(View.VISIBLE);
            bio_et.requestFocus();
            bio_et.setFocusableInTouchMode(true);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(bio_et, InputMethodManager.SHOW_FORCED);
            bio_et.post(() -> bio_et.setSelection(bio_et.getText().length()));
        });

        facebook_link_et = findViewById(R.id.facebook_link_et);
        instagram_link_et = findViewById(R.id.instagram_link_et);
        twitter_link_et = findViewById(R.id.twitter_link_et);
        tiktok_link_et = findViewById(R.id.tiktok_link_et);

        instagram_user_et = findViewById(R.id.instagram_user_et);
        twitter_user_et = findViewById(R.id.twitter_user_et);
        tiktok_user_et = findViewById(R.id.tiktok_user_et);
        telegram_user_et = findViewById(R.id.telegram_user_et);

        facebook_save = findViewById(R.id.facebook_info_save_btn);
        instagram_save = findViewById(R.id.instagram_info_save_btn);
        twitter_save = findViewById(R.id.twitter_info_save_btn);
        tiktok_save = findViewById(R.id.tiktok_info_save_btn);
        telegram_save = findViewById(R.id.telegram_info_save_btn);


        //TODO link under username
        if (MainActivity.contact.getFacebook_link() != null){
            facebook_text.setText(MainActivity.contact.getFacebook_link());
            facebook_link_et.setText(MainActivity.contact.getFacebook_link());
            facebook_text.setOnClickListener(view -> {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(MainActivity.contact.getFacebook_link()));
                startActivity(i);
            });
        }

        if (MainActivity.contact.getInstagram_username() != null){
            instagram_text.setText(MainActivity.contact.getInstagram_username());
            instagram_link_et.setText(MainActivity.contact.getInstagram_link());
            instagram_user_et.setText(MainActivity.contact.getInstagram_username());
            instagram_text.setOnClickListener(view1 -> {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(MainActivity.contact.getInstagram_link()));
                startActivity(i);
            });
        }

        if (MainActivity.contact.getTwitter_username() != null){
            twitter_text.setText(MainActivity.contact.getTwitter_username());
            twitter_link_et.setText(MainActivity.contact.getTwitter_link());
            twitter_user_et.setText(MainActivity.contact.getTwitter_username());
            twitter_text.setOnClickListener(view1 -> {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(MainActivity.contact.getTwitter_link()));
                startActivity(i);
            });
        }

        if (MainActivity.contact.getTiktok_username() != null){
            tiktok_text.setText(MainActivity.contact.getTiktok_username());
            tiktok_link_et.setText(MainActivity.contact.getTiktok_link());
            tiktok_user_et.setText(MainActivity.contact.getTiktok_username());
            tiktok_text.setOnClickListener(view1 -> {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(MainActivity.contact.getTiktok_link()));
                startActivity(i);
            });
        }

        if (MainActivity.contact.getTelegram_username() != null){
            telegram_text.setText(MainActivity.contact.getTelegram_username());
            telegram_user_et.setText(MainActivity.contact.getTelegram_username());
        }

        if (MainActivity.contact.getNickname() != null){
            name.setText(MainActivity.contact.getNickname());
        }

        if (MainActivity.contact.getBio_text() != null){
            bio.setText(MainActivity.contact.getBio_text());
        }

        facebook_save.setOnClickListener(view -> {
            //TODO save info
            facebook_text.setText(facebook_link_et.getText());
            MainActivity.contact.setFacebook_link(String.valueOf(facebook_link_et.getText()));
            facebook_text.setOnClickListener(view1 -> {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(String.valueOf(facebook_link_et.getText())));
                startActivity(i);
            });
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(facebook_link_et.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
            facebook_info.setVisibility(View.GONE);
            @SuppressLint("StaticFieldLeak")
            class ChangeTask extends AsyncTask<Void, Void, Void> {
                @Override
                protected Void doInBackground(Void... voids) {
                    if (MainActivity.firstCreate){
                        ContactsDatabase.getDatabase(getApplicationContext()).contactsDao().insertStat(MainActivity.contact);
                    } else {
                        ContactsDatabase.getDatabase(getApplicationContext()).contactsDao().changeStat(MainActivity.contact);
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void unused) {
                    super.onPostExecute(unused);
                }
            }
            new ChangeTask().execute();
        });
        instagram_save.setOnClickListener(view -> {
            //TODO save info
            instagram_text.setText(instagram_user_et.getText());
            MainActivity.contact.setInstagram_link(String.valueOf(instagram_link_et.getText()));
            MainActivity.contact.setInstagram_username(String.valueOf(instagram_user_et.getText()));
            instagram_text.setOnClickListener(view1 -> {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(String.valueOf(instagram_link_et.getText())));
                startActivity(i);
            });
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(instagram_user_et.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
            @SuppressLint("StaticFieldLeak")
            class ChangeTask extends AsyncTask<Void, Void, Void> {
                @Override
                protected Void doInBackground(Void... voids) {
                    if (MainActivity.firstCreate){
                        ContactsDatabase.getDatabase(getApplicationContext()).contactsDao().insertStat(MainActivity.contact);
                    } else {
                        ContactsDatabase.getDatabase(getApplicationContext()).contactsDao().changeStat(MainActivity.contact);
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void unused) {
                    super.onPostExecute(unused);
                }
            }
            new ChangeTask().execute();
            instagram_info.setVisibility(View.GONE);
        });
        twitter_save.setOnClickListener(view -> {
            //TODO save info
            twitter_text.setText(twitter_user_et.getText());
            MainActivity.contact.setTwitter_link(String.valueOf(twitter_link_et.getText()));
            MainActivity.contact.setTwitter_username(String.valueOf(twitter_user_et.getText()));
            twitter_text.setOnClickListener(view1 -> {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(String.valueOf(twitter_link_et.getText())));
                startActivity(i);
            });
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(twitter_user_et.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
            @SuppressLint("StaticFieldLeak")
            class ChangeTask extends AsyncTask<Void, Void, Void> {
                @Override
                protected Void doInBackground(Void... voids) {
                    if (MainActivity.firstCreate){
                        ContactsDatabase.getDatabase(getApplicationContext()).contactsDao().insertStat(MainActivity.contact);
                    } else {
                        ContactsDatabase.getDatabase(getApplicationContext()).contactsDao().changeStat(MainActivity.contact);
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void unused) {
                    super.onPostExecute(unused);
                }
            }
            new ChangeTask().execute();
            twitter_info.setVisibility(View.GONE);
        });
        tiktok_save.setOnClickListener(view -> {
            //TODO save info
            tiktok_text.setText(tiktok_user_et.getText());
            MainActivity.contact.setTiktok_link(String.valueOf(tiktok_link_et.getText()));
            MainActivity.contact.setTiktok_username(String.valueOf(tiktok_user_et.getText()));
            tiktok_text.setOnClickListener(view1 -> {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(String.valueOf(tiktok_link_et.getText())));
                startActivity(i);
            });
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(tiktok_user_et.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
            @SuppressLint("StaticFieldLeak")
            class ChangeTask extends AsyncTask<Void, Void, Void> {
                @Override
                protected Void doInBackground(Void... voids) {
                    if (MainActivity.firstCreate){
                        ContactsDatabase.getDatabase(getApplicationContext()).contactsDao().insertStat(MainActivity.contact);
                    } else {
                        ContactsDatabase.getDatabase(getApplicationContext()).contactsDao().changeStat(MainActivity.contact);
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void unused) {
                    super.onPostExecute(unused);
                }
            }
            new ChangeTask().execute();
            tiktok_info.setVisibility(View.GONE);
        });
        telegram_save.setOnClickListener(view -> {
            //TODO save info
            telegram_text.setText(telegram_user_et.getText());
            MainActivity.contact.setTelegram_username(String.valueOf(telegram_user_et.getText()));
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(telegram_user_et.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
            @SuppressLint("StaticFieldLeak")
            class ChangeTask extends AsyncTask<Void, Void, Void> {
                @Override
                protected Void doInBackground(Void... voids) {
                    if (MainActivity.firstCreate){
                        ContactsDatabase.getDatabase(getApplicationContext()).contactsDao().insertStat(MainActivity.contact);
                    } else {
                        ContactsDatabase.getDatabase(getApplicationContext()).contactsDao().changeStat(MainActivity.contact);
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void unused) {
                    super.onPostExecute(unused);
                }
            }
            new ChangeTask().execute();
            telegram_info.setVisibility(View.GONE);
        });

        name_save.setOnClickListener(view -> {
            //TODO save info
            name.setText(username_et.getText());
            MainActivity.contact.setNickname(String.valueOf(username_et.getText()));
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(username_et.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
            @SuppressLint("StaticFieldLeak")
            class ChangeTask extends AsyncTask<Void, Void, Void> {
                @Override
                protected Void doInBackground(Void... voids) {
                    if (MainActivity.firstCreate){
                        ContactsDatabase.getDatabase(getApplicationContext()).contactsDao().insertStat(MainActivity.contact);
                    } else {
                        ContactsDatabase.getDatabase(getApplicationContext()).contactsDao().changeStat(MainActivity.contact);
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void unused) {
                    super.onPostExecute(unused);
                }
            }
            new ChangeTask().execute();
            name_info.setVisibility(View.GONE);
        });

        bio_save.setOnClickListener(view -> {
            //TODO save info
            bio.setText(bio_et.getText());
            MainActivity.contact.setBio_text(String.valueOf(bio_et.getText()));
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(bio_et.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
            @SuppressLint("StaticFieldLeak")
            class ChangeTask extends AsyncTask<Void, Void, Void> {
                @Override
                protected Void doInBackground(Void... voids) {
                    if (MainActivity.firstCreate){
                        ContactsDatabase.getDatabase(getApplicationContext()).contactsDao().insertStat(MainActivity.contact);
                    } else {
                        ContactsDatabase.getDatabase(getApplicationContext()).contactsDao().changeStat(MainActivity.contact);
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void unused) {
                    super.onPostExecute(unused);
                }
            }
            new ChangeTask().execute();
            bio_info.setVisibility(View.GONE);
        });
    }

    @Override
    public void onBackPressed() {
        if (facebook_info.getVisibility() == View.VISIBLE) {
            facebook_info.setVisibility(View.GONE);
            return;
        } else if (instagram_info.getVisibility() == View.VISIBLE) {
            instagram_info.setVisibility(View.GONE);
            return;
        } else if (twitter_info.getVisibility() == View.VISIBLE) {
            twitter_info.setVisibility(View.GONE);
            return;
        } else if (tiktok_info.getVisibility() == View.VISIBLE) {
            tiktok_info.setVisibility(View.GONE);
            return;
        } else if (telegram_info.getVisibility() == View.VISIBLE) {
            telegram_info.setVisibility(View.GONE);
            return;
        } else if (name_info.getVisibility() == View.VISIBLE) {
            name_info.setVisibility(View.GONE);
            return;
        } else if (bio_info.getVisibility() == View.VISIBLE) {
            bio_info.setVisibility(View.GONE);
            return;
        }
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICKIMAGE_REQUEST_CODE) {
            if (data != null) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                        selectedProfileImagePath = getPathFromUri(selectedImageUri);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                Bitmap bitmap = BitmapFactory.decodeFile(selectedProfileImagePath);
                icv.setImageBitmap(bitmap);
                icv.setVisibility(View.VISIBLE);
                cancel_crop.setVisibility(View.VISIBLE);
                accept_crop.setVisibility(View.VISIBLE);
                icv.setGridInnerMode(ImageCropView.GRID_ON);
                icv.setGridOuterMode(ImageCropView.GRID_ON);
                cancel_crop.setOnClickListener(view -> {
                    icv.setVisibility(View.GONE);
                    cancel_crop.setVisibility(View.GONE);
                    accept_crop.setVisibility(View.GONE);
                });

                accept_crop.setOnClickListener(view -> {
                    icv.setVisibility(View.GONE);
                    cancel_crop.setVisibility(View.GONE);
                    accept_crop.setVisibility(View.GONE);
                    Bitmap cropped_bitmap = icv.getCroppedImage();
                    profile_photo.setImageBitmap(cropped_bitmap);

                    String file_path = "";

                    try {
                        String path = "";
                        OutputStream fOut = null;
                        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1) {
                            path = this.getExternalFilesDir(Environment.DIRECTORY_DCIM) + "/" + timeStamp + ".jpeg";
                        }
                        else
                        {
                            path = Environment.getExternalStorageDirectory().toString() + "/" + timeStamp + ".jpeg";
                        }
                        File file = new File(path/*, "IMG_"+timeStamp+".jpg"*/);
                        fOut = new FileOutputStream(file);

                        cropped_bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
                        fOut.flush(); // Not really required
                        fOut.close(); // do not forget to close the stream


                        file_path = file.getAbsolutePath();
                        MediaStore.Images.Media.insertImage(getContentResolver(),file.getAbsolutePath(),file.getName(),file.getName());
                    } catch (Exception ignored){
                        ignored.printStackTrace();
                    }


                    MainActivity.contact.setProfile_image(file_path);
                    @SuppressLint("StaticFieldLeak")
                    class ChangeTask extends AsyncTask<Void, Void, Void> {
                        @Override
                        protected Void doInBackground(Void... voids) {
                            if (MainActivity.firstCreate){
                                ContactsDatabase.getDatabase(getApplicationContext()).contactsDao().insertStat(MainActivity.contact);
                            } else {
                                ContactsDatabase.getDatabase(getApplicationContext()).contactsDao().changeStat(MainActivity.contact);
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void unused) {
                            super.onPostExecute(unused);
                        }
                    }
                    new ChangeTask().execute();
                });
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
}