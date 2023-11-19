package com.daniel.toot.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.daniel.toot.dao.ContactsDao;
import com.daniel.toot.entities.Contacts;

@Database(entities = Contacts.class, version = 2, exportSchema = false)
public abstract class ContactsDatabase extends RoomDatabase {

    private static ContactsDatabase contactsDatabase;

    public static synchronized ContactsDatabase getDatabase(Context context){
        if (contactsDatabase == null){
            contactsDatabase = Room.databaseBuilder(
                    context,
                    ContactsDatabase.class,
                    "stats_db"
            ).build();
        }
        return contactsDatabase;
    }

    public abstract ContactsDao contactsDao();

}
