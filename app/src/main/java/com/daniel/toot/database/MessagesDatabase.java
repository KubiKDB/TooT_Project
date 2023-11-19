package com.daniel.toot.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.daniel.toot.dao.ContactsDao;
import com.daniel.toot.dao.MessagesDao;
import com.daniel.toot.entities.Contacts;
import com.daniel.toot.entities.Messages;

@Database(entities = Messages.class, version = 1, exportSchema = false)
public abstract class MessagesDatabase extends RoomDatabase {

    private static MessagesDatabase messagesDatabase;

    public static synchronized MessagesDatabase getDatabase(Context context){
        if (messagesDatabase == null){
            messagesDatabase = Room.databaseBuilder(
                    context,
                    MessagesDatabase.class,
                    "messages_db"
            ).build();
        }
        return messagesDatabase;
    }

    public abstract MessagesDao messagesDao();

}
