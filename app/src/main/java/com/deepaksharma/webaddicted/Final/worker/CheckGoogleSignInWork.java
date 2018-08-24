package com.deepaksharma.webaddicted.Final.worker;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.deepaksharma.webaddicted.BackUpManager;
import com.deepaksharma.webaddicted.Final.BackUpConstants;
import com.deepaksharma.webaddicted.Final.BackUpUtility;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.drive.Drive;



import java.util.HashSet;
import java.util.Set;

import androidx.work.Data;
import androidx.work.Worker;

/**
 * Created by Deepak Sharma
 */

public class CheckGoogleSignInWork extends Worker {

    BackUpManager backUpManager;

    @NonNull
    @Override
    public WorkerResult doWork() {
        GoogleSignInAccount mCurrentAccount = BackUpUtility.getGoogleSignInAccount(getApplicationContext());
        Set<Scope> requiredScopes = new HashSet<>(2);
        requiredScopes.add(Drive.SCOPE_FILE);
        requiredScopes.add(Drive.SCOPE_APPFOLDER);
        boolean isGoogDriveIniaLized = BackUpUtility.isGoogleDriveInialized(mCurrentAccount, requiredScopes);
        if (isGoogDriveIniaLized) {
            backUpManager = BackUpManager.getBackUpMangerInstance(mCurrentAccount);
            backUpManager.initializeDriveClient(mCurrentAccount, getApplicationContext());
            Log.d("GoogleDrive Initialized", "Success");
            setOutputData(sendCurrentSignedInAccount(mCurrentAccount));
            return WorkerResult.SUCCESS;
        } else {
//            EventBus.getDefault().post(BackUpBroadCastReceiver.ACTION_STATUS.SIGN_IN_FAILED);
//            LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
//            Intent localIntent = new Intent(IntentKeypool.CHECK_GOOGLE_AUTHENTICATION);
//            localIntent.putExtra(IntentKeypool.STATUS, BackUpBroadCastReceiver.ACTION_STATUS.SIGN_IN_FAILED);
//            localBroadcastManager.sendBroadcast(localIntent);
            return WorkerResult.FAILURE;
        }


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
