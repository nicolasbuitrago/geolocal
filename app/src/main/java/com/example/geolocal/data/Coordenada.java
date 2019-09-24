package com.example.geolocal.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(indices = {@Index("user_id")},
        foreignKeys = @ForeignKey(entity = User.class,
        parentColumns = "user_id",
        childColumns = "user_id",
        onDelete = ForeignKey.CASCADE))
public class Coordenada {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "coordenada_id")
    public int coordenadaId;

    @ColumnInfo(name = "date")
    public Date date;

    @ColumnInfo(name = "user_id")
    public int userId;

    @ColumnInfo(name = "longitud")
    public double longitud;

    @ColumnInfo(name = "latitud")
    public double latitud;

}
