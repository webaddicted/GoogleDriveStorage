package com.deepaksharma.webaddicted.Final.create;

import android.arch.lifecycle.ViewModel;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.util.Log;

import com.deepaksharma.webaddicted.utils.BackupConstant;
import com.deepaksharma.webaddicted.Final.worker.CheckGoogleSignInWork;
import com.deepaksharma.webaddicted.Final.worker.DriveDirectoryWork;
import com.deepaksharma.webaddicted.Final.worker.RetrieveMediaWork;
import com.deepaksharma.webaddicted.Final.worker.RetriveDbWork;
import com.deepaksharma.webaddicted.Final.worker.UploadDbWork;
import com.deepaksharma.webaddicted.Final.worker.UploadMediaWork;
import com.deepaksharma.webaddicted.utils.Utilities;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.ExecutionException;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import static com.deepaksharma.webaddicted.utils.Utilities.showMessage;

/**
 * Created by deepaksharma on 24/8/18.
 */

public class CreateViewModel extends ViewModel {
    private static final String TAG = CreateViewModel.class.getSimpleName();
    public WorkManager mWorkManger;
    public OneTimeWorkRequest checkGoogleSignIn, createDirectory, mediaUploadRequest,
            dbUploadDbRequest, dbRetriveRequest, mediaRetrieveRequest;

    protected void initWorker() {
        mWorkManger = WorkManager.getInstance();
        checkGoogleSignIn = new OneTimeWorkRequest.Builder(CheckGoogleSignInWork.class)
                .build();
        createDirectory = new OneTimeWorkRequest.Builder(DriveDirectoryWork.class)
                .build();
        mediaUploadRequest = new OneTimeWorkRequest.Builder(UploadMediaWork.class)
                .build();
        dbUploadDbRequest = new OneTimeWorkRequest.Builder(UploadDbWork.class)
                .build();
        dbRetriveRequest = new OneTimeWorkRequest.Builder(RetriveDbWork.class)
                .build();
        mediaRetrieveRequest = new OneTimeWorkRequest.Builder(RetrieveMediaWork.class)
                .build();

    }

    public void createFolderStructure(DriveResourceClient driveResourceClient) {
        isFolderExist(BackupConstant.parentFolderName,driveResourceClient , new CreateDirectory.CallbackListener() {
            @Override
            public void Success(DriveId driveId) {
                if (driveId == null) showMessage("Folder not exist.");
                else createChildFolder(driveId, driveResourceClient);
            }

            @Override
            public void Failure() {
                createFolder(BackupConstant.parentFolderName,driveResourceClient, new CreateDirectory.CallbackListener() {
                    @Override
                    public void Success(DriveId driveId) {
                        createChildFolder(driveId, driveResourceClient);
                    }

                    @Override
                    public void Failure() {

                    }
                });
            }
        });
    }

