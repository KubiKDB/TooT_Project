package com.daniel.toot.entities;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.List;

@Entity(tableName = "contacts")
public class Contacts implements Serializable{
    //TODO entity

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "account_number")
    private String account_number;

    @ColumnInfo(name = "nickname")
    private String nickname;

    @ColumnInfo(name = "profile_image")
    private String profile_image;

    @ColumnInfo(name = "bio_text")
    private String bio_text;

    @ColumnInfo(name = "facebook_link")
    private String facebook_link;

    @ColumnInfo(name = "instagram_username")
    private String instagram_username;

    @ColumnInfo(name = "instagram_link")
    private String instagram_link;

    @ColumnInfo(name = "twitter_username")
    private String twitter_username;

    @ColumnInfo(name = "twitter_link")
    private String twitter_link;

    @ColumnInfo(name = "tiktok_username")
    private String tiktok_username;

    @ColumnInfo(name = "tiktok_link")
    private String tiktok_link;

    @ColumnInfo(name = "telegram_username")
    private String telegram_username;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAccount_number() {
        return account_number;
    }

    public void setAccount_number(String account_number) {
        this.account_number = account_number;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getBio_text() {
        return bio_text;
    }

    public void setBio_text(String bio_text) {
        this.bio_text = bio_text;
    }

    public String getFacebook_link() {
        return facebook_link;
    }

    public void setFacebook_link(String facebook_link) {
        this.facebook_link = facebook_link;
    }

    public String getInstagram_username() {
        return instagram_username;
    }

    public void setInstagram_username(String instagram_username) {
        this.instagram_username = instagram_username;
    }

    public String getInstagram_link() {
        return instagram_link;
    }

    public void setInstagram_link(String instagram_link) {
        this.instagram_link = instagram_link;
    }

    public String getTwitter_username() {
        return twitter_username;
    }

    public void setTwitter_username(String twitter_username) {
        this.twitter_username = twitter_username;
    }

    public String getTwitter_link() {
        return twitter_link;
    }

    public void setTwitter_link(String twitter_link) {
        this.twitter_link = twitter_link;
    }

    public String getTiktok_username() {
        return tiktok_username;
    }

    public void setTiktok_username(String tiktok_username) {
        this.tiktok_username = tiktok_username;
    }

    public String getTiktok_link() {
        return tiktok_link;
    }

    public void setTiktok_link(String tiktok_link) {
        this.tiktok_link = tiktok_link;
    }

    public String getTelegram_username() {
        return telegram_username;
    }

    public void setTelegram_username(String telegram_username) {
        this.telegram_username = telegram_username;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }
}
