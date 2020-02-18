package com.example.lineplusmemoapp;

import java.util.Vector;

public class ImgPathModificationRecorder {

    private Vector<String> beAddedAndCopiedPathList;
    private Vector<String> beAddedPathList;
    private Vector<String> beDeletedIidList;

    public ImgPathModificationRecorder() {
        beAddedAndCopiedPathList = new Vector<>();
        beAddedPathList = new Vector<>();
        beDeletedIidList = new Vector<>();
    }

    public void addBeAddedAndCopiedPath (String path) {
        beAddedAndCopiedPathList.add(path);
    }

    public void addBeAddedPath(String path) {
        beAddedPathList.add(path);
    }

    public void addBeDeletedIid (String iid){
        beDeletedIidList.add(iid);
    }

    public void removeFromBeAddedAndCopiedPath(String derivedPath) {
        int tempCnt = 0;
        while(tempCnt < beAddedAndCopiedPathList.size()) {
            if(beAddedAndCopiedPathList.get(tempCnt) == derivedPath) {
                beAddedAndCopiedPathList.remove(tempCnt);
                break;
            }
        }
    }

    public void removeFromBeAddedPath(String derivedPath) {
        int tempCnt = 0;
        while(tempCnt < beAddedPathList.size()) {
            if(beAddedPathList.get(tempCnt) == derivedPath) {
                beAddedPathList.remove(tempCnt);
                break;
            }
        }
    }

    public Vector<String> getBeAddedAndCopiedPathList() {
        return beAddedAndCopiedPathList;
    }
    public Vector<String> getBeAddedPathList() {
        return beAddedPathList;
    }

    public Vector<String> getBeDeletedIidList() {
        return beDeletedIidList;
    }
}
