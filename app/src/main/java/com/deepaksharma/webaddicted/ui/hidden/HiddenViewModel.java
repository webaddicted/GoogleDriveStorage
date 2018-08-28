package com.deepaksharma.webaddicted.ui.hidden;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.deepaksharma.webaddicted.db.entity.HiddenInfo;

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

}
