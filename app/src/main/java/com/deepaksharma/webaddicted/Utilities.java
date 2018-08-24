package com.deepaksharma.webaddicted;

import android.os.Environment;
import android.util.Log;

import com.deepaksharma.webaddicted.Final.BackupConstant;

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
    public static long getFolderSize() {
        final long[] result = {0};
//        new Handler().post(() -> {
        Stack<File> dirlist = new Stack<File>();
        dirlist.clear();
        dirlist.push(new File(Environment.getExternalStorageDirectory()+"/"+BackupConstant.parentFolderName));
        while (!dirlist.isEmpty()) {
            File dirCurrent = dirlist.pop();
            File[] fileList = dirCurrent.listFiles();
            for (File f : fileList) {
                if (f.isDirectory())
                    dirlist.push(f);
                else
                    result[0] += f.length();
            }
        }
//        });
        return result[0];

    }

    public static void saveSizeInFile(String fileSize) {
        File file = new File(BackupConstant.parentFolderName, "another txt file name which store media file");
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

    public static String readFile(){
        String result = null;
        File file = new File(BackupConstant.parentFolderName, "another txt file name which store media size");
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
            }
            result = text.toString();
            br.close();
        }
        catch (IOException e) {
            Log.d("Util", "readFile: "+e);
        }
        return  result;
    }

}
