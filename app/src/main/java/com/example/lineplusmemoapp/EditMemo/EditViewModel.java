package com.example.lineplusmemoapp.EditMemo;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.lineplusmemoapp.Database.EntireDao;
import com.example.lineplusmemoapp.Database.EntireDbClient;
import com.example.lineplusmemoapp.Database.ImgPathEntity;
import com.example.lineplusmemoapp.Database.MemoAndImgPathEntity;
import com.example.lineplusmemoapp.Database.MemoEntity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EditViewModel extends AndroidViewModel {
    private EntireDao entireDao;
    private ExecutorService executorService;

    public EditViewModel(@NonNull Application application) {
        super(application);
        entireDao = EntireDbClient.getInstance(application).getAppDatabase().entireDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public MemoAndImgPathEntity getCurrentMemoAndImgPath(int mid) {
        return entireDao.selectCurrentMemoAndImgPathWhereMid(mid);
    }

    public long insertCurrentMemo(MemoEntity c_Memo) {
        return entireDao.insertMemo(c_Memo);
    }

    public void updateCurrentMemo(int mid, MemoEntity c_Memo) {
        entireDao.updateMemo(mid, c_Memo.getSubject(), c_Memo.getContent());
    }

    public void insertImgPath(ImgPathEntity c_ImgPath) {
        entireDao.insertImgPath(c_ImgPath);
    }

    public void deleteImgPath(int iid) {
        entireDao.deleteImgPath(iid);
    }

    public ImgPathEntity selectImgPath(int iid) {
        return entireDao.selectImgPathWhereIid(iid);
    }
}
