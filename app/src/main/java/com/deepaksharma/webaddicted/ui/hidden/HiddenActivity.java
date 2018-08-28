package com.deepaksharma.webaddicted.ui.hidden;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;

import com.deepaksharma.webaddicted.R;
import com.deepaksharma.webaddicted.db.DBUtilites;
import com.deepaksharma.webaddicted.utils.Utilities;
import com.deepaksharma.webaddicted.databinding.ActivityHiddenBinding;
import com.deepaksharma.webaddicted.db.entity.HiddenInfo;
import com.deepaksharma.webaddicted.ui.BaseActivity;

import java.io.File;

public class HiddenActivity extends BaseActivity implements View.OnClickListener {
    ActivityHiddenBinding hiddenBinding;
    RecyclViewAdapter recyclViewAdapter;
    HiddenViewModel hiddenViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hiddenBinding = DataBindingUtil.setContentView(this, R.layout.activity_hidden);
        hiddenViewModel = ViewModelProviders.of(this).get(HiddenViewModel.class);
        hiddenBinding.addHidden.setOnClickListener(this);

        hiddenViewModel.getHiddenFile().observe(this, hiddenInfos -> {
            hiddenViewModel.getHideLiveData.postValue(DBUtilites.getHiddenDao().getHiddenFile());
            setAdapter();
        });
        Log.d("TAG", "onCreate: "+DBUtilites.getHiddenDao().getHiddenFile().size());
        hiddenViewModel.getHideLiveData.postValue(DBUtilites.getHiddenDao().getHiddenFile());

    }

    private void setAdapter() {
        recyclViewAdapter = new RecyclViewAdapter(this, hiddenViewModel.getHideLiveData.getValue());
        hiddenBinding.hiddenRecycler.setHasFixedSize(true);
        hiddenBinding.hiddenRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        hiddenBinding.hiddenRecycler.setAdapter(recyclViewAdapter);
    }

    @Override
    protected File filePath(File filePath) {
        if (filePath.exists() && Utilities.hideFile(filePath)) {
            HiddenInfo hiddenInfo = new HiddenInfo();
            hiddenInfo.setFileStatus(FileStatus.HIDE.toString());
            hiddenInfo.setFileDate(System.currentTimeMillis());
            hiddenInfo.setFileName(filePath.getName());
            hiddenInfo.setFilePath(filePath.toString());
            hiddenInfo.setFileSize(filePath.length() / 1024);
            DBUtilites.getHiddenDao().insertFileInfo(hiddenInfo);
            Utilities.showMessage("Provide storage permission/File successfully hide.");
            hiddenViewModel.getHideLiveData.postValue(DBUtilites.getHiddenDao().getHiddenFile());
            recyclViewAdapter.notifyDataSetChanged();
        } else Utilities.showMessage("File not exist.");
        return filePath;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_hidden:
                pickFile();
                break;
        }
    }
}