    public void isFolderExist(String folderName, DriveResourceClient driveResourceClient, CreateDirectory.CallbackListener callbackListener) {
        Task<MetadataBuffer> queryTask = null;
        Query query = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.TITLE, folderName))
                .build();
        queryTask = driveResourceClient.query(query);
        queryTask.addOnSuccessListener(
                metadataBuffer -> {
                    Metadata metadata = null;
                    for (Metadata mss : metadataBuffer) {
                        if (mss.getTitle().equals(folderName)) {
//                                    strDriveId = mss.getDriveId().encodeToString();
//                                        DriveId.decodeFromString()
//                                    DriveFolder driveFolderName = mss.getDriveId().asDriveFolder();
                            metadata = mss;
                            break;
                        }
                    }
                    if (metadata != null)
                        callbackListener.Success(metadata.getDriveId());
                    else
                        callbackListener.Failure();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error retrieving files", e);
                });
    }

    private void createFolder(String folderName, DriveResourceClient driveResourceClient , CreateDirectory.CallbackListener callbackListener) {
//        getRootFolder is used for show folder
//        getAppFolder is used for hide folder
        driveResourceClient
                .getRootFolder()
                .continueWithTask(task -> {
                    DriveFolder parentFolder = task.getResult();
                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setTitle(folderName)
                            .setMimeType(DriveFolder.MIME_TYPE)
                            .setStarred(true)
                            .build();
                    return driveResourceClient.createFolder(parentFolder, changeSet);
                })
                .addOnSuccessListener(
                        driveFolder -> {
                            if (driveFolder != null) {
                                callbackListener.Success(driveFolder.getDriveId());
                            }
                            showMessage("file_created -> "+
                                    driveFolder.getDriveId().encodeToString());
                        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Unable to create single folder file", e);
                        showMessage("file_create_error -> "+e);
                    }
                });
    }

    public void createHideFolder(String folderName, DriveResourceClient driveResourceClient, CreateDirectory.CallbackListener callbackListener) {
//        getRootFolder is used for show folder
//        getAppFolder is used for hide folder
        driveResourceClient
                .getAppFolder()
                .continueWithTask(task -> {
                    DriveFolder parentFolder = task.getResult();
                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setTitle(folderName)
                            .setMimeType(DriveFolder.MIME_TYPE)
                            .setStarred(true)
                            .build();
                    return driveResourceClient.createFolder(parentFolder, changeSet);
                })
                .addOnSuccessListener(
                        driveFolder -> {
                            if (driveFolder != null) {
                                callbackListener.Success(driveFolder.getDriveId());
                            }
                            showMessage("File created -> "+driveFolder.getDriveId().encodeToString());
                        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Unable to create single folder file", e);
                        Utilities.showMessage("file_create_error "+e);
                    }
                });
    }

    private void createFolderInFolder(String folderName, final DriveFolder parent, DriveResourceClient driveResourceClient, CreateDirectory.CallbackListener callbackListener) {
        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                .setTitle(folderName)
                .setMimeType(DriveFolder.MIME_TYPE)
                .setStarred(true)
                .build();
        driveResourceClient
                .createFolder(parent, changeSet)
                .addOnSuccessListener(
                        driveFolder -> {
                            if (driveFolder != null) {
                                callbackListener.Success(driveFolder.getDriveId());
                            }
                            showMessage("file created -> "+driveFolder.getDriveId().encodeToString());
//                            showMessage(getString(R.string.file_created,
//                                    driveFolder.getDriveId().encodeToString()));
                        })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Unable to create folder in folder file "+e);
                    showMessage("file_create_error");
                });
    }

    public void deleteFolderExist(String folderName, DriveResourceClient driveResourceClient) {
        Query query = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.TITLE, folderName))
                .build();
        Task<MetadataBuffer> queryTask = driveResourceClient.query(query);
        queryTask
                .addOnSuccessListener(
                        metadataBuffer -> {
                            for (Metadata mss : metadataBuffer) {
                                if (mss.getTitle().equals(folderName)) {
                                    deleteFile(mss.getDriveId().asDriveFolder(), null, driveResourceClient);
                                }
                            }
                        })
                .addOnFailureListener( e -> {
                    Log.e(TAG, "Error retrieving files", e);
                });
    }

    private void deleteFileExist(String folderName, DriveResourceClient driveResourceClient) {
        Query query = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.TITLE, folderName))
                .build();
        Task<MetadataBuffer> queryTask = driveResourceClient.query(query);
        queryTask
                .addOnSuccessListener(
                        metadataBuffer -> {
                            for (Metadata mss : metadataBuffer) {
                                if (mss.getTitle().equals(folderName)) {
                                    deleteFile(null, mss.getDriveId().asDriveFile(), driveResourceClient);
                                }
                            }
                        })
                .addOnFailureListener(e -> Log.e(TAG, "Error retrieving files", e));
    }

    private void deleteFile(DriveFolder driveFolder, DriveFile driveFile, DriveResourceClient driveResourceClient) {
        driveResourceClient
                .delete(driveFolder != null ? driveFolder : driveFile)
                .addOnSuccessListener(
                        aVoid -> showMessage("File deleted."))
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Unable to delete file", e);
                    showMessage("File failed.");
                });
        ;
    }

    Task<DriveFolder> uploadFileInFolder = null;
    public void uploadFile(File filePath, DriveId driveId, DriveResourceClient driveResourceClient) {
        if (driveId == null)
            uploadFileInFolder = driveResourceClient.getRootFolder();
        final Task<DriveContents> createContentsTask = driveResourceClient.createContents();
        createContentsTask
                .continueWithTask(task -> {
                    DriveFolder parent;
                    if (driveId == null)
                        parent = uploadFileInFolder.getResult();
                    else
                        parent = driveId.asDriveFolder();
                    DriveContents contents = createContentsTask.getResult();
                    OutputStream outputStream = contents.getOutputStream();
//                    inFileName is path of file
                    final String inFileName = filePath.toString();

                    try {
                        File uploadFile = new File(inFileName);
                        FileInputStream fis = new FileInputStream(uploadFile);
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = fis.read(buffer)) > 0) {
                            outputStream.write(buffer, 0, length);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setTitle(filePath.getName())
                            .setStarred(false)
                            .build();

                    return driveResourceClient.createFile(parent, changeSet, contents);
                })
                .addOnSuccessListener(
                        driveFile -> {
//                            CreateDirectory.this.showMessage(getString(R.string.file_created,
//                                    driveFile.getDriveId().encodeToString()));
                            showMessage("File successfully uploaded.");
                            BackupConstant.hideDialog();
                        })

                .addOnFailureListener(e -> {
//                    Log.e(TAG, "Unable to create file", e);
                    Utilities.showMessage("file_create_error" + e);
                    BackupConstant.hideDialog();
                });
    }

    private void createChildFolder(DriveId driveIds, DriveResourceClient driveResourceClient) {
        List<String> childLiat = BackupConstant.getChildFolder();
        for (String folderName : childLiat) {
            isFolderExist(folderName,driveResourceClient, new CreateDirectory.CallbackListener() {
                @Override
                public void Success(DriveId driveId) {
                    Log.d(TAG, "Success: Folder created  -> ");
                }

                @Override
                public void Failure() {
                    createFolderInFolder(folderName, driveIds.asDriveFolder(),driveResourceClient , new CreateDirectory.CallbackListener() {
                        @Override
                        public void Success(DriveId driveId) {
                            showMessage("folder successfully created " + driveId);
                            Log.d(TAG, "Success : -> " + driveId);
                        }

                        @Override
                        public void Failure() {
                            showMessage("Folder creation filed");

                            Log.d(TAG, "Failure: -> ");
                        }
                    });
                }
            });
        }
    }

    public String retrieveDriveData(DriveFile driveFile, String file, DriveResourceClient driveResourceClient) throws ExecutionException, InterruptedException {
        //DB Path
        Task<DriveContents> openFileTask = driveResourceClient.openFile(driveFile, DriveFile.MODE_READ_ONLY);
//      stop task for work in background
//        DriveContents driveContents = Tasks.await(openFileTask);
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

                        // Close the streams
                        output.flush();
                        output.close();
                        fileInputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return driveResourceClient.discardContents(contents);

                }).addOnSuccessListener(aVoid -> {
                    showMessage("File successfully downloaded.");
                    BackupConstant.hideDialog();
                });
//        Tasks.await(discard);
        return file;

    }

}
