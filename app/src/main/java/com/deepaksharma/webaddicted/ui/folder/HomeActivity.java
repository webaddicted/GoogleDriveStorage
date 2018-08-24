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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.deepaksharma.webaddicted.ui.events.ListenChangeEventsForFilesActivity;
import com.deepaksharma.webaddicted.Final.create.CreateDirectory;
import com.deepaksharma.webaddicted.R;
import com.deepaksharma.webaddicted.ui.events.SubscribeChangeEventsForFilesActivity;

/**
 * An activity to list all available demo activities.
 */
public class HomeActivity extends Activity {

    private final Class[] sActivities = new Class[] {CreateEmptyFileActivity.class,
            CreateFileActivity.class, CreateFolderActivity.class, CreateFileInFolderActivity.class,
            CreateFolderInFolderActivity.class, CreateFileInAppFolderActivity.class,
            CreateFileWithCreatorActivity.class, RetrieveMetadataActivity.class,
            RetrieveContentsActivity.class, RetrieveContentsWithProgressDialogActivity.class,
            EditMetadataActivity.class, AppendContentsActivity.class, RewriteContentsActivity.class,
            PinFileActivity.class, InsertUpdateCustomPropertyActivity.class,
            DeleteCustomPropertyActivity.class, QueryFilesActivity.class,
            QueryFilesInFolderActivity.class, QueryNonTextFilesActivity.class,
            QuerySortedFilesActivity.class, QueryFilesSharedWithMeActivity.class,
            QueryFilesWithTitleActivity.class, QueryFilesWithCustomPropertyActivity.class,
            QueryStarredTextFilesActivity.class, QueryTextOrHtmlFilesActivity.class,
            ListenChangeEventsForFilesActivity.class, SubscribeChangeEventsForFilesActivity.class};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        String[] titles = getResources().getStringArray(R.array.titles_array);
        ListView mListViewSamples = (ListView) findViewById(R.id.listViewSamples);
        mListViewSamples.setAdapter(
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, titles));
        mListViewSamples.setOnItemClickListener((arg0, arg1, i, arg3) -> {
            Intent intent = new Intent(getBaseContext(), sActivities[i]);
            startActivity(intent);
        });
        findViewById(R.id.btn_original).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, CreateDirectory.class));
            }
        });
    }
}
