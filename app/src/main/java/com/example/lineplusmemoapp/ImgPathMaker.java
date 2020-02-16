package com.example.lineplusmemoapp;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import androidx.core.content.FileProvider;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImgPathMaker {
    private Context c;
    public ImgPathMaker (Context context) {
        c = context;
    }

    public Uri makeUri() {
        File imagesFolder = new File(Environment.getExternalStorageDirectory(), "Images");
        imagesFolder.mkdirs();
        File image = new File(imagesFolder, new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".png");
        Uri mCurrentPhotoPath = FileProvider.getUriForFile(c, "com.example.lineplusmemoapp.fileprovider", image);
        return mCurrentPhotoPath;
    }
}
