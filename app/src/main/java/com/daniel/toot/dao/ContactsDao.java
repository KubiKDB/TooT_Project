package com.daniel.toot.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.daniel.toot.entities.Contacts;

import java.util.List;

@Dao
public interface ContactsDao {
    @Query("SELECT * FROM contacts ORDER BY ID DESC ")
    List<Contacts> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertStat(Contacts contacts);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void changeStat(Contacts contacts);
}
