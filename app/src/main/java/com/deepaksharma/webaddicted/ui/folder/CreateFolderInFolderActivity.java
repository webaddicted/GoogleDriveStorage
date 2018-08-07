/*
 * Copyright 2013 Google Inc. All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.deepaksharma.webaddicted.ui.folder;

import android.util.Log;

import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.MetadataChangeSet;
import com.deepaksharma.webaddicted.R;

/**
 * An activity to create a folder inside a folder.
 */
public class CreateFolderInFolderActivity extends BaseDemoActivity {
    private static final String TAG = "CreateFolderInFolder";

    @Override
    protected void onDriveClientReady() {
        pickFolder()
                .addOnSuccessListener(this,
                        driveId -> createFolderInFolder(driveId.asDriveFolder()))
                .addOnFailureListener(this, e -> {
                    Log.e(TAG, "No folder selected", e);
                    showMessage(getString(R.string.folder_not_selected));
                    finish();
                });
    }

    private void createFolderInFolder(final DriveFolder parent) {
        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                              .setTitle("New folder")
                                              .setMimeType(DriveFolder.MIME_TYPE)
                                              .setStarred(true)
                                              .build();

        getDriveResourceClient()
                .createFolder(parent, changeSet)
                .addOnSuccessListener(this,
                        driveFolder -> {
                            showMessage(getString(R.string.file_created,
                                    driveFolder.getDriveId().encodeToString()));
                            finish();
                        })
                .addOnFailureListener(this, e -> {
                    Log.e(TAG, "Unable to create file", e);
                    showMessage(getString(R.string.file_create_error));
                    finish();
                });
    }
}
