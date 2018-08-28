package com.deepaksharma.webaddicted.Final.worker;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.deepaksharma.webaddicted.utils.BackUpManager;
import com.deepaksharma.webaddicted.utils.BackUpUtility;
import com.deepaksharma.webaddicted.utils.BackupConstant;
import com.deepaksharma.webaddicted.utils.Utilities;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import androidx.work.Data;
import androidx.work.Worker;

public class UploadDbWork extends Worker {
    private static final String TAG = UploadDbWork.class.getSimpleName();
    @NonNull
    @Override
    public WorkerResult doWork() {
        Log.d(TAG, "doWork: Initialized UploadDbWork ");
        Data inputData = getInputData();
        if (inputData != null) {
            String email = inputData.getString(BackupConstant.KEY_GOOGLE_SIGN_UP_ACCOUNT, "");
            if (!TextUtils.isEmpty(email)) {
                GoogleSignInAccount signInAccount = BackUpUtility.getGoogleSignInAccount(getApplicationContext());
                BackUpManager backUpManager = BackUpManager.getBackUpMangerInstance(signInAccount);
                DriveResourceClient driveResourceClient = backUpManager.getmDriveResourceClient();
                try {
                    BackUpUtility.deleteExistingFile(BackupConstant.DBNAME, driveResourceClient);
                    BackUpUtility.deleteExistingFile(BackupConstant.BACKUP_SIZE, driveResourceClient);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Utilities.saveSizeInFile(String.valueOf(Utilities.getFolderSize()));
                File file = BackUpUtility.getDbFile(getApplicationContext());
                File file_size = new File(BackupConstant.getParentFolder(), BackupConstant.BACKUP_SIZE);
                Metadata metadata = BackUpUtility.isFolderExists(BackupConstant.parentFolderName, driveResourceClient);
                if (metadata != null) {
                    try {
                        uploadDatabase(backUpManager, metadata.getDriveId(), driveResourceClient, file_size);
                        uploadDatabase(backUpManager, metadata.getDriveId(), driveResourceClient, file);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return WorkerResult.FAILURE;
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                        return WorkerResult.FAILURE;
                    } catch (TimeoutException e) {
                        e.printStackTrace();
                        return WorkerResult.FAILURE;
                    }
                }
            }
        }
        return WorkerResult.SUCCESS;
    }

    private DriveFile uploadDatabase(BackUpManager backUpManager, DriveId driveId, DriveResourceClient driveResourceClient, File file) throws InterruptedException, ExecutionException, TimeoutException {
        DriveFile driveFile = null;
        Task<DriveFile> mUploadTask = backUpManager.uploadBackup(driveId, file, driveResourceClient);
        driveFile = Tasks.await(mUploadTask);
        if (driveFile != null && mUploadTask.isSuccessful()) {
            Log.d(TAG, "uploadDatabase: Database upload successfully.");
//            EventBus.getDefault().post(BackUpBroadCastReceiver.ACTION_STATUS.UPLOAD_DATABASE_SUCCESSFUL);
        }
        return driveFile;
    }
}
