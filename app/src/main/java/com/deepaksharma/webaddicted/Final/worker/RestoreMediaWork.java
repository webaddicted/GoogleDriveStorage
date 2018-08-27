package com.deepaksharma.webaddicted.Final.worker;

import android.support.annotation.NonNull;

import com.deepaksharma.webaddicted.BackUpManager;
import com.deepaksharma.webaddicted.Final.BackUpUtility;
import com.deepaksharma.webaddicted.Final.BackupConstant;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.drive.DriveResourceClient;

import java.util.List;

import androidx.work.Data;
import androidx.work.Worker;

public class RestoreMediaWork extends Worker {
    @NonNull
    @Override
    public WorkerResult doWork() {
        Data inputData = getInputData();
        if (inputData != null) {
            String email = inputData.getString(BackupConstant.KEY_GOOGLE_SIGN_UP_ACCOUNT, "");
            GoogleSignInAccount signInAccount = BackUpUtility.getGoogleSignInAccount(getApplicationContext());
            BackUpManager backUpManager = BackUpManager.getBackUpMangerInstance(signInAccount);
            List<String> mAllFolders = BackUpUtility.getMediaFoldersName();
            DriveResourceClient driveResourceClient = backUpManager.getmDriveResourceClient();


        } else {
            return WorkerResult.FAILURE;

        }

        return WorkerResult.SUCCESS;
    }
}
