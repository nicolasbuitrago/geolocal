package com.example.geolocal.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(indices = {@Index("user_id")},
        foreignKeys = @ForeignKey(entity = User.class,
        parentColumns = "user_id",
        childColumns = "user_id",
        onDelete = ForeignKey.CASCADE))
public class Coordenada implements Parcelable {

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

    @Ignore
    public Coordenada(int userId, Date date, double longitud, double latitud) {
        this.userId = userId;
        this.date = date;
        this.longitud = longitud;
        this.latitud = latitud;
    }

    public Coordenada() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(coordenadaId);
        parcel.writeInt(userId);
        parcel.writeSerializable(date);
        parcel.writeDouble(longitud);
        parcel.writeDouble(latitud);
    }

    // Creator
    public static final Parcelable.Creator CREATOR
            = new Parcelable.Creator() {
        public Coordenada createFromParcel(Parcel parcel) {
            Coordenada c = new Coordenada();
            c.coordenadaId = parcel.readInt();
            c.userId = parcel.readInt();
            c.date = (Date) parcel.readSerializable();
            c.longitud = parcel.readDouble();
            c.latitud = parcel.readDouble();
            return c;
        }

        public Coordenada[] newArray(int size) {
            return new Coordenada[size];
        }
    };

}
