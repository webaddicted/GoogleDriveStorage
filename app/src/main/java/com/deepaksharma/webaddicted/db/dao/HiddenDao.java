package com.deepaksharma.webaddicted.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.deepaksharma.webaddicted.db.entity.HiddenInfo;
import com.deepaksharma.webaddicted.db.entity.UserInfo;

import java.util.List;

@Dao
public interface HiddenDao {
    @Insert
    public void insertFileInfo(HiddenInfo userInfo);

    @Query("SELECT * FROM hidden_info")
    public List<HiddenInfo> getHiddenFile();

    @Delete
    public void deleteFileInfo(HiddenInfo hiddenInfo);

    @Update
    public void updateFileInfo(HiddenInfo hiddenInfo);

}