package com.daniel.toot;

import static com.daniel.toot.ChatActivity.selectedImagePath;
import static com.daniel.toot.ChatActivity.selectedVideoPath;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.daniel.toot.entities.Contacts;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    private List<File> files;
    private ChatListener chatListener;
    private Context context;
    private String dataType;
    public static float video_width;
    public static float video_height;
    boolean isAudioPlaying = false;
    boolean resumeAudio = false;
    long seekbar_time = 0;
    long elapsedMillis = 0;
    private MediaPlayer mediaPlayer = null;
    private Handler seekbarHandlerAudio = new Handler();
    private Runnable updateSeekbarAudio;

    public ChatAdapter(List<File> files, ChatListener chatListener, Context context) {
        this.files = files;
        this.chatListener = chatListener;
        this.context = context;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChatViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.message_item,
                        parent,
                        false
                )
        );
    }

    @SuppressLint({"ClickableViewAccessibility", "UseCompatLoadingForDrawables"})
    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        //TODO date time
        Date date = new Date();
        final SimpleDateFormat dtf = new SimpleDateFormat("HH:mm | MMM dd yyyy", Locale.ENGLISH);
        String messageDay = dtf.format(date);
        holder.date_time.setText(messageDay);
//        holder.message_text.setVisibility(View.GONE);
//        holder.message_photo.setVisibility(View.VISIBLE);
//        holder.audio_container.setVisibility(View.VISIBLE);
//        holder.message_video.setVisibility(View.VISIBLE);
//        holder.file_container.setVisibility(View.VISIBLE);


        if (ChatActivity.isFile && files.get(position) != null) {
            holder.message_text.setVisibility(View.GONE);
            holder.file_container.setVisibility(View.VISIBLE);
            holder.message_photo.setVisibility(View.GONE);
            holder.message_video.setVisibility(View.GONE);
            holder.audio_container.setVisibility(View.GONE);
            holder.play_triangle.setVisibility(View.GONE);
//            holder.chat_layout.setBackground(context.getDrawable(R.drawable.rectangle_round_stroke));
            holder.file_name.setText(files.get(holder.getAdapterPosition()).getName());
            //TODO file size
//            File file = new File(getFilePathFromUri(context, Uri.fromFile(files.get(position))));
//            try{
//                long fileSizeInBytes = file.length();
//                double fileSizeInKB = fileSizeInBytes / 1024.0;
//                double fileSizeInMB = fileSizeInKB / 1024.0;
//                holder.file_size.setText(fileSizeInMB + "MB");
//            } catch (Exception e){
//                e.printStackTrace();
//                Log.e("File error", e.getLocalizedMessage());
//            }
        } else if (ChatActivity.isImage && files.get(holder.getAdapterPosition()) != null) {
            holder.message_text.setVisibility(View.GONE);
            holder.file_container.setVisibility(View.GONE);
            holder.message_photo.setVisibility(View.VISIBLE);
            holder.message_video.setVisibility(View.GONE);
            holder.audio_container.setVisibility(View.GONE);

            if (SendVideoActivity.isSendVideo) {
                holder.message_video.setVisibility(View.VISIBLE);
                holder.play_triangle.setVisibility(View.VISIBLE);
                File file = new File(selectedVideoPath);
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                try {
                    retriever.setDataSource(file.getAbsolutePath());
                    holder.message_video.setImageBitmap(retriever.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST));
                } catch (IllegalArgumentException ex) {
                    ex.printStackTrace();
                } catch (RuntimeException ex) {
                    ex.printStackTrace();
                } finally {
                    try {
                        retriever.release();
                    } catch (RuntimeException ex) {

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                holder.chat_layout.setBackground(context.getDrawable(R.drawable.rectangle_round));
                holder.message_photo.setImageBitmap(BitmapFactory.decodeFile(selectedImagePath));
            }

        } else if (ChatActivity.isText && files.get(holder.getAdapterPosition()) != null) {
            holder.message_text.setVisibility(View.VISIBLE);
            holder.file_container.setVisibility(View.GONE);
            holder.message_photo.setVisibility(View.GONE);
            holder.message_video.setVisibility(View.GONE);
            holder.audio_container.setVisibility(View.GONE);
            holder.play_triangle.setVisibility(View.GONE);
            holder.message_text.setText(files.get(holder.getAdapterPosition()).getPath());
        } else if (ChatActivity.isAudio && files.get(holder.getAdapterPosition()) != null) {
            holder.message_text.setVisibility(View.GONE);
            holder.file_container.setVisibility(View.GONE);
            holder.message_photo.setVisibility(View.GONE);
            holder.message_video.setVisibility(View.GONE);
            holder.audio_container.setVisibility(View.VISIBLE);
            holder.play_triangle.setVisibility(View.GONE);
            mediaPlayer = MediaPlayer.create(context, Uri.parse(files.get(position).getAbsolutePath()));
        }

        holder.play_button_audio.setOnClickListener(view -> {
            if (isAudioPlaying) {
                mediaPlayer.pause();
                holder.audio_timer.stop();
                elapsedMillis = SystemClock.elapsedRealtime() - holder.audio_timer.getBase();
                seekbarHandlerAudio.removeCallbacks(updateSeekbarAudio);
                holder.play_button_audio.setImageDrawable(context.getDrawable(R.drawable.play_white));
                isAudioPlaying = false;
            } else {
                if (resumeAudio) {
                    mediaPlayer.start();
                    if (seekbar_time == 0) {
                        holder.audio_timer.setBase(SystemClock.elapsedRealtime() - elapsedMillis);
                    } else {
                        holder.audio_timer.setBase(SystemClock.elapsedRealtime() - seekbar_time);
                        seekbar_time = 0;
                    }

                    holder.audio_timer.start();
                    holder.play_button_audio.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_pause_24));
                    seekbarHandlerAudio = new Handler();
                    updateSeekbarAudio = new Runnable() {
                        @Override
                        public void run() {
                            holder.seekbar_audio.setProgress(mediaPlayer.getCurrentPosition());
                            seekbarHandlerAudio.postDelayed(this, 100);
                        }
                    };
                    seekbarHandlerAudio.postDelayed(updateSeekbarAudio, 0);
                } else {
                    mediaPlayer.start();
                    isAudioPlaying = true;
                    resumeAudio = true;
                    holder.seekbar_audio.setMax(mediaPlayer.getDuration());
                    holder.audio_timer.start();
                    holder.play_button_audio.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_pause_24));
                    seekbarHandlerAudio = new Handler();
                    updateSeekbarAudio = new Runnable() {
                        @Override
                        public void run() {
                            holder.seekbar_audio.setProgress(mediaPlayer.getCurrentPosition());
                            seekbarHandlerAudio.postDelayed(this, 100);
                        }
                    };
                    seekbarHandlerAudio.postDelayed(updateSeekbarAudio, 0);
                    mediaPlayer.setOnCompletionListener(mediaPlayer -> {
                        holder.audio_timer.stop();
                        holder.audio_timer.setBase(SystemClock.elapsedRealtime());
                        holder.play_button_audio.setImageDrawable(context.getDrawable(R.drawable.play_white));
                        isAudioPlaying = false;
                        resumeAudio = false;
                        holder.seekbar_audio.setProgress(0);
                        mediaPlayer.seekTo(0);
                        mediaPlayer.pause();
                        seekbarHandlerAudio.removeCallbacks(updateSeekbarAudio);
                    });
                }
                isAudioPlaying = true;
            }
        });

        holder.seekbar_audio.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    mediaPlayer.seekTo(i);
                    holder.seekbar_audio.setMax(mediaPlayer.getDuration());

