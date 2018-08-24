package com.deepaksharma.webaddicted.Final.worker;

import com.google.android.gms.drive.DriveFile;

import androidx.work.Worker;

public class UploadWorkResult {
    private Worker.WorkerResult mUploadWorkResult;
    private DriveFile mUploadedFile;


    public Worker.WorkerResult getmUploadWorkResult() {
        return mUploadWorkResult;
    }

    public void setmUploadWorkResult(Worker.WorkerResult mUploadWorkResult) {
        this.mUploadWorkResult = mUploadWorkResult;
    }

    public DriveFile getmUploadedFile() {
        return mUploadedFile;
    }

    public void setmUploadedFile(DriveFile mUploadedFile) {
        this.mUploadedFile = mUploadedFile;
    }


}
