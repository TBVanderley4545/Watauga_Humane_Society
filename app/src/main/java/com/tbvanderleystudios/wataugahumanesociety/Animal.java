package com.tbvanderleystudios.wataugahumanesociety;

import android.graphics.Bitmap;

public class Animal {
    private String mName;
    private Bitmap mBitmapImage;
    private String mStatus;
    private String mGender;
    private String mAge;
    private String mBreed;

    public Animal(String name, Bitmap bitmapImage, String status,
                  String gender, String age, String breed) {
        mName = name;
        mBitmapImage = bitmapImage;
        mStatus = status;

        mGender = gender;
        mAge = age;
        mBreed = breed;
    }

    public String getName() {
        return mName;
    }

    public Bitmap getBitmapImage() {
        return mBitmapImage;
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

}
