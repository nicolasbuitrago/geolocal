package com.example.geolocal.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.geolocal.data.model.Message;

import java.util.Date;
import java.util.List;

@Dao
public interface MessageDao {
    @Query("select * from Message")
    List<Message> getAll();

    @Query("select * from Message  WHERE user_id IN (:userIds)")
    List<Message> loadAllByIds(int[] userIds);

    @Query("SELECT * FROM Message WHERE date BETWEEN :from AND :to")
    List<Message> findMessageBetweenDates(Date from, Date to);

    @Query("select * from Message  WHERE date = :date")
    List<Message> getMessagebyDate(Date date);

    @Query("select * from Message  WHERE (user_id = :userId) AND (date BETWEEN :from AND :to)")
    List<Message> findMessageBetweenDates(int userId, Date from, Date to);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(Message Message);

    @Insert
    void insertAll(Message... Messages);

    @Delete
    void delete(Message Message);
}
