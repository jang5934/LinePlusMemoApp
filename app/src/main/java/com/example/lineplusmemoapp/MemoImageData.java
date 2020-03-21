package com.example.lineplusmemoapp;

public class MemoImageData {
    public int iid;
    public String path;
    public int pathType;

    public MemoImageData(int _iid, String _path, int _pathType) {
        iid = _iid;
        path = _path;
        pathType = _pathType;
    }

    public int getIid() {
        return iid;
    }

    public String getImagePath() {
        return path;
    }

    public int getPathType() {
        return pathType;
    }
}
