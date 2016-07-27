package com.tbvanderleystudios.wataugahumanesociety;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class Animal implements Parcelable {
    private String mName;
    private String mBitmapImageUrl;
    private String mStatus;
    private String mGender;
    private String mAge;
    private String mBreed;

    private String mScrapedURLAddress;
    private String mHousetrained;
    private String mDeclawed;
    private String mDescription;

    public Animal(String name, String bitmapImageUrl, String status,
                  String gender, String age, String breed, String scrapedURLAddress) {
        mName = name;
        mBitmapImageUrl = bitmapImageUrl;
        mStatus = status;
        mGender = gender;
        mAge = age;
        mBreed = breed;
        mScrapedURLAddress = scrapedURLAddress;
        mHousetrained = "";
        mDeclawed = "";
        mDescription = "";
    }

    public Animal(String name, String bitmapImageUrl, String status,
                  String gender, String age, String breed, String housetrained,
                  String declawed, String description) {
        mName = name;
        mBitmapImageUrl = bitmapImageUrl;
        mStatus = status;
        mGender = gender;
        mAge = age;
        mBreed = breed;
        mHousetrained = housetrained;
        mDeclawed = declawed;
        mDescription = description;
        mScrapedURLAddress = "";
    }

    public String getName() {
        return mName;
    }

    public String getBitmapImageUrl() {
        return mBitmapImageUrl;
    }

    public String getStatus() {
        return mStatus;
    }

    public String getGender() {
        return mGender;
    }

    public String getAge() {
        return mAge;
    }

    public String getBreed() {
        return mBreed;
    }

    public String getScrapedURLAddress() {
        return mScrapedURLAddress;
    }

    public String getHousetrained() {
        return mHousetrained;
    }

    public String getDeclawed() {
        return mDeclawed;
    }

    public String getDescription() {
        return mDescription;
    }

    public Animal(Parcel in) {
        mName = in.readString();
        mBitmapImageUrl = in.readString();
        mStatus = in.readString();
        mGender = in.readString();
        mAge = in.readString();
        mBreed = in.readString();
        mScrapedURLAddress = in.readString();
        mHousetrained = in.readString();
        mDeclawed = in.readString();
        mDescription = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeString(mBitmapImageUrl);
        dest.writeString(mStatus);
        dest.writeString(mGender);
        dest.writeString(mAge);
        dest.writeString(mBreed);
        dest.writeString(mScrapedURLAddress);
        dest.writeString(mHousetrained);
        dest.writeString(mDeclawed);
        dest.writeString(mDescription);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Animal> CREATOR = new Parcelable.Creator<Animal>() {
        @Override
        public Animal createFromParcel(Parcel in) {
            return new Animal(in);
        }

        @Override
        public Animal[] newArray(int size) {
            return new Animal[size];
        }
    };
}