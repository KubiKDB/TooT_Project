package com.daniel.toot;

import java.io.File;

public interface ChatListener {
    void onImageClicked(File contact, int position);
    void onVideoClicked(File contact, int position);
}
