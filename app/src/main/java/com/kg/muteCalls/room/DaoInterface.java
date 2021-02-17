package com.kg.muteCalls.room;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;


@Dao
public interface DaoInterface {

    @Insert
    void insert(Contact contact);

    @Delete
    void delete(Contact contact);

    @Query("SELECT * FROM Contact")
    LiveData<List<Contact>> getAllContact();

    @Query("DELETE FROM Contact")
    void deleteAllContact();



    @Query("SELECT * FROM Contact")
    List<Contact> getContact();



}
