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

import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.metadata.CustomPropertyKey;
import com.deepaksharma.webaddicted.R;
import com.google.android.gms.tasks.Task;

/**
 * An activity to illustrate how to delete a custom property from a file.
 */
public class DeleteCustomPropertyActivity extends BaseDemoActivity {
    private static final String TAG = "DeleteCustomProperty";

    @Override
    protected void onDriveClientReady() {
        pickTextFile()
                .addOnSuccessListener(this,
                        driveId -> deleteCustomProperty(driveId.asDriveFile()))
                .addOnFailureListener(this, e -> {
                    Log.e(TAG, "No file selected", e);
                    showMessage(getString(R.string.file_not_selected));
                    finish();
                });
    }

    private void deleteCustomProperty(DriveFile file) {
        // [START delete_custom_property]
        CustomPropertyKey approvalPropertyKey =
                new CustomPropertyKey("approved", CustomPropertyKey.PUBLIC);
        CustomPropertyKey submitPropertyKey =
                new CustomPropertyKey("submitted", CustomPropertyKey.PUBLIC);
        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                              .deleteCustomProperty(approvalPropertyKey)
                                              .deleteCustomProperty(submitPropertyKey)
                                              .build();
        Task<Metadata> updateMetadataTask =
                getDriveResourceClient().updateMetadata(file, changeSet);
        updateMetadataTask
                .addOnSuccessListener(this,
                        metadata -> {
                            showMessage(getString(R.string.custom_property_deleted));
                            finish();
                        })
                .addOnFailureListener(this, e -> {
                    Log.e(TAG, "Unable to update metadata", e);
                    showMessage(getString(R.string.update_failed));
                    finish();
                });
        // [END delete_custom_property]
    }
}
