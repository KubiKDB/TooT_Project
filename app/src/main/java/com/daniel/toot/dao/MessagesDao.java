package com.daniel.toot.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.daniel.toot.entities.Contacts;
import com.daniel.toot.entities.Messages;

import java.util.List;

@Dao
public interface MessagesDao {
    @Query("SELECT * FROM messages ORDER BY ID DESC ")
    List<Messages> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertStat(Messages messages);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void changeStat(Messages messages);
}
