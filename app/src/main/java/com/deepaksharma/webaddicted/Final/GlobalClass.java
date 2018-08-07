package com.deepaksharma.webaddicted.Final;

import android.app.Application;
import android.os.Environment;

import java.io.File;

/**
 * Created by deepaksharma on 7/8/18.
 */

public class GlobalClass extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        File folder = new File(Environment.getExternalStorageDirectory() + "/GD");
        if (!folder.exists()) {
            folder.mkdir();
        }
    }
}
