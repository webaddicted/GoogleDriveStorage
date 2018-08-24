package com.deepaksharma.webaddicted.Final;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

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
import static com.deepaksharma.webaddicted.Final.BackupConstant.parentFolderName;
import static com.deepaksharma.webaddicted.Final.BackupConstant.subFolderGalleryName;
import static com.deepaksharma.webaddicted.Final.BackupConstant.subFolderGallerythumbName;
import static com.deepaksharma.webaddicted.Final.BackupConstant.subFolderMediaName;
import static com.deepaksharma.webaddicted.Final.BackupConstant.subFolderMessageName;

/**
 * Created by rahil on 2/8/18.
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

    public static File[] getLocalFilesInFolder(String folderName) {
        File[] files = null;
        if (folderName.equals(BackupConstant.subFolderGalleryName))
            files = new File(Environment.getExternalStorageDirectory()+"/"+parentFolderName+"/"+folderName).listFiles();
        else if (folderName.equals(BackupConstant.subFolderGallerythumbName))
            files = new File(Environment.getExternalStorageDirectory()+"/"+parentFolderName+"/"+folderName).listFiles();
        else if (folderName.equals(BackupConstant.subFolderMediaName))
            files = new File(Environment.getExternalStorageDirectory()+"/"+parentFolderName+"/"+folderName).listFiles();
        else if (folderName.equals(BackupConstant.subFolderMessageName))
            files = new File(Environment.getExternalStorageDirectory()+"/"+parentFolderName+"/"+folderName).listFiles();
        return files;
    }

//    public static String getLocalPathWithFileName(String folderName, String fileName) {
//        String filePath = null;
//        if (folderName.equals(DbConstant.SUB_FOLDER_COMPRESS_VIDEO))
//            filePath = FileUtils.getCompressVideoFolder().toString() + "/" + fileName;
//        else if (folderName.equals(DbConstant.SUB_FOLDER_PROFILE))
//            filePath = FileUtils.getProfileFolder().toString() + "/" + fileName;
//        else if (folderName.equals(DbConstant.SUB_FOLDER_RECEIVED))
//            filePath = FileUtils.getReceivedFolder().toString() + "/" + fileName;
//        else if (folderName.equals(DbConstant.SUB_FOLDER_SENT))
//            filePath = FileUtils.getSentFolder().toString() + "/" + fileName;
//        else if (folderName.equals(DbConstant.SUB_FOLDER_TEMP))
//            filePath = FileUtils.getTempFolder().toString() + "/" + fileName;
//        else if (folderName.equals(DbConstant.SUB_FOLDER_THUMBS))
//            filePath = FileUtils.getThumbsFolder().toString() + "/" + fileName;
//        return filePath;
//    }

    public static void deleteWork() {
        String uploadWork = null; //= worker id
        String downLoadWork = null ; //= worker id
        String uploadDb = null ;//= worker id;

        if (uploadWork != null && uploadWork.length() > 1) {
            WorkManager.getInstance().cancelWorkById(UUID.fromString(uploadWork));
            Log.d("", "scheduleWork: " + uploadWork);
        }
        if (downLoadWork != null && downLoadWork.length() > 1) {
            WorkManager.getInstance().cancelWorkById(UUID.fromString(downLoadWork));
            Log.d("", "scheduleWork: " + downLoadWork);
        }

        if (uploadDb != null && uploadDb.length() > 1) {
            WorkManager.getInstance().cancelWorkById(UUID.fromString(uploadDb));
            Log.d("", "scheduleWork: " + uploadDb);
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

    public static void deleteExistingFolder(String folderName, DriveResourceClient resourceClient) throws ExecutionException, InterruptedException {
        Metadata metadata = null;
        Query query = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.TITLE, folderName))
                .build();
        Task<MetadataBuffer> queryTask = resourceClient.query(query);
        MetadataBuffer metadataBuffer = Tasks.await(queryTask);
        for (Metadata b : metadataBuffer) {
            if (b.getTitle().equals(folderName)) {
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
        List<String>mediaFolderName = new ArrayList<>();
        mediaFolderName.add(subFolderGalleryName);
        mediaFolderName.add(subFolderGallerythumbName);
        mediaFolderName.add(subFolderMediaName);
        mediaFolderName.add(subFolderMessageName);
        return mediaFolderName;
    }


//    public static File getDbFile(Context context) {
//        return context.getDatabasePath(DbConstant.DATABASENAME);
//    }

//    public static int getTotalFilesToUpload() {
//        int totalSize = 0;
//        for (String folder : getMediaFoldersName()) {
//            if (getLocalFilesInFolder(folder) != null && getLocalFilesInFolder(folder).length > 0)
//                totalSize = totalSize + getLocalFilesInFolder(folder).length;
//        }
//        return totalSize;
//    }



}
