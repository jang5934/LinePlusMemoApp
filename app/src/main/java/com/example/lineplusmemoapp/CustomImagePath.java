package com.example.lineplusmemoapp;

public class CustomImagePath {
    String imagePath;
    int pathType;

    public CustomImagePath(String p, int t) {
        this.imagePath = p;
        this.pathType = t;
    }

    public String getImagePath() {
        return this.imagePath;
    }

    public int getPathType() {
        return this.pathType;
    }
}