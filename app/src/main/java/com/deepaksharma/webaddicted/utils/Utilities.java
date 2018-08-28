package com.deepaksharma.webaddicted.utils;

import android.util.Log;
import android.widget.Toast;

import com.deepaksharma.webaddicted.GlobalClass;
import com.deepaksharma.webaddicted.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Stack;

/**
 * Created by deepaksharma on 24/8/18.
 */

public class Utilities {

    private static final String TAG = Utilities.class.getSimpleName();
    private static final String APP_NAME = GlobalClass.getInstance().getString(R.string.app_name);

    public static long getFolderSize() {
        final long[] result = {0};
//        new Handler().post(() -> {
        Stack<File> dirlist = new Stack<File>();
        dirlist.clear();
        dirlist.push(BackupConstant.getParentFolder());
        while (!dirlist.isEmpty()) {
            File dirCurrent = dirlist.pop();
            File[] fileList = dirCurrent.listFiles();
            if (fileList != null && fileList.length > 0) {
                for (File f : fileList) {
                    if (f.isDirectory())
                        dirlist.push(f);
                    else
                        result[0] += f.length();
                }
            }
        }
//        });
        return result[0];

    }

    public static void saveSizeInFile(String fileSize) {
        File file = new File(BackupConstant.getParentFolder(), BackupConstant.BACKUP_SIZE);
        FileOutputStream fileOutputStream = null;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("file_size", fileSize);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            String stackString = fileSize;
            if (stackString.length() > 0) {
                file.createNewFile();
                fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(stackString.getBytes());
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        } catch (FileNotFoundException fileNotFoundException) {
            Log.e("TAG", "File not found!", fileNotFoundException);
        } catch (IOException ioException) {
            Log.e("TAG", "Unable to write to file!", ioException);
        }
    }

    public static String readFile() {
        String result = null;
        File file = new File(BackupConstant.parentFolderName, BackupConstant.BACKUP_SIZE);
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
            }
            result = text.toString();
            br.close();
        } catch (IOException e) {
            Log.d("Util", "readFile: " + e);
        }
        return result;
    }

    public static boolean hideFile(File file) {
        File dstFile = new File(file.getParent(), "."  + file.getName());
        return file.renameTo(dstFile);
    }

    public static void unhideFile(File file) {
        String fileName = file.getName().replace("." + APP_NAME, "");
        Log.d(TAG, "unhideFile: old File name -> " + file.getName() + "\n New FileName -> " + fileName);
        File dstFile = new File(file.getParent(), fileName);
        file.renameTo(dstFile);
    }

    public static void openHiddenFile(File file) {
        File dstFile = new File(file.getParent(), "/." + APP_NAME + file.getName());
        file.renameTo(dstFile);
    }

    public static void showMessage(String message) {
        Toast.makeText(GlobalClass.getInstance(), message, Toast.LENGTH_SHORT).show();
    }


}
