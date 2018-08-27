package com.deepaksharma.webaddicted.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.deepaksharma.webaddicted.db.dao.UserDao;
import com.deepaksharma.webaddicted.db.entity.MediaInfo;
import com.deepaksharma.webaddicted.db.entity.UserInfo;

@Database(entities = {UserInfo.class, MediaInfo.class}, version = 1)
public abstract class MyAppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract MediaDao mediaDao();
}