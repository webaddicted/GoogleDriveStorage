package com.deepaksharma.webaddicted.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;


import com.deepaksharma.webaddicted.db.entity.MediaInfo;
import com.deepaksharma.webaddicted.db.entity.UserInfo;

import java.util.List;

@Dao
public interface UserDao {

    @Insert
    public void insertUser(UserInfo userInfo);

    @Query("SELECT * FROM user_info")
    public List<UserInfo> getUserInfo();

    @Delete
    public void deleteUser(UserInfo userInfo);

    @Update
    public void updateUserInfo(UserInfo userInfo);


}