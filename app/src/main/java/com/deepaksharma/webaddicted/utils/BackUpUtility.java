package com.deepaksharma.webaddicted.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.deepaksharma.webaddicted.preference.PreferenceConstant;
import com.deepaksharma.webaddicted.preference.PreferenceUtil;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import androidx.work.WorkManager;

/**
 * Created by Deepak Sharma on 2/8/18.
 */

public class BackUpUtility {

    public static boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    //
    public static File[] getLocalFilesInFolder(String folderName) {
        return new File(Environment.getExternalStorageDirectory() + "/" + BackupConstant.parentFolderName + "/" + folderName).listFiles();
    }

    public static void deleteWork() {
        String signInWorker = PreferenceUtil.getInstance().getPreference(PreferenceConstant.WORKER_CHECK_SIGN_IN, "");
        String createDirectoryWorker = PreferenceUtil.getInstance().getPreference(PreferenceConstant.WORKER_CHECK_SIGN_IN, "");
        String uploadMediaWorker = PreferenceUtil.getInstance().getPreference(PreferenceConstant.WORKER_CHECK_SIGN_IN, "");
        String uploadDbWorker = PreferenceUtil.getInstance().getPreference(PreferenceConstant.WORKER_CHECK_SIGN_IN, "");
        String retriveDbWorker = PreferenceUtil.getInstance().getPreference(PreferenceConstant.WORKER_RETRIVE_DB, "");
        String retriveMediaWorker = PreferenceUtil.getInstance().getPreference(PreferenceConstant.WORKER_RETRIVE_MEDIA, "");

        if (signInWorker != null && signInWorker.length() > 1) {
            WorkManager.getInstance().cancelWorkById(UUID.fromString(signInWorker));
            Log.d("", "signInWorker: " + signInWorker);
        }
        if (createDirectoryWorker != null && createDirectoryWorker.length() > 1) {
            WorkManager.getInstance().cancelWorkById(UUID.fromString(createDirectoryWorker));
            Log.d("", "createDirectoryWorker: " + createDirectoryWorker);
        }

        if (uploadMediaWorker != null && uploadMediaWorker.length() > 1) {
            WorkManager.getInstance().cancelWorkById(UUID.fromString(uploadMediaWorker));
            Log.d("", "uploadMediaWorker: " + uploadMediaWorker);
        }
        if (uploadDbWorker != null && uploadDbWorker.length() > 1) {
            WorkManager.getInstance().cancelWorkById(UUID.fromString(uploadDbWorker));
            Log.d("", "uploadDbWorker: " + uploadDbWorker);
        }
        if (retriveDbWorker != null && retriveDbWorker.length() > 1) {
            WorkManager.getInstance().cancelWorkById(UUID.fromString(retriveDbWorker));
            Log.d("", "retriveDbWorker: " + retriveDbWorker);
        }
        if (retriveMediaWorker != null && retriveMediaWorker.length() > 1) {
            WorkManager.getInstance().cancelWorkById(UUID.fromString(retriveMediaWorker));
            Log.d("", "retriveMediaWorker: " + retriveMediaWorker);
        }
    }

    public static Metadata isFolderExists(String folderName, DriveResourceClient resourceClient) {
        Metadata metadata = null;
        Query query = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.TITLE, folderName))
                .build();
        Task<MetadataBuffer> queryTask = resourceClient.query(query);
        try {
            MetadataBuffer buffer = Tasks.await(queryTask);
            for (Metadata b : buffer) {
                if (b.getTitle().equals(folderName)) {
                    metadata = b;
                    break;
                }
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return metadata;
    }

    public static Metadata isChildFolderExists(String folderName, DriveResourceClient resourceClient) {
        Metadata metadata = null;
        Query query = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.TITLE, folderName))
                .build();
        Task<MetadataBuffer> queryTask = resourceClient.query(query);
        if (queryTask.isSuccessful()) {
            MetadataBuffer buffer = queryTask.getResult();
            for (Metadata b : buffer) {
                if (b.getTitle().equals(folderName)) {
                    metadata = b;
                    break;
                }
            }
        }
        return metadata;
    }

    public static void deleteExistingFile(String fileName, DriveResourceClient resourceClient) throws ExecutionException, InterruptedException {
        Metadata metadata = null;
        Query query = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.TITLE, fileName))
                .build();
        Task<MetadataBuffer> queryTask = resourceClient.query(query);
        MetadataBuffer metadataBuffer = Tasks.await(queryTask);
        for (Metadata b : metadataBuffer) {
            if (b.getTitle().equals(fileName)) {
                metadata = b;
                Task<Void> deleteTask = deleteExistingFile(resourceClient, metadata.getDriveId().asDriveFile());
                Tasks.await(deleteTask);
            }
        }
    }

    public static Task<Void> deleteExistingFile(DriveResourceClient resourceClient, DriveFile driveFile) {
        return resourceClient.delete(driveFile);
    }

    public static Task<DriveFolder> createFolder(String folderName, DriveResourceClient resourceClient) {
        return resourceClient
                .getRootFolder()
                .continueWithTask(task -> {
                    DriveFolder parentFolder = task.getResult();
                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setTitle(folderName)
                            .setMimeType(DriveFolder.MIME_TYPE)
                            .setStarred(false)
                            .build();
                    return resourceClient.createFolder(parentFolder, changeSet);
                });
    }

    public static Task<DriveFolder> createFolderInFolder(String folderName, final DriveFolder parent, DriveResourceClient driveResourceClient) {
        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                .setTitle(folderName)
                .setMimeType(DriveFolder.MIME_TYPE)
                .setStarred(false)
                .build();
        return driveResourceClient.createFolder(parent, changeSet);
    }

    public static GoogleSignInAccount getGoogleSignInAccount(Context context) {
        Set<Scope> requiredScopes = new HashSet<>(2);
        requiredScopes.add(Drive.SCOPE_FILE);
        requiredScopes.add(Drive.SCOPE_APPFOLDER);
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(context);
        return signInAccount;
    }

    public static boolean isGoogleDriveInialized(GoogleSignInAccount signInAccount, Set<Scope> requiredScopes) {
        return signInAccount != null && signInAccount.getGrantedScopes().containsAll(requiredScopes);
    }

    public static List<String> getMediaFoldersName() {
        List<String> mediaFolderName = new ArrayList<>();
        mediaFolderName.add(BackupConstant.subFolderGalleryName);
        mediaFolderName.add(BackupConstant.subFolderGallerythumbName);
        mediaFolderName.add(BackupConstant.subFolderMediaName);
        mediaFolderName.add(BackupConstant.subFolderMessageName);
        return mediaFolderName;
    }


    public static File getDbFile(Context context) {
        return context.getDatabasePath(BackupConstant.DBNAME);
    }

//    public static int getTotalFilesToUpload() {
//        int totalSize = 0;
//        for (String folder : getMediaFoldersName()) {
//            if (getLocalFilesInFolder(folder) != null && getLocalFilesInFolder(folder).length > 0)
//                totalSize = totalSize + getLocalFilesInFolder(folder).length;
//        }
//        return totalSize;
//    }


}
