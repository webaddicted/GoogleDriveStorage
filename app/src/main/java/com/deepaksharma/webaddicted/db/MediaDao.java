package com.deepaksharma.webaddicted.db;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
/**
 * Created by deepaksharma on 27/8/18.
 */
@Dao
public interface MediaDao {
    @Query("SELECT drive_id FROM media_info WHERE file_name =:fileName")
    String getGoogleDriveId(String fileName);


//    @Query("SELECT * FROM media_info")
//    public List<MediaInfo> getMediaInfo();
//
//    @Insert
//    public void insertMedia(MediaInfo mediaInfo);
}
