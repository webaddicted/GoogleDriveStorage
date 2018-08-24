package com.deepaksharma.webaddicted.Final.worker;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.deepaksharma.webaddicted.BackUpManager;
import com.deepaksharma.webaddicted.Final.BackUpConstants;
import com.deepaksharma.webaddicted.Final.BackUpUtility;
import com.deepaksharma.webaddicted.Final.BackupConstant;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;



import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import androidx.work.Data;
import androidx.work.Worker;

public class UploadDbWork extends Worker {
    @NonNull
    @Override
    public WorkerResult doWork() {
        Data inputData = getInputData();
        if (inputData != null) {
            String email = inputData.getString(BackUpConstants.KEY_GOOGLE_SIGN_UP_ACCOUNT, "");
            if (!TextUtils.isEmpty(email)) {
                GoogleSignInAccount signInAccount = BackUpUtility.getGoogleSignInAccount(getApplicationContext());
                BackUpManager backUpManager = BackUpManager.getBackUpMangerInstance(signInAccount);
                DriveResourceClient driveResourceClient = backUpManager.getmDriveResourceClient();
                try {
                    BackUpUtility.deleteExistingFolder("DatabaseName", driveResourceClient);
                    BackUpUtility.deleteExistingFolder("another file Which store  media final size", driveResourceClient);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
//                Utilities.saveSizeInFile(String.valueOf(Utilities.getFolderSize()));
                File file = null;//BackUpUtility.getDbFile(getApplicationContext());
//                File file_size = new File(BackupConstant.parentFolderName, DbConstant.BACKUP_SIZE);
                File file_size = new File(BackupConstant.parentFolderName, "another file backup wali");
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
//            EventBus.getDefault().post(BackUpBroadCastReceiver.ACTION_STATUS.UPLOAD_DATABASE_SUCCESSFUL);
        }
        return driveFile;
    }


    public List<DriveFolder> isChildFolderPresent(List<String> mAllFolders, DriveFolder
            driveFolder, DriveResourceClient driveResourceClient) throws
            ExecutionException, InterruptedException {
        List<DriveFolder> mChildFolders = new ArrayList<>();
        for (String folder : mAllFolders) {
            Metadata childFolder = BackUpUtility.isFolderExists(folder, driveResourceClient);
            if (childFolder == null) {
                Task<DriveFolder> driveFolderTask = BackUpUtility.createFolderInFolder(folder, driveFolder.getDriveId().asDriveFolder(), driveResourceClient);
                DriveFolder child = Tasks.await(driveFolderTask);
                Log.d("UploadMedia", child.toString());
                mChildFolders.add(child);

            } else
                mChildFolders.add(childFolder.getDriveId().asDriveFolder());


        }
        return mChildFolders;
    }

}