//                    int minutes = i / 60;
//                    int seconds = i % 60;
//
//                    // format the minutes and seconds as a string
//                    String time = String.format("%02d:%02d", minutes, seconds);

                    seekbar_time = i;

                    mediaPlayer.start();

                    holder.audio_timer.setBase(SystemClock.elapsedRealtime() - i);

                    holder.audio_timer.start();
                    holder.play_button_audio.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_pause_24));
                    seekbarHandlerAudio = new Handler();
                    updateSeekbarAudio = new Runnable() {
                        @Override
                        public void run() {
                            holder.seekbar_audio.setProgress(mediaPlayer.getCurrentPosition());
                            seekbarHandlerAudio.postDelayed(this, 100);
                        }
                    };
                    seekbarHandlerAudio.postDelayed(updateSeekbarAudio, 0);
                    resumeAudio = true;
                    isAudioPlaying = true;
                    mediaPlayer.setOnCompletionListener(mediaPlayer -> {
                        holder.audio_timer.stop();
                        holder.audio_timer.setBase(SystemClock.elapsedRealtime());
                        holder.play_button_audio.setImageDrawable(context.getDrawable(R.drawable.play_white));
                        isAudioPlaying = false;
                        resumeAudio = false;
                        holder.seekbar_audio.setProgress(0);
                        mediaPlayer.seekTo(0);
                        mediaPlayer.pause();
                        seekbarHandlerAudio.removeCallbacks(updateSeekbarAudio);
                    });
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                seekbarHandlerAudio.removeCallbacks(updateSeekbarAudio);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekbarHandlerAudio.postDelayed(updateSeekbarAudio, 1000);
            }
        });

        holder.message_photo.setOnClickListener(view -> chatListener.onImageClicked(files.get(position), position));
        holder.message_video.setOnClickListener(view -> chatListener.onVideoClicked(files.get(position), position));
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        //TODO items
        TextView date_time;
        ImageView message_photo;
        TextView message_text;
        ImageView message_video;
        ImageView play_triangle;
        ConstraintLayout audio_container;
        ConstraintLayout file_container;
        TextView file_name, file_size;
        ConstraintLayout chat_layout;
        ImageView play_button_audio;
        TextView audio_length;
        Chronometer audio_timer;
        SeekBar seekbar_audio;

        ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            //TODO
            date_time = itemView.findViewById(R.id.date);
            message_photo = itemView.findViewById(R.id.message_photo);
            message_text = itemView.findViewById(R.id.message_text);
            audio_container = itemView.findViewById(R.id.audio_container);
            file_container = itemView.findViewById(R.id.file_container);
            message_video = itemView.findViewById(R.id.message_video);
            file_name = itemView.findViewById(R.id.file_name);
            file_size = itemView.findViewById(R.id.file_size);
            chat_layout = itemView.findViewById(R.id.chat_text_item);
            play_triangle = itemView.findViewById(R.id.play_triangle);
            play_button_audio = itemView.findViewById(R.id.play_button);
            audio_length = itemView.findViewById(R.id.audio_length);
            audio_timer = itemView.findViewById(R.id.playback_timer);
            seekbar_audio = itemView.findViewById(R.id.seekbar_audio);
            // profile_pic = itemView.findViewById(R.id.profile_pic);
        }

        void setContact(Contacts contact) {
            //TODO set contact
        }
    }
    public String getFilePathFromUri(Context context, Uri uri) {
        if (uri == null) {
            return null;
        }
        String filePath = null;
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    filePath = cursor.getString(columnIndex);
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        } else if (ContentResolver.SCHEME_FILE.equals(uri.getScheme())) {
            filePath = uri.getPath();
        }
        return filePath;
    }
}
