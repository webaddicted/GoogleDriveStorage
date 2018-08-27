package com.deepaksharma.webaddicted.Final.worker;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.deepaksharma.webaddicted.BackUpManager;
import com.deepaksharma.webaddicted.Final.BackUpUtility;
import com.deepaksharma.webaddicted.Final.BackupConstant;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.Metadata;



import java.io.File;
import java.util.concurrent.ExecutionException;

import androidx.work.Data;
import androidx.work.Worker;

public class RetriveDbWork extends Worker {
    GoogleSignInAccount signInAccount;

    @NonNull
    @Override
    public WorkerResult doWork() {
        Data inputData = getInputData();
        if (inputData != null) {
            String email = inputData.getString(BackupConstant.KEY_GOOGLE_SIGN_UP_ACCOUNT, "");
            if (!TextUtils.isEmpty(email)) {
                signInAccount = BackUpUtility.getGoogleSignInAccount(getApplicationContext());
                setOutputData(sendCurrentSignedInAccount(signInAccount));
                BackUpManager backUpManager = BackUpManager.getBackUpMangerInstance(signInAccount);
                DriveResourceClient driveResourceClient = backUpManager.getmDriveResourceClient();
                Metadata parentMetaData = BackUpUtility.isFolderExists("Db name which available on server", driveResourceClient);
                if (parentMetaData != null) {
                    try {
                        retriveDatabase(backUpManager, parentMetaData);
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                        return WorkerResult.FAILURE;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return WorkerResult.FAILURE;
                    }
                    Log.d("UploadMedia", "Parent Already Present");

                }
            }
        }
        return WorkerResult.SUCCESS;
    }

    private String retriveDatabase(BackUpManager backUpManager, Metadata parentMetaData) throws ExecutionException, InterruptedException {
        File file =null; // complete Db path here
        return backUpManager.retrieveDriveData(parentMetaData.getDriveId().asDriveFile(), file.toString());
    }


    private Data sendCurrentSignedInAccount(GoogleSignInAccount current) {
        Data.Builder builder = new Data.Builder();
        if (current != null) {
            builder.putString(BackupConstant.KEY_GOOGLE_SIGN_UP_ACCOUNT, current.getEmail());
        } else {
            builder.putString(BackupConstant.KEY_GOOGLE_SIGN_UP_ACCOUNT, "");

        }
        return builder.build();
    }

}
