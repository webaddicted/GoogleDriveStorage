package com.deepaksharma.webaddicted.db;

import android.arch.persistence.room.Room;
import android.content.Context;

import com.deepaksharma.webaddicted.utils.BackupConstant;
import com.deepaksharma.webaddicted.GlobalClass;
import com.deepaksharma.webaddicted.db.dao.HiddenDao;
import com.deepaksharma.webaddicted.db.dao.MediaDao;
import com.deepaksharma.webaddicted.db.dao.UserDao;

/**
 * Created by deepaksharma on 27/8/18.
 */

public class DBUtilites {
    private static MyAppDatabase myAppDatabase;

    public static MyAppDatabase getInstance(Context context) {
        if (myAppDatabase == null) {
            myAppDatabase = Room.databaseBuilder(context, MyAppDatabase.class, BackupConstant.DBNAME)
                    .allowMainThreadQueries().build();
        }
        return myAppDatabase;
    }

    public static UserDao getDbDao() {
        if (myAppDatabase == null) {
            myAppDatabase = getInstance(GlobalClass.getInstance());
        }
        return myAppDatabase.userDao();
    }

    public static MediaDao getMediaDao() {
        if (myAppDatabase == null) {
            myAppDatabase = getInstance(GlobalClass.getInstance());
        }
        return myAppDatabase.mediaDao();
    }

    public static HiddenDao getHiddenDao() {
        if (myAppDatabase == null) {
            myAppDatabase = getInstance(GlobalClass.getInstance());
        }
        return myAppDatabase.hiddenDao();
    }
}
