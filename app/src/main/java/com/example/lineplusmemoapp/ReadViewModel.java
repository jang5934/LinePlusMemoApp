package com.example.lineplusmemoapp;
import android.content.Context;
import android.database.Cursor;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class ReadViewModel extends ViewModel {

    private int memo_id;
    private MutableLiveData<MemoData> liveData;
    private Context requestingContext;

    public ReadViewModel() {
        if(liveData == null) {
            liveData = new MutableLiveData<MemoData>();
        }
    }

    public void setMemo_id(int _memo_id) {
        memo_id = _memo_id;
    }

    public void setRequestingContextt(Context _reqContext) {
        requestingContext = _reqContext;
    }

    public MutableLiveData<MemoData> getCurrentData() {
        // DB 헬퍼 및 커서 생성
        MemoDBOpenHelper openhelper = new MemoDBOpenHelper(requestingContext);
        openhelper.open();
        openhelper.create();
        Cursor mCursor = openhelper.selectMemoWhereMid(memo_id);
        mCursor.moveToNext();

        ArrayList<MemoImageData> imgDatas = new ArrayList<>();
        Cursor iCursor = openhelper.selectImgPathWhereMid(memo_id);
        try {
            while (iCursor.moveToNext()) {
                MemoImageData memoImgData;
                int T_iid = iCursor.getInt(iCursor.getColumnIndex("iid"));
                String T_imgPath = iCursor.getString(iCursor.getColumnIndex("path"));
                int T_type = iCursor.getInt(iCursor.getColumnIndex("path_type"));
                imgDatas.add(new MemoImageData(T_iid, T_imgPath, T_type));
            }
        } finally {
            iCursor.close();
        }

        liveData.setValue(new MemoData(
                memo_id,
                mCursor.getString(mCursor.getColumnIndex("subject")),
                mCursor.getString(mCursor.getColumnIndex("content")),
                imgDatas
        ));
        mCursor.close();
        return liveData;
    }
}
