package com.example.lineplusmemoapp;

import android.content.Context;
import android.database.Cursor;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class MainActivityViewModel extends ViewModel {

    private Context requestingContext;
    private MutableLiveData<ArrayList<MemoData>> liveData;
    private ArrayList<MemoData> listItems;

    public MainActivityViewModel() {
        if(liveData == null) {
            liveData = new MutableLiveData<ArrayList<MemoData>>();
        }
        if(listItems == null) {
            listItems = new ArrayList<MemoData>();
        }
    }

    public void setRequestingContextt(Context _context) {
        requestingContext = _context;
    }

    public MutableLiveData<ArrayList<MemoData>> getCurrentData() {
        // DBhelper 및 커서 생성
        MemoDBOpenHelper openHelper = new MemoDBOpenHelper(requestingContext);
        openHelper.open();
        openHelper.create();

        // 리스트 아이템 추가 파트
        Cursor mCursor = openHelper.selectMemo();
        try{
            while(mCursor.moveToNext()) {
                MemoData tMemoData;
                int tMid = mCursor.getInt(mCursor.getColumnIndex("mid"));
                String tSubjcet = mCursor.getString(mCursor.getColumnIndex("subject"));
                String tContent = mCursor.getString(mCursor.getColumnIndex("content"));

                Cursor iCursor = openHelper.selectImgPathWhereMid(tMid);

                if(iCursor.getCount() == 0) {
                    tMemoData = new MemoData(tMid, tSubjcet, tContent, null);
                }
                else {
                    iCursor.moveToFirst();
                    int iid = iCursor.getInt(iCursor.getColumnIndex("iid"));
                    String img_path = iCursor.getString(iCursor.getColumnIndex("path"));
                    int img_path_type = iCursor.getInt(iCursor.getColumnIndex("path_type"));

                    ArrayList<MemoImageData> tMemoImageData = new ArrayList<>();
                    tMemoImageData.add(new MemoImageData(iid, img_path, img_path_type));

                    tMemoData = new MemoData(tMid, tSubjcet, tContent, tMemoImageData);
                }
                iCursor.close();
                listItems.add(tMemoData);
            }
        } finally {
            mCursor.close();
        }
        openHelper.close();
        liveData.setValue(listItems);
        return liveData;
    }
}
