package com.example.otp;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;



@Dao
public interface ContactDao {
    @Query("SELECT * FROM contact")
    List<Contact> getAll();

    @Insert
    void insert(Contact contact);

    @Delete
    void delete(Contact contact);

    @Update
    void update(Contact contact);

}
