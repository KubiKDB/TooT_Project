package com.daniel.toot.entities;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "messages")
public class Messages implements Serializable{
    //TODO entity

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "account_number")
    private String account_number;

    @ColumnInfo(name = "message_type")
    private String message_type;

    @ColumnInfo(name = "text")
    private String text;

    @ColumnInfo(name = "path")
    private String path;

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

    public String getMessage_type() {
        return message_type;
    }

    public void setMessage_type(String message_type) {
        this.message_type = message_type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
