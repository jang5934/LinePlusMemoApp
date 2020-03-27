package com.example.lineplusmemoapp.EditMemo;

import java.util.Iterator;
import java.util.Vector;

public class ImgPathModificationRecorder {

    // 추가될 이미지와 삭제될 이미지에 대한 정보를 담아둘 벡터
    private Vector<CustomImagePath> beAddedPathList;
    private Vector<Integer> beDeletedIidList;

    public ImgPathModificationRecorder() {
        beAddedPathList = new Vector<>();
        beDeletedIidList = new Vector<>();
    }

    // 추가될 이미지 정보를 벡터에 삽입
    public void addBeAddedPath (String path, int type) {
        beAddedPathList.add(new CustomImagePath(path, type));
    }

    // 삭제될 이미지 id값을 벡터에 삽입
    public void addBeDeletedIid (int iid){
        beDeletedIidList.add(iid);
    }

    // 추가될 이미지 벡터에서 제외시킬 이미지만 골라주는 메서드
    public void removeFromBeAddedPath(String derivedPath) {
        Iterator i = beAddedPathList.iterator();
        while(i.hasNext()) {
            if(((CustomImagePath)i.next()).getImagePath() == derivedPath) {
                i.remove();
                break;
            }
        }
    }

    // 추가 및 삭제될 이미지 정보 벡터 반환
    public Vector<CustomImagePath> getBeAddedPathList() {
        return beAddedPathList;
    }
    public Vector<Integer> getBeDeletedIidList() {
        return beDeletedIidList;
    }

    // 추가될 이미지를 벡터에 편하게 담기 위한 클래스 선언
    public static class CustomImagePath {
        private String imagePath;
        private int pathType;

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
}
