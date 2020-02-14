package com.example.lineplusmemoapp;

public class AttachedImg {
    // img_type가 1인 경우 저장된 사진
    // 0인 경우 빈 사진
    private int img_type;

    public AttachedImg(int img_type){
        this.img_type = img_type;
    }

    public void setImg_type(int img_type) {
        this.img_type = img_type;
    }

    public int getImg_type() {
        return img_type;
    }
}
