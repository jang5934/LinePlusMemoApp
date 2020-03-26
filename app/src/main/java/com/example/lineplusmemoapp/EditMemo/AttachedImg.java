package com.example.lineplusmemoapp.EditMemo;

public class AttachedImg {

    // 0 - 빈 사진
    // 1 - 경로를 포함한 이미 등록됐었던 사진
    // 2 - 경로를 포함한 새로 등록된 사진이 사진앨범에서 선택한 경우
    // 3 - 경로를 포함한 새로 등록된 사진이 카메라에서 촬영된 경우
    // 4 - 경로를 포함한 새로 등록된 사진이 URL인 경우
    private int img_type;
    private int iid;
    private String imgPath;

    public AttachedImg(int img_type){
        this.img_type = img_type;
    }
    public AttachedImg(int img_type, String img_path) {
        this.img_type = img_type;
        this.imgPath = img_path;
    }

    public AttachedImg(int img_type, String img_path, int iid) {
        this.img_type = img_type;
        this.imgPath = img_path;
        this.iid = iid;
    }

    public void setImg_type(int img_type) {
        this.img_type = img_type;
    }
    public void setImgPath(String path) {imgPath = path;}

    public int getImg_type() {
        return img_type;
    }
    public String getImgPath() {return imgPath; }
    public int getIid() {return iid;}
}
