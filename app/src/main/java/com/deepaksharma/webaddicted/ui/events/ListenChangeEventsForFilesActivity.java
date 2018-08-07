/*
 * Copyright 2013 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.deepaksharma.webaddicted.ui.events;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.events.ChangeEvent;
import com.google.android.gms.drive.events.ListenerToken;
import com.google.android.gms.drive.events.OnChangeListener;
import com.deepaksharma.webaddicted.R;
import com.deepaksharma.webaddicted.ui.folder.BaseDemoActivity;


import java.util.Date;

/**
 * An activity that listens to change events on a user-picked file.
 */
public class ListenChangeEventsForFilesActivity extends BaseDemoActivity {
    private static final String TAG = "ListenChangeEvents";

    /*
     * Toggles file change event listening.
     */
    private Button mActionButton;

    /**
     * Displays the change event on the screen.
     */
    private TextView mLogTextView;

    /**
     * Represents the file picked by the user.
     */
    private DriveId mSelectedFileId;

    /**
     * Identifies our change listener so it can be unsubscribed.
     */
    private ListenerToken mChangeListenerToken;

    /**
     * Keeps the status whether change events are being listened to or not.
     */
    private boolean mIsSubscribed = false;

    /**
     * Timer to force periodic tickles of the watched file
     */
    private CountDownTimer mCountDownTimer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changeevents);

        mLogTextView = findViewById(R.id.textViewLog);
        mActionButton = findViewById(R.id.buttonAction);
        mActionButton.setOnClickListener(v -> toggle());
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopTimer();
    }

    @Override
    protected void onDriveClientReady() {
        pickTextFile()
                .addOnSuccessListener(this,
                        driveId -> {
                            mSelectedFileId = driveId;
                            refresh();
                        })
                .addOnFailureListener(this, e -> {
                    Log.e(TAG, "No file selected", e);
                    showMessage(getString(R.string.file_not_selected));
                    finish();
                });
    }

    /**
     * Refreshes the status of UI elements. Enables/disables subscription button
     * depending on whether there is file picked by the user.
     */
    private void refresh() {
        if (mSelectedFileId == null) {
            mActionButton.setEnabled(false);
        } else {
            mActionButton.setEnabled(true);
        }

        if (!mIsSubscribed) {
            mActionButton.setText(R.string.button_subscribe);
        } else {
            mActionButton.setText(R.string.button_unsubscribe);
        }
    }

    /**
     * Toggles the subscription status. If there is no selected file, returns
     * immediately.
     */
    private void toggle() {
        if (mSelectedFileId == null) {
            return;
        }
        stopTimer();
        DriveFile file = mSelectedFileId.asDriveFile();
        if (!mIsSubscribed) {
            Log.d(TAG, "Starting to listen to the file changes.");
            mIsSubscribed = true;
            mCountDownTimer = new TickleTimer(30000 /* 30 seconds total */,
                    1000 /* tick every 1 second */ );
            mCountDownTimer.start();
            // [START add_change_listener]
            getDriveResourceClient()
                    .addChangeListener(file, changeListener)
                    .addOnSuccessListener(this, listenerToken -> mChangeListenerToken = listenerToken);
            // [END add_change_listener]
        } else {
            Log.d(TAG, "Stopping to listen to the file changes.");
            mIsSubscribed = false;
            // [START remove_change_listener]
            getDriveResourceClient().removeChangeListener(mChangeListenerToken);
            // [END remove_change_listener]
        }
        refresh();
    }

    private void stopTimer() {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
            mCountDownTimer = null;
        }
    }

    // [START change_listener]
    /**
     * A listener to handle file change events.
     */
    final private OnChangeListener changeListener = new OnChangeListener() {
        @Override
        public void onChange(ChangeEvent event) {
            mLogTextView.append(getString(R.string.change_event, event));
        }
    };
    // [END change_listener]

    private class TickleTimer extends CountDownTimer {
        TickleTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long l) {
            Log.d(TAG, "Updating metadata.");
            MetadataChangeSet metadata =
                    new MetadataChangeSet.Builder().setLastViewedByMeDate(new Date()).build();
            getDriveResourceClient()
                    .updateMetadata(mSelectedFileId.asDriveResource(), metadata)
                    .addOnSuccessListener(ListenChangeEventsForFilesActivity.this,
                            metadata1 -> Log.d(TAG, "Updated metadata."))
                    .addOnFailureListener(
                            ListenChangeEventsForFilesActivity.this, e -> Log.e(TAG, "Unable to update metadata", e));
        }

        @Override
        public void onFinish() {
            showMessage(getString(R.string.tickle_finished));
        }
    }
}
