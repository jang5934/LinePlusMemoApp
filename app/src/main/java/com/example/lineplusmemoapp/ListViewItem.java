package com.example.lineplusmemoapp;

import android.graphics.drawable.Drawable;
import android.net.Uri;

public class ListViewItem {
    private int mid;
    private Drawable iconDrawable ;
    private String imageUri;
    private String subjectStr ;
    private String previewStr ;
    private int pathType;

    public void setMid(int id) {
        mid = id;
    }
    public void setIcon(Drawable icon) {
        iconDrawable = icon;
    }
    public void setImageuri(String imguri) {
        imageUri = imguri;
    }
    public void setSubject(String subject) {
        subjectStr = subject;
    }
    public void setPreview(String preview) { previewStr = preview; }
    public void setPathType(int pathtype) { pathType = pathtype; }

    public int getMid() {
        return this.mid;
    }
    public Drawable getIcon() {
        return this.iconDrawable ;
    }
    public String getImageUri() {
        return this.imageUri;
    }
    public String getSubject() {
        return this.subjectStr ;
    }
    public String getPreview() {
        return this.previewStr ;
    }
    public int getPathType() { return this.pathType; }
}