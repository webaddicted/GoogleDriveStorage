package com.deepaksharma.webaddicted.ui.hidden;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.deepaksharma.webaddicted.db.DBUtilites;
import com.deepaksharma.webaddicted.db.entity.HiddenInfo;
import com.deepaksharma.webaddicted.utils.Utilities;

import java.io.File;
import java.util.List;

/**
 * Created by deepaksharma on 28/8/18.
 */

public class HiddenViewModel extends ViewModel {
    public MutableLiveData<List<HiddenInfo>> getHideLiveData;

    LiveData<List<HiddenInfo>> getHiddenFile() {
        if (getHideLiveData == null) {
            getHideLiveData = new MutableLiveData<>();
        }
        return getHideLiveData;
    }

    public void deleteFiles(List<HiddenInfo> files) {
        if(files!=null && files.size()>0){
            for (HiddenInfo hiddenInfo : files){
                if(hiddenInfo.isCheck() && new File(hiddenInfo.getFilePath()).exists()){
                    Utilities.deleteFile(new File(hiddenInfo.getFilePath()));
                    DBUtilites.getHiddenDao().deleteFileInfo(hiddenInfo);
                }}}
    }

    public void unHideFiles(List<HiddenInfo> files) {
        if(files!=null && files.size()>0){
            for (HiddenInfo hiddenInfo : files){
                if(hiddenInfo.isCheck() && new File(hiddenInfo.getFilePath()).exists()){
                    Utilities.unhideFile(new File(hiddenInfo.getFilePath()));
                    DBUtilites.getHiddenDao().deleteFileInfo(hiddenInfo);
                }
            }
        }
    }
}
