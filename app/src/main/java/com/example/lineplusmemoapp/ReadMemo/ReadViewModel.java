package com.example.lineplusmemoapp.ReadMemo;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.lineplusmemoapp.Database.EntireDao;
import com.example.lineplusmemoapp.Database.EntireDbClient;
import com.example.lineplusmemoapp.Database.MemoAndImgPathEntity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReadViewModel extends AndroidViewModel {

    private EntireDao entireDao;
    private ExecutorService executorService;

    public ReadViewModel(@NonNull Application application) {
        super(application);
        entireDao = EntireDbClient.getInstance(application).getAppDatabase().entireDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<MemoAndImgPathEntity> getCurrentMemoAndImgPath(int mid) {
        return entireDao.selectImgPathWhereMid(mid);
    }

    public void deleteCurrentMemoAndImgPath(int mid) {
        entireDao.deleteMemo(mid);
    }
}
