package com.deepaksharma.webaddicted;

import android.app.Application;

import com.deepaksharma.webaddicted.utils.BackupConstant;
import com.deepaksharma.webaddicted.preference.PreferenceUtil;

/**
 * Created by deepaksharma on 7/8/18.
 */

public class GlobalClass extends Application {
    private static GlobalClass mInstance;
    @Override
    public void onCreate() {
        super.onCreate();
        BackupConstant.createAppFolder();
        PreferenceUtil.init(getApplicationContext());
        mInstance = this;
    }
    public static GlobalClass getInstance() {
        return mInstance;
    }
}
