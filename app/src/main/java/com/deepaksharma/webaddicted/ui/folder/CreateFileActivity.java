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

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.MetadataChangeSet;
import com.deepaksharma.webaddicted.R;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * An activity to illustrate how to create a file.
 */
public class CreateFileActivity extends BaseDemoActivity {
    private static final String TAG = "CreateFileActivity";

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onDriveClientReady() {
        createFile();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void createFile() {
        // [START create_file]
        final Task<DriveFolder> rootFolderTask = getDriveResourceClient().getRootFolder();
        final Task<DriveContents> createContentsTask = getDriveResourceClient().createContents();
        Tasks.whenAll(rootFolderTask, createContentsTask)
                .continueWithTask(task -> {
                    DriveFolder parent = rootFolderTask.getResult();
                    DriveContents contents = createContentsTask.getResult();
                    OutputStream outputStream = contents.getOutputStream();
                    try (Writer writer = new OutputStreamWriter(outputStream)) {
                        writer.write("{\n" +
                                "   \"sys\":\n" +
                                "   {\n" +
                                "      \"country\":\"GB\",\n" +
                                "      \"sunrise\":1381107633,\n" +
                                "      \"sunset\":1381149604\n" +
                                "   },\n" +
                                "   \"weather\":[\n" +
                                "      {\n" +
                                "         \"id\":711,\n" +
                                "         \"main\":\"Smoke\",\n" +
                                "         \"description\":\"smoke\",\n" +
                                "         \"icon\":\"50n\"\n" +
                                "      }\n" +
                                "   ],\n" +
                                "\t\n" +
                                "  \"main\":\n" +
                                "   {\n" +
                                "      \"temp\":304.15,\n" +
                                "      \"pressure\":1009,\n" +
                                "   }\n" +
                                "}");
                    }

                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                                          .setTitle("BC_121_file.json")
                                                          .setMimeType("text/plain")
                                                          .setStarred(true)
                                                          .build();

                    return getDriveResourceClient().createFile(parent, changeSet, contents);
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
        // [END create_file]
    }
}
