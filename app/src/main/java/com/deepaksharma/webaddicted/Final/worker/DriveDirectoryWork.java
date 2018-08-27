package com.deepaksharma.webaddicted.Final.worker;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.deepaksharma.webaddicted.BackUpManager;
import com.deepaksharma.webaddicted.Final.BackUpUtility;
import com.deepaksharma.webaddicted.Final.BackupConstant;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import androidx.work.Data;
import androidx.work.Worker;


public class DriveDirectoryWork extends Worker {
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
                Metadata parentMetaData = BackUpUtility.isFolderExists(BackupConstant.parentFolderName, driveResourceClient);
                if (parentMetaData != null) {
                    Log.d("UploadMedia", "Parent Already Present");
                    try {
                        List<DriveFolder> childFolders = isChildFolderPresent(BackUpUtility.getMediaFoldersName(), parentMetaData.getDriveId().asDriveFolder(), driveResourceClient);
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    // parent folder not exists;
                    Task<DriveFolder> parentFolderCreateTask = BackUpUtility.createFolder(BackupConstant.parentFolderName, driveResourceClient);
                    try {
                        DriveFolder driveFolder = Tasks.await(parentFolderCreateTask);
                        if (driveFolder != null) {
                            Log.d("UploadMedia", "Parent Created");
                            List<DriveFolder> childFolders = isChildFolderPresent(BackUpUtility.getMediaFoldersName(), driveFolder, driveResourceClient);
                        } else {
                            Log.d("UploadMedia", "Parent Folder Not Created");
                        }
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return WorkerResult.SUCCESS;
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
