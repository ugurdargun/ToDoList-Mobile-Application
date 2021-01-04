package com.msu.todolist;

import android.os.Parcel;
import android.os.Parcelable;

public class Category implements Parcelable {

    String name;
    String imgName;

    public Category() {

    }

    public Category(String name, String imgName) {
        this.name = name;
        this.imgName = imgName;
    }

    protected Category(Parcel in) {
        name = in.readString();
        imgName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(imgName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImgName() {
        return imgName;
    }

    public void setImgName(String imgName) {
        this.imgName = imgName;
    }
}
