package com.example.a67024.serviceapp;

public class Fruit {

    private String mFruitName;

    private int mImageId;

    Fruit(String fruitName, int imageId) {
        mFruitName = fruitName;
        mImageId = imageId;
    }

    public void setmFruitName(String mFruitName) {
        this.mFruitName = mFruitName;
    }

    public String getmFruitName() {
        return mFruitName;
    }

    public void setmImageId(int mImageId) {
        this.mImageId = mImageId;
    }

    public int getmImageId() {
        return mImageId;
    }
}
