package com.example.lineplusmemoapp;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MemoData {
    public int mid;
    public String subject;
    public String content;
    public ArrayList<MemoImageData> images;

    public MemoData(int _mid, String _subject, String _content, ArrayList<MemoImageData> _images) {
        mid = _mid;
        subject = _subject;
        content = _content;
        images = _images;
    }

    public int getMid() {
        return mid;
    }

    public String getSubject() {
        return subject;
    }

    public String getMemoContent() {
        return content;
    }

    public ArrayList<MemoImageData> getImages() {
        return images;
    }
}
