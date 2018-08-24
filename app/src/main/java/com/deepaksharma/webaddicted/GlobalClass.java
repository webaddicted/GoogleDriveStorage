package com.deepaksharma.webaddicted;

import android.app.Application;
import android.os.Environment;

import com.deepaksharma.webaddicted.Final.BackupConstant;

import java.io.File;

/**
 * Created by deepaksharma on 7/8/18.
 */

public class GlobalClass extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        BackupConstant.createAppFolder();
    }
}
