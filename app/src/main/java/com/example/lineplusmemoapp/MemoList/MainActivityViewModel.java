package com.example.lineplusmemoapp.MemoList;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.lineplusmemoapp.Database.EntireDao;
import com.example.lineplusmemoapp.Database.EntireDbClient;
import com.example.lineplusmemoapp.Database.MemoAndImgPathEntity;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivityViewModel extends AndroidViewModel {

    private EntireDao entireDao;
    private ExecutorService executorService;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        entireDao = EntireDbClient.getInstance(application).getAppDatabase().entireDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<MemoAndImgPathEntity>> getCurrentMemoAndImgPath() {
        return entireDao.selectMemoAndImgPathEntity();
    }
}
