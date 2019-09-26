package com.example.geolocal.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.geolocal.data.model.Coordenada;

import java.util.Date;
import java.util.List;

@Dao
public interface CoordenadaDao {

    @Query("select * from Coordenada")
    List<Coordenada> getAll();

    @Query("select * from Coordenada  WHERE user_id IN (:userIds)")
    List<Coordenada> loadAllByIds(int[] userIds);

    @Query("SELECT * FROM Coordenada WHERE date BETWEEN :from AND :to")
    List<Coordenada> findCoordenadaBetweenDates(Date from, Date to);

    @Query("select * from Coordenada  WHERE date = :date")
    List<Coordenada> getCoordenadabyDate(Date date);

    @Query("select * from Coordenada  WHERE user_id = :userId AND date BETWEEN :from AND :to")
    List<Coordenada> findCoordenadaBetweenDates(int userId, Date from, Date to);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(Coordenada coordenada);

    @Insert
    void insertAll(Coordenada... coordenadas);

    @Delete
    void delete(Coordenada coordenada);
}
