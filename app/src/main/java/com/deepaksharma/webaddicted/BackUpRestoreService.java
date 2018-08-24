package com.deepaksharma.webaddicted;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.view.View;

//import org.greenrobot.eventbus.EventBus;
//import org.greenrobot.eventbus.Subscribe;
//import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkContinuation;
import androidx.work.WorkManager;
//import bconnected.com.bconnected.R;
//import bconnected.com.bconnected.backup.events.ProgressEvent;
//import bconnected.com.bconnected.constants.AppConstant;
//import bconnected.com.bconnected.constants.PreferenceConstant;
//import bconnected.com.bconnected.db.ChatDao;
//import bconnected.com.bconnected.db.DbConstant;
//import bconnected.com.bconnected.ui.home.HomeActivity;
//import bconnected.com.bconnected.utilities.SessionManager;
//
//import static bconnected.com.bconnected.backup.BackUpUtility.deleteWork;
//import static bconnected.com.bconnected.backup.BackUpUtility.removeNotification;

public class BackUpRestoreService extends Service {
    private Handler h;
    private Runnable r;
    public static final String START_BACK_UP_SERVICE = "startBackUp";
    public static final String START_RESTORE_SERVICE = "startRestore";
    public static final String STOP_BACKUP_SERVICE = "stopBackUp";
    public static final String STOP_RESTORE_SERVICE = "stopRestore";
    private int currentProgressPercentage;
    private int uploadedFileSizeInKb;
    long counter = 0;
    int PROGRESS_MAX = 100;
    int PROGRESS_CURRENT = 0;
    int totalFiles;
    String information = "";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

//    @Override
//    public void onCreate() {
//        super.onCreate();
//        EventBus.getDefault().register(this);
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        EventBus.getDefault().unregister(this);
//    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onEvent(ProgressEvent progressEvent) {
//        long totalFiles = progressEvent.getTotalProgress();
//        counter = progressEvent.getCurrentProgress();
//        if (totalFiles > 0 && counter > 0) {
//            currentProgressPercentage = (int) ((counter / totalFiles) * 100);
//        }
//    }
//
////    @Subscribe(threadMode = ThreadMode.MAIN)
////    public void onEvent(BackUpBroadCastReceiver.ACTION_STATUS actionStatus) {
////        if (actionStatus == BackUpBroadCastReceiver.ACTION_STATUS.UPLOAD_FAILED) {
////            h.removeCallbacks(r);
////            stopForeground(true);
////            stopSelf();
////            deleteWork();
////
////        } else if (actionStatus == BackUpBroadCastReceiver.ACTION_STATUS.UPLOAD_DATABASE_SUCCESSFUL ||
////                actionStatus == BackUpBroadCastReceiver.ACTION_STATUS.SIGN_IN_FAILED) {
////            if (actionStatus == BackUpBroadCastReceiver.ACTION_STATUS.UPLOAD_DATABASE_SUCCESSFUL) {
////                File dbFile = getDatabasePath(DbConstant.DATABASENAME);
////                if (dbFile != null)
////                    EventBus.getDefault().post(dbFile.length() / 1024);
////
////            }
////            information = "Back Up Successful";
////            h.removeCallbacks(r);
////            deleteWork();
////            stopForeground(true);
////            stopSelf();
////        } else if (actionStatus == BackUpBroadCastReceiver.ACTION_STATUS.DBRETRIVAL_SUCESS) {
////            h.removeCallbacks(r);
////            stopForeground(true);
////            stopSelf();
////        }
////    }
//
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//    private Notification updateNotification(BackUpBroadCastReceiver.ACTION_STATUS type) {
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
//                new Intent(this, HomeActivity.class), 0);
//        String info = "";
//
//        if (type == BackUpBroadCastReceiver.ACTION_STATUS.DBRETRIVAL_SUCESS)
//            info = "Restore Succesful";
//        else {
//            info = counter + "KB Uploaded";
//        }
//        return new NotificationCompat.Builder(this)
//                .setContentTitle(info)
//                .setTicker(info)
//                .setContentText(currentProgressPercentage+" %")
//                .setSmallIcon(R.drawable.app_icon_round)
//                .setLargeIcon(
//                        Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),
//                                R.drawable.app_icon_round), 128, 128, false))
//                .setContentIntent(pendingIntent)
//                .setProgress(100, currentProgressPercentage, false)
//                .setOngoing(true).build();
//    }
//
//    @SuppressLint("RestrictedApi")
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        String action = intent.getAction();
//        WorkManager mWorkManger = WorkManager.getInstance();
//        OneTimeWorkRequest checkGoogleSignIn = new OneTimeWorkRequest.Builder(CheckGoogleSignInWork.class)
//                .build();
//        OneTimeWorkRequest createDirectory = new OneTimeWorkRequest.Builder(DriveDirectoryWork.class)
//                .build();
//        OneTimeWorkRequest mediaUploadRequest = new OneTimeWorkRequest.Builder(UploadMediaWork.class)
//                .build();
//        OneTimeWorkRequest dbUploadDbRequest = new OneTimeWorkRequest.Builder(UploadDbWork.class)
//                .build();
//        OneTimeWorkRequest dbRetriveRequest = new OneTimeWorkRequest.Builder(RetriveDbWork.class)
//                .build();
//        OneTimeWorkRequest mediaRetrieveRequest = new OneTimeWorkRequest.Builder(RetrieveMediaWork.class)
//                .build();
//
//        switch (action) {
//            case START_BACK_UP_SERVICE:
//                deleteWork();
//                WorkContinuation continuation =
//                        mWorkManger.beginWith(checkGoogleSignIn);
//                continuation = continuation.then(createDirectory);
//                if (SessionManager.getInstance().getBooleanFromPreferences(PreferenceConstant.INCLUDE_MEDIA))
//                    continuation = continuation.then(mediaUploadRequest);
//                continuation = continuation.then(dbUploadDbRequest);
//                continuation.enqueue();
//                SessionManager.getInstance().setStringFromPref(PreferenceConstant.WORKER_UPLOAD, checkGoogleSignIn.getStringId());
//                SessionManager.getInstance().setStringFromPref(PreferenceConstant.WORKER_UPLOAD_DB, mediaUploadRequest.getStringId());
//                SessionManager.getInstance().setStringFromPref(PreferenceConstant.WORKER_CHECK_SIGN_IN, mediaUploadRequest.getStringId());
//                SessionManager.getInstance().setStringFromPref(PreferenceConstant.WORKER_UPLOAD, mediaUploadRequest.getStringId());
//                initForeGround(1);
//                break;
//            case START_RESTORE_SERVICE:
//                WorkContinuation retriveContinuation =
//                        mWorkManger.beginWith(checkGoogleSignIn);
//                retriveContinuation = retriveContinuation.then(createDirectory);
//                retriveContinuation = retriveContinuation.then(dbRetriveRequest);
//                retriveContinuation = retriveContinuation.then(mediaRetrieveRequest);
//                retriveContinuation.enqueue();
//                SessionManager.getInstance().setStringFromPref(PreferenceConstant.WORKER_DOWNLOAD, mediaUploadRequest.getStringId());
//                initForeGround(8);
//
//                break;
//            case STOP_BACKUP_SERVICE:
//                h.removeCallbacks(r);
//                stopForeground(true);
//                stopSelf();
//                deleteWork();
//                break;
//            case STOP_RESTORE_SERVICE:
//                h.removeCallbacks(r);
//                stopForeground(true);
//                stopSelf();
//                break;
//        }
//        return Service.START_STICKY;
//    }
//
//    private void initForeGround(int type) {
//        h = new Handler();
//        r = new Runnable() {
//            @Override
//            public void run() {
//                startForeground(101, updateNotification(BackUpBroadCastReceiver.ACTION_STATUS.UPLOAD_FAILED));
//                h.postDelayed(this, 5000);
//            }
//        };
//
//        h.post(r);
//    }
}


