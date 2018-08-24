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


/**
 * Created by Deepak Sharma
 */

public class UploadMediaWork extends Worker {


GoogleSignInAccount signInAccount;

    @NonNull
    @Override
    public WorkerResult doWork() {
        Data inputData = getInputData();
        if (inputData != null) {
            String email = inputData.getString(BackUpConstants.KEY_GOOGLE_SIGN_UP_ACCOUNT, "");
            if (!TextUtils.isEmpty(email)) {
                signInAccount = BackUpUtility.getGoogleSignInAccount(getApplicationContext());
                setOutputData(sendCurrentSignedInAccount(signInAccount));
                BackUpManager backUpManager = BackUpManager.getBackUpMangerInstance(signInAccount);
                List<String> mAllFolders = BackUpUtility.getMediaFoldersName();
                DriveResourceClient driveResourceClient = backUpManager.getmDriveResourceClient();
                DriveFile uploadedFile = null;
//                ChatDao chatDao = backUpManager.getmChatDao();
                for (String folderName : BackUpUtility.getMediaFoldersName()) {
                    Metadata metadata = BackUpUtility.isFolderExists(folderName, driveResourceClient);
                    if (metadata != null) {
                        try {
                            DriveFile driveFile = uploadFile(backUpManager, folderName, metadata.getDriveId(), driveResourceClient);
                            if (driveFile != null) {
                                Log.d("File Uploaded", driveFile.toString());
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (TimeoutException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return WorkerResult.SUCCESS;
    }

    public Metadata isParentFolderExists(DriveResourceClient driveResourceClient) {
        return BackUpUtility.isFolderExists(BackupConstant.parentFolderName, driveResourceClient);

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


//    private void writeFailedStatusToDisk(DriveFile uploadedFile, ChatDao chatDao) {
//        ChangeLog changeLog = new ChangeLog();
//        changeLog.setStatus(AppConstant.FAILED);
//        changeLog.setFileSize(0); //Length in kb
//        changeLog.setGoogleDriveFileId("");
//        chatDao.insert(changeLog);
//
//
//    }

    public DriveFile uploadFile(BackUpManager backUpManager, String folderName, DriveId
            driveId, DriveResourceClient driveResourceClient) throws
            InterruptedException, ExecutionException, TimeoutException {
        File[] mAllFiles = BackUpUtility.getLocalFilesInFolder(folderName);
//        ChatDao mChatdao = backUpManager.getmChatDao();
        ChangeLog changeLog = new ChangeLog();
        DriveFile driveFile = null;
        if (mAllFiles !=null && mAllFiles.length > 0) {
            for (File file : mAllFiles) {
                String fileName = file.getName();
//                if (TextUtils.isEmpty(mChatdao.getGoogleDriveId(fileName))) {
//                    Task<DriveFile> mUploadTask = backUpManager.uploadBackup(driveId, file, driveResourceClient);
//                    driveFile = Tasks.await(mUploadTask);
//                    if (driveFile != null && driveFile.getDriveId() != null && !TextUtils.isEmpty(driveFile.getDriveId().encodeToString())) {
//                        changeLog.setStatus(AppConstant.UPLOADED);
//                        changeLog.setFileSize(file.length() / 1024); //Length in kb
//                        changeLog.setGoogleDriveFileId(driveFile.getDriveId().encodeToString());
//                    } else {
//                        changeLog.setStatus(AppConstant.FAILED);
//                        changeLog.setFileSize(0); //Length in kb
//                        changeLog.setGoogleDriveFileId("");
//                    }
//                    changeLog.setFileName(file.getName());
//                    changeLog.setFolderName(folderName);
//                    changeLog.setUpdate_time(System.currentTimeMillis());
//                    mChatdao.insert(changeLog);
//                    ProgressEvent progressEvent = new ProgressEvent();
//                    int count = mChatdao.getAllNumbersOfFilesUploaded(AppConstant.UPLOADED);
//                    int totalFiles = BackUpUtility.getTotalFilesToUpload();
//                    progressEvent.setCurrentProgress(count);
//                    progressEvent.setTotalProgress(totalFiles);
//                    EventBus.getDefault().post(progressEvent);


                }
//                ProgressEvent progressEvent = new ProgressEvent();
//                int count = mChatdao.getAllNumbersOfFilesUploaded(AppConstant.UPLOADED);
//                int totalFiles = BackUpUtility.getTotalFilesToUpload();
//                progressEvent.setCurrentProgress(count);
//                progressEvent.setTotalProgress(totalFiles);
//                EventBus.getDefault().post(progressEvent);
            }
            return driveFile;
//        }
//        return null;

    }


    private int getTimeToWait(File file) {
        int timeToWait = 15;
        if (file != null && file.length() > 0) {
            if (getFileLengthInKb(file.length()) > 100) {

            } else if (getFileLengthInKb(file.length()) > 500 && getFileLengthInKb(file.length()) < 1000) {

            } else if (getFileLengthInKb(file.length()) > 500 && getFileLengthInKb(file.length()) < 1000) {

            } else if (getFileLengthInKb(file.length()) > 500 && getFileLengthInKb(file.length()) < 1000) {

            } else if (getFileLengthInKb(file.length()) > 500 && getFileLengthInKb(file.length()) < 1000) {

            } else if (getFileLengthInKb(file.length()) > 500 && getFileLengthInKb(file.length()) < 1000) {

            } else if (getFileLengthInKb(file.length()) > 500 && getFileLengthInKb(file.length()) < 1000) {

            } else if (getFileLengthInKb(file.length()) > 500 && getFileLengthInKb(file.length()) < 1000) {

            } else if (getFileLengthInKb(file.length()) > 500 && getFileLengthInKb(file.length()) < 1000) {

            } else if (getFileLengthInKb(file.length()) > 500 && getFileLengthInKb(file.length()) < 1000) {

            } else if (getFileLengthInKb(file.length()) > 500 && getFileLengthInKb(file.length()) < 1000) {

            } else if (getFileLengthInKb(file.length()) > 500 && getFileLengthInKb(file.length()) < 1000) {

            }


        }
        return timeToWait;
    }

    private long getFileLengthInKb(long length) {
        return length / 1024;
    }

    private Data sendCurrentSignedInAccount(GoogleSignInAccount current) {
        Data.Builder builder = new Data.Builder();
        if (current != null) {
            builder.putString(BackUpConstants.KEY_GOOGLE_SIGN_UP_ACCOUNT, current.getEmail());
        } else {
            builder.putString(BackUpConstants.KEY_GOOGLE_SIGN_UP_ACCOUNT, "");

        }
        return builder.build();
    }


}
