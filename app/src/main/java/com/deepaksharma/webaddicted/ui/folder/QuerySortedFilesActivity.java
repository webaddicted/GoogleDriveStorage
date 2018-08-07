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

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SortOrder;
import com.google.android.gms.drive.query.SortableField;
import com.deepaksharma.webaddicted.R;
import com.google.android.gms.drive.widget.DataBufferAdapter;

/**
 * An activity to illustrate how to sort the results of a query.
 */
public class QuerySortedFilesActivity extends BaseDemoActivity {
    private static final String TAG = "QuerySorted";

    private DataBufferAdapter<Metadata> mResultsAdapter;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_listfiles);
        ListView mListView = findViewById(R.id.listViewResults);
        mResultsAdapter = new ResultsAdapter(this);
        mListView.setAdapter(mResultsAdapter);
    }

    @Override
    protected void onDriveClientReady() {
        listFiles();
    }

    /**
     * Clears the result buffer to avoid memory leaks as soon
     * as the activity is no longer visible by the user.
     */
    @Override
    protected void onStop() {
        super.onStop();
        mResultsAdapter.clear();
    }

    /**
     * Retrieves results for the next page. For the first run,
     * it retrieves results for the first page.
     */
    private void listFiles() {
        // [START query_sorted]
        SortOrder sortOrder = new SortOrder.Builder().addSortAscending(SortableField.TITLE).build();
        Query query = new Query.Builder().setSortOrder(sortOrder).build();
        // [END query_sorted]
        getDriveResourceClient()
                .query(query)
                .addOnSuccessListener(this,
                        metadataBuffer -> mResultsAdapter.append(metadataBuffer))
                .addOnFailureListener(this, e -> {
                    Log.e(TAG, "Error retrieving files", e);
                    showMessage(getString(R.string.query_failed));
                    finish();
                });
    }
}