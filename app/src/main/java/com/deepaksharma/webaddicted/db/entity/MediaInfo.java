package com.deepaksharma.webaddicted.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.deepaksharma.webaddicted.utils.BackupConstant;

/**
 * Created by deepaksharma on 24/8/18.
 */
@Entity(tableName = BackupConstant.MEDIA_INFO_TABLE_NAME)
public class MediaInfo{//} implements Comparator<>{
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "file_name")
    private String name;
    @ColumnInfo(name = "upload_date")
    private long uploadDate;
    @ColumnInfo(name = "drive_id")
    private String driveId;
    @ColumnInfo(name = "file_status")
    private String fileStatus;
    @ColumnInfo(name = "file_size")
    private long fileSize;
    @ColumnInfo(name = "folder_name")
    private String folderName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(long uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String getDriveId() {
        return driveId;
    }

    public void setDriveId(String driveId) {
        this.driveId = driveId;
    }

    public String getFileStatus() {
        return fileStatus;
    }

    public void setFileStatus(String fileStatus) {
        this.fileStatus = fileStatus;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }
//Comprator
//    @Override
//    public int compare(MediaInfo o1, MediaInfo o2) {
//        return o1.getName().compareTo(o2.getName());
//    }
//comparable
//    @Override
//    public int compareTo(@NonNull MediaInfo o) {
//        return this.id - id;
//    }
}
