package com.deepaksharma.webaddicted.utils;

import android.content.Context;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveClient;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ExecutionException;


/**
 * Created by Deepak Sharma
 */

public class BackUpManager {
private static final String TAG = BackUpManager.class.getSimpleName();
    private Context mContext;
    private DriveResourceClient mDriveResourceClient;
    private DriveClient mDriveClient;
    private static BackUpManager mInstance;

    private GoogleSignInAccount mGoogleSignInAccount;


    public GoogleSignInAccount getmGoogleSignInAccount() {
        return mGoogleSignInAccount;
    }

    public void setmGoogleSignInAccount(GoogleSignInAccount mGoogleSignInAccount) {
        this.mGoogleSignInAccount = mGoogleSignInAccount;
    }


    public static BackUpManager getBackUpMangerInstance(GoogleSignInAccount signInAccount) {
        if (mInstance == null) {
            mInstance = new BackUpManager();
        }
        return mInstance;
    }



    public void initializeDriveClient(GoogleSignInAccount signInAccount, Context context) {
        mDriveClient = Drive.getDriveClient(context.getApplicationContext(), signInAccount);
        mDriveResourceClient = Drive.getDriveResourceClient(context.getApplicationContext(), signInAccount);
    }

    private Task<Void> deleteFile(DriveFolder driveFolder, DriveFile driveFile, DriveResourceClient driveResourceClient) {
        return driveResourceClient.delete(driveFolder != null ? driveFolder : driveFile);

    }

    public Task<DriveFile> uploadBackup(DriveId driverId, File file, DriveResourceClient driveResourceClient) {
        final Task<DriveFolder> rootFolderTask = driveResourceClient.getRootFolder();
        final Task<DriveContents> createContentsTask = driveResourceClient.createContents();
        return Tasks.whenAll(rootFolderTask, createContentsTask)
                .continueWithTask(task -> {
                    DriveFolder parent = driverId.asDriveFolder();
                    DriveContents contents = createContentsTask.getResult();
                    OutputStream outputStream = contents.getOutputStream();
                    try {
                        FileInputStream fis = new FileInputStream(file);
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = fis.read(buffer)) > 0) {
                            outputStream.write(buffer, 0, length);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String fileName = file.getName();
                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setTitle(file.getName())
                            .setStarred(false)
                            .build();

                    return driveResourceClient.createFile(parent, changeSet, contents);
                });


    }

    public String retrieveDriveData(DriveFile driveFile, String file) throws ExecutionException, InterruptedException {
        //DB Path
        Task<DriveContents> openFileTask = mDriveResourceClient.openFile(driveFile, DriveFile.MODE_READ_ONLY);
        DriveContents driveContents = Tasks.await(openFileTask);
        Task<Void> discard = openFileTask
                .continueWithTask(task -> {
                    DriveContents contents = task.getResult();
                    try {
                        ParcelFileDescriptor parcelFileDescriptor = contents.getParcelFileDescriptor();
                        FileInputStream fileInputStream = new FileInputStream(parcelFileDescriptor.getFileDescriptor());

                        // Open the empty db as the output stream
                        OutputStream output = new FileOutputStream(file);

                        // Transfer bytes from the inputfile to the outputfile
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = fileInputStream.read(buffer)) > 0) {
                            output.write(buffer, 0, length);
                        }
                        Log.d(TAG, "retrieveDriveData: save file "+file);
                        // Close the streams
                        output.flush();
                        output.close();
                        fileInputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return mDriveResourceClient.discardContents(contents);

                });
        Tasks.await(discard);
        return file;

    }


    public Context getmContext() {
        return mContext;
    }

    public void setmContext(Context context) {
        mContext = context;
    }

    public DriveResourceClient getmDriveResourceClient() {
        return mDriveResourceClient;
    }

    public void setmDriveResourceClient(DriveResourceClient mDriveResourceClient) {
        mDriveResourceClient = mDriveResourceClient;
    }

    public DriveClient getmDriveClient() {
        return mDriveClient;
    }

    public void setmDriveClient(DriveClient driveClient) {
        mDriveClient = driveClient;
    }

}
