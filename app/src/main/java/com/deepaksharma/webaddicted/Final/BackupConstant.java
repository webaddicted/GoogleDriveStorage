package com.deepaksharma.webaddicted.Final;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by deepaksharma on 6/8/18.
 */

public class BackupConstant {
    public static final String parentFolderName = "BC_112";
    public static final String subFolderMessageName = "Message_112";
    public static final String subFolderMediaName = "Media_112";
    public static final String subFolderGalleryName = "Gallery_112";
    public static final String subFolderGallerythumbName = "Gallery_thumb_112";
    public static final int PICKFILE_RESULT_CODE = 256;
    private static ProgressDialog progressDialog;

    public static List<String> getChildFolder() {
        List<String> childList = new ArrayList<>();
        childList.add(subFolderMessageName);
        childList.add(subFolderMediaName);
        childList.add(subFolderGalleryName);
        childList.add(subFolderGallerythumbName);
        return childList;
    }

    public static void createAppFolder() {
        File file ;
        file = new File(Environment.getExternalStorageDirectory()+"/"+parentFolderName);
        file.mkdirs();
        file = new File(Environment.getExternalStorageDirectory()+"/"+parentFolderName+"/"+subFolderGalleryName);
        file.mkdirs();
        file = new File(Environment.getExternalStorageDirectory()+"/"+parentFolderName+"/"+subFolderGallerythumbName);
        file.mkdirs();
        file = new File(Environment.getExternalStorageDirectory()+"/"+parentFolderName+"/"+subFolderMediaName);
        file.mkdirs();
        file = new File(Environment.getExternalStorageDirectory()+"/"+parentFolderName+"/"+subFolderMessageName);
        file.mkdirs();
    }


    public static void showDialog(Context context, String Message) {
        if (progressDialog == null)
            progressDialog = ProgressDialog.show(context, "Please wait", Message);
    }

    public static void hideDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.hide();
            progressDialog = null;
        }
    }

    public static String getFolderPath(){
        return Environment.getExternalStorageDirectory()+"/"+BackupConstant.parentFolderName+"/";
    }
}
