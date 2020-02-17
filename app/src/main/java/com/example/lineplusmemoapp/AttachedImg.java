package com.example.lineplusmemoapp;

public class AttachedImg {

    // 0인 경우 빈 사진
    // 1인 경우 저장된 사진
    // 2인 경우 인터넷 이미지
    private int img_type;

    private String imgPath;

    public AttachedImg(int img_type){
        this.img_type = img_type;
    }
    public AttachedImg(int img_type, String img_path) {
        this.img_type = img_type;
        this.imgPath = img_path;
    }

    public void setImg_type(int img_type) {
        this.img_type = img_type;
    }
    public void setImgPath(String path) {imgPath = path;}

    public int getImg_type() {
        return img_type;
    }
    public String getImgPath() {return imgPath; }
}
