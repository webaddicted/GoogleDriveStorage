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
package com.deepaksharma.webaddicted.ui.folder;

import android.util.Log;

import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.MetadataChangeSet;
import com.deepaksharma.webaddicted.R;

/**
 * An activity to illustrate how to create an empty file.
 */
public class CreateEmptyFileActivity extends BaseDemoActivity {
    private static final String TAG = "CreateEmptyFileActivity";

    @Override
    protected void onDriveClientReady() {
        createEmptyFile();
    }

    private void createEmptyFile() {
        // [START create_empty_file]
        getDriveResourceClient()
                .getRootFolder()
                .continueWithTask(task -> {
                    DriveFolder parentFolder = task.getResult();
                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                                          .setTitle("New file")
                                                          .setMimeType("text/plain")
                                                          .setStarred(true)
                                                          .build();
                    return getDriveResourceClient().createFile(parentFolder, changeSet, null);
                })
                .addOnSuccessListener(this,
                        driveFile -> {
                            showMessage(getString(R.string.file_created,
                                    driveFile.getDriveId().encodeToString()));
                            finish();
                        })
                .addOnFailureListener(this, e -> {
                    Log.e(TAG, "Unable to create file", e);
                    showMessage(getString(R.string.file_create_error));
                    finish();
                });
        // [END create_empty_file]
    }

}
