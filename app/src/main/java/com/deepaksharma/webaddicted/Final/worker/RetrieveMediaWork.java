package com.deepaksharma.webaddicted.Final.worker;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.deepaksharma.webaddicted.BackUpManager;
import com.deepaksharma.webaddicted.Final.BackUpUtility;
import com.deepaksharma.webaddicted.Final.BackupConstant;
import com.deepaksharma.webaddicted.db.entity.MediaInfo;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.Metadata;

import java.util.concurrent.ExecutionException;

import androidx.work.Data;
import androidx.work.Worker;

public class RetrieveMediaWork extends Worker {
    @NonNull
    @Override
    public WorkerResult doWork() {
        Data inputData = getInputData();
        BackupConstant.createAppFolder();
        if (inputData != null) {
            String email = inputData.getString(BackupConstant.KEY_GOOGLE_SIGN_UP_ACCOUNT, "");
            if (!TextUtils.isEmpty(email)) {
                GoogleSignInAccount signInAccount = BackUpUtility.getGoogleSignInAccount(getApplicationContext());
                BackUpManager backUpManager = BackUpManager.getBackUpMangerInstance(signInAccount);
                DriveResourceClient driveResourceClient = backUpManager.getmDriveResourceClient();
                String fileCompletePath = null;// complete folder path
                MediaInfo fileDetail = null; // is all media file db table

//                ChatDao chatDao = backUpManager.getmChatDao();
//                if (chatDao != null) {
//                    List<ChangeLog> mediaList = chatDao.getChangeLogData();
//                    if (mediaList != null && mediaList.size() > 0) {
//                        for (ChangeLog fileDetail : mediaList) {
//                            String fileCompletePath = BackUpUtility.getLocalPathWithFileName(fileDetail.getFolderName(), fileDetail.getFileName());
//                            if (!new File(fileCompletePath).exists())
//                                downloadMedia(backUpManager, fileDetail, fileCompletePath, driveResourceClient);
//                            else
//                                Log.d("retrieve data", "doWork: File Exist -> "+fileCompletePath);
//                        }
//                    }
//                }
            }
        }
        return WorkerResult.SUCCESS;
    }

    private void downloadMedia(BackUpManager backUpManager, MediaInfo mediaInfo, String localFileCompletePath, DriveResourceClient driveResourceClient) {

        Metadata fileMetaData = BackUpUtility.isFolderExists(mediaInfo.getName(), driveResourceClient);
        if (fileMetaData != null) {
            try {
                retriveMedia(backUpManager, fileMetaData, localFileCompletePath);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();

            }
            Log.d("UploadMedia", "Parent Already Present");

        }
    }

    private String retriveMedia(BackUpManager backUpManager, Metadata parentMetaData, String localFileCompletePath) throws ExecutionException, InterruptedException {
        return backUpManager.retrieveDriveData(parentMetaData.getDriveId().asDriveFile(), localFileCompletePath);
    }

}
