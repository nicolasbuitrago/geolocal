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
public class Message implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "message_id")
    public int messageId;

    @ColumnInfo(name = "date")
    public Date date;

    @ColumnInfo(name = "user_id")
    public int userId;

    @ColumnInfo(name = "message")
    public String message;

    @Ignore
    public Message(Date date, int userId, String message) {
        this.date = date;
        this.userId = userId;
        this.message = message;
    }

    public Message() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(messageId);
        parcel.writeSerializable(date);
        parcel.writeInt(userId);
        parcel.writeString(message);
    }

    // Creator
    public static final Parcelable.Creator CREATOR
            = new Parcelable.Creator() {
        public Message createFromParcel(Parcel parcel) {
            Message m = new Message();
            m.messageId = parcel.readInt();
            m.date = (Date) parcel.readSerializable();
            m.userId = parcel.readInt();
            m.message = parcel.readString();
            return m;
        }

        public Message[] newArray(int size) {
            return new Message[size];
        }
    };
}
