package com.example.lineplusmemoapp;

import android.graphics.drawable.Drawable;
import android.net.Uri;

public class AttachedImg {
    // img_type가 1인 경우 저장된 사진
    // 0인 경우 빈 사진
    private int img_type;
    private Drawable iconDrawable ;
    private Uri imageUri;

    public AttachedImg(int img_type){
        this.img_type = img_type;
    }

    public void setImg_type(int img_type) {
        this.img_type = img_type;
    }
    public void setIcon(Drawable icon) {
        iconDrawable = icon;
    }
    public void setImageuri(Uri imguri) {
        imageUri = imguri;
    }

    public int getImg_type() {
        return img_type;
    }
    public Drawable getIcon() {
        return this.iconDrawable ;
    }
    public Uri getImageUri() {
        return this.imageUri;
    }
}
