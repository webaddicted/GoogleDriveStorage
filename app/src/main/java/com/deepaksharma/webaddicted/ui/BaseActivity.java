package com.deepaksharma.webaddicted.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.deepaksharma.webaddicted.utils.BackupConstant;
import com.deepaksharma.webaddicted.GlobalClass;
import com.deepaksharma.webaddicted.utils.PathUtil;

import java.io.File;

/**
 * Created by deepaksharma on 28/8/18.
 */

public abstract class BaseActivity extends AppCompatActivity {
    String TAG = BaseActivity.class.getSimpleName();

    protected void pickFile() {
        Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.setType("*/*");
        chooseFile.putExtra("return-data", false);
        startActivityForResult(Intent.createChooser(chooseFile, "Choose a file"),
                BackupConstant.PICKFILE_RESULT_CODE);
    }

    protected abstract File filePath(File filePath);

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BackupConstant.PICKFILE_RESULT_CODE && resultCode == RESULT_OK) {
            Uri content_describer = data.getData();
            Log.d(TAG, "onActivityResult: " + PathUtil.getPath(this, content_describer));
            File filePath = PathUtil.getPath(this, content_describer);
            filePath(filePath);
        }
    }
    public static String getPath(Uri uri) {
        String path = null;
        String[] projection = {MediaStore.Files.FileColumns.DATA};
        Cursor cursor = GlobalClass.getInstance().getContentResolver().query(uri, projection, null, null, null);

        if (cursor == null) {
            path = uri.getPath();
        } else {
            cursor.moveToFirst();
            int column_index = cursor.getColumnIndexOrThrow(projection[0]);
            path = cursor.getString(column_index);
            cursor.close();
        }
        return ((path == null || path.isEmpty()) ? (uri.getPath()) : path);
    }
}
