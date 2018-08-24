package com.deepaksharma.webaddicted.Final.worker;

import android.support.annotation.NonNull;
import android.util.Log;

import androidx.work.Worker;

/**
 * Created by rahil on 2/8/18.
 */

public class UploadDatabaseWork extends Worker {

    @NonNull
    @Override
    public WorkerResult doWork() {
        Log.d("I am in UploadDb", "hhfgffg---------");
        return WorkerResult.SUCCESS;
    }
}
