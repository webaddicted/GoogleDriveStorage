package com.deepaksharma.webaddicted.Final.worker;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.deepaksharma.webaddicted.utils.BackUpManager;
import com.deepaksharma.webaddicted.utils.BackUpUtility;
import com.deepaksharma.webaddicted.utils.BackupConstant;
import com.deepaksharma.webaddicted.db.DBUtilites;
import com.deepaksharma.webaddicted.db.dao.MediaDao;
import com.deepaksharma.webaddicted.db.entity.MediaInfo;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.Metadata;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutionException;

import androidx.work.Data;
import androidx.work.Worker;

public class RetrieveMediaWork extends Worker {
    private static final String TAG = RetrieveMediaWork.class.getSimpleName();
    @NonNull
    @Override
    public WorkerResult doWork() {
        Log.d(TAG, "doWork: Initialized RetrieveMediaWork ");
        Data inputData = getInputData();
        BackupConstant.createAppFolder();
        if (inputData != null) {
            String email = inputData.getString(BackupConstant.KEY_GOOGLE_SIGN_UP_ACCOUNT, "");
            if (!TextUtils.isEmpty(email)) {
                GoogleSignInAccount signInAccount = BackUpUtility.getGoogleSignInAccount(getApplicationContext());
                BackUpManager backUpManager = BackUpManager.getBackUpMangerInstance(signInAccount);
                DriveResourceClient driveResourceClient = backUpManager.getmDriveResourceClient();
                MediaDao mediaDao= DBUtilites.getMediaDao();
                if (mediaDao != null) {
                    List<MediaInfo> mediaList = mediaDao.getMediaInfo();
                    if (mediaList != null && mediaList.size() > 0) {
                        for (MediaInfo fileDetail : mediaList) {
                            File fileCompletePath = BackupConstant.getLocalPathWithFileName(fileDetail.getFolderName(), fileDetail.getName());
                            if (!(fileCompletePath.exists()))
                                downloadMedia(backUpManager, fileDetail, fileCompletePath.toString(), driveResourceClient);
                            else
                                Log.d(TAG, "doWork: File Exist -> "+fileCompletePath);
                        }
                    }
                }
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
