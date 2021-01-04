package com.msu.todolist;

import android.os.Parcel;
import android.os.Parcelable;

public class Task implements Parcelable {

    int id;
    String category;
    String name;
    String details;
    String date;
    String time;
    int favorite;
    int status;

    public Task() {

    }

    public Task(int id, String category, String name, String details, String date, String time, int favorite, int status) {
        this.id = id;
        this.category = category;
        this.name = name;
        this.details = details;
        this.date = date;
        this.time = time;
        this.favorite = favorite;
        this.status = status;
    }

    protected Task(Parcel in) {
        id = in.readInt();
        category = in.readString();
        name = in.readString();
        details = in.readString();
        date = in.readString();
        time = in.readString();
        favorite = in.readInt();
        status = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(category);
        dest.writeString(name);
        dest.writeString(details);
        dest.writeString(date);
        dest.writeString(time);
        dest.writeInt(favorite);
        dest.writeInt(status);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Task> CREATOR = new Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getFavorite() {
        return favorite;
    }

    public void setFavorite(int favorite) {
        this.favorite = favorite;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
