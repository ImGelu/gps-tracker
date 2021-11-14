package edu.utcluj.gpstrack.client.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface UserDao {
    @Query("SELECT * FROM User WHERE email = :mail AND password = :password")
    User getUser(String mail, String password);

    @Query("SELECT * FROM User WHERE email = :mail")
    User getUserByEmail(String mail);

    @Insert
    long insert(User user);
}