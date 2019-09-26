package com.example.geolocal.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.geolocal.data.model.User;

import java.util.List;

@Dao
public interface UserDao {

    @Query("select * from User")
    List<User> getAll();

    @Query("select * from User  WHERE user_id IN (:userIds)")
    List<User> loadAllByIds(int[] userIds);

    @Query("select * from User  WHERE email = :userEmail LIMIT 1")
    User getUserbyEmail(String userEmail);

    @Query("select * from User  WHERE user_name = :userName")
    List<User> getUserbyUserName(String userName);

    @Insert
    void insertAll(User... users);

    @Delete
    void delete(User user);
}
