package com.deepaksharma.webaddicted.Final.create;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.deepaksharma.webaddicted.Final.BackupConstant;
import com.deepaksharma.webaddicted.R;
import com.deepaksharma.webaddicted.databinding.ActivityCreateDirectoryBinding;
import com.deepaksharma.webaddicted.ui.folder.BaseDemoActivity;
import com.deepaksharma.webaddicted.ui.folder.HomeActivity;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class CreateDirectory extends BaseDemoActivity implements View.OnClickListener {
    String TAG = CreateDirectory.class.getSimpleName();
    ActivityCreateDirectoryBinding createDirectoryBinding;
    CreateViewModel createViewModel;
    DriveId uploadDriveId;
    String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createDirectoryBinding = DataBindingUtil.setContentView(this, R.layout.activity_create_directory);
//        createViewModel = ViewModelProviders.of(this).get(CreateViewModel.class);

        createDirectoryBinding.createDirectory.setOnClickListener(this);
        createDirectoryBinding.deleteDirectory.setOnClickListener(this);
        createDirectoryBinding.drivePage.setOnClickListener(this);
        createDirectoryBinding.downloadFile.setOnClickListener(this);
        createDirectoryBinding.uploadFile.setOnClickListener(this);
        createDirectoryBinding.createFolder.setOnClickListener(this);


//      all drive api work on background thread
//     Task.await is used to pull driver process in main thread
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.createDirectory:
                createFolderStructure();
                break;
            case R.id.deleteDirectory:
                if (createDirectoryBinding.edtDeleteDirectory.getText().toString() != null &&
                        createDirectoryBinding.edtDeleteDirectory.getText().toString().length() > 0)
                    deleteFolderExist(createDirectoryBinding.edtDeleteDirectory.getText().toString());
                break;
            case R.id.uploadFile:
                isFolderExist(BackupConstant.parentFolderName, new CallbackListener() {
                    @Override
                    public void Success(DriveId driveId) {
                        uploadDriveId = driveId;
                        if (isDriveInitialized()) {
                            Toast.makeText(CreateDirectory.this, "isDriveInitialized() true", Toast.LENGTH_SHORT).show();
                            init();
                            pickFile();
                        } else {
                            Toast.makeText(CreateDirectory.this, "isDriveInitialized() false", Toast.LENGTH_SHORT).show();
                            init();
                        }
                    }

                    @Override
                    public void Failure() {
                        showMessage("Folder not exist.");
                        createFolderStructure();
                    }
                });

                break;
            case R.id.downloadFile:
                if (fileName != null) {
                    isFolderExist(fileName, new CallbackListener() {
                        @Override
                        public void Success(DriveId driveId) {
                            try {
                                BackupConstant.showDialog(CreateDirectory.this,"Downloading...");
                                retrieveDriveData(driveId.asDriveFile(), BackupConstant.getFolderPath() + fileName);
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void Failure() {
                            showMessage("file not exist");
                        }
                    });
                }else{
                    showMessage("please upload file first");
                }
                break;
            case R.id.drivePage:
                startActivity(new Intent(this, HomeActivity.class));
                break;
            case R.id.createFolder:
                createHideFolder("Deepak1115", new CallbackListener() {
                    @Override
                    public void Success(DriveId driveId) {
                        showMessage("Folder created successfully.");
                    }

                    @Override
                    public void Failure() {

                    }
                });
                break;
        }
    }

    @Override
    protected void onDriveClientReady() {
    }

    private void createFolderStructure() {
        isFolderExist(BackupConstant.parentFolderName, new CallbackListener() {
            @Override
            public void Success(DriveId driveId) {
                if (driveId == null)
                    Toast.makeText(CreateDirectory.this, "Folder not exist.", Toast.LENGTH_SHORT).show();
                else
                    createChildFolder(driveId);
            }

            @Override
            public void Failure() {
                createFolder(BackupConstant.parentFolderName, new CallbackListener() {
                    @Override
                    public void Success(DriveId driveId) {
                        createChildFolder(driveId);
                    }

                    @Override
                    public void Failure() {

                    }
                });
            }
        });
    }

    private void isFolderExist(String folderName, CallbackListener callbackListener) {
        Task<MetadataBuffer> queryTask = null;
        Query query = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.TITLE, folderName))
                .build();
        queryTask = getDriveResourceClient().query(query);

        queryTask
                .addOnSuccessListener(this,
                        metadataBuffer -> {
                            Metadata metadata = null;
                            for (Metadata mss : metadataBuffer) {
                                if (mss.getTitle().equals(folderName)) {
//                                    strDriveId = mss.getDriveId().encodeToString();
//                                        DriveId.decodeFromString()
//                                    DriveFolder driveFolderName = mss.getDriveId().asDriveFolder();
                                    metadata = mss;
                                    break;
                                }
                            }
                            if (metadata != null)
                                callbackListener.Success(metadata.getDriveId());
                            else
                                callbackListener.Failure();
                        })
                .addOnFailureListener(this, e -> {
                    Log.e(TAG, "Error retrieving files", e);
                });
    }

    private void createFolder(String folderName, CallbackListener callbackListener) {
//        getRootFolder is used for show folder
//        getAppFolder is used for hide folder
        getDriveResourceClient()
                .getRootFolder()
                .continueWithTask(task -> {
                    DriveFolder parentFolder = task.getResult();
                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setTitle(folderName)
                            .setMimeType(DriveFolder.MIME_TYPE)
                            .setStarred(true)
                            .build();
                    return CreateDirectory.this.getDriveResourceClient().createFolder(parentFolder, changeSet);
                })
                .addOnSuccessListener(this,
                        driveFolder -> {
                            if (driveFolder != null) {
                                callbackListener.Success(driveFolder.getDriveId());
                            }
                            showMessage(getString(R.string.file_created,
                                    driveFolder.getDriveId().encodeToString()));
                        })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Unable to create single folder file", e);
                        showMessage(CreateDirectory.this.getString(R.string.file_create_error));
                    }
                });
    }

    private void createHideFolder(String folderName, CallbackListener callbackListener) {
//        getRootFolder is used for show folder
//        getAppFolder is used for hide folder
        getDriveResourceClient()
                .getAppFolder()
                .continueWithTask(task -> {
                    DriveFolder parentFolder = task.getResult();
                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setTitle(folderName)
                            .setMimeType(DriveFolder.MIME_TYPE)
                            .setStarred(true)
                            .build();
                    return CreateDirectory.this.getDriveResourceClient().createFolder(parentFolder, changeSet);
                })
                .addOnSuccessListener(this,
                        driveFolder -> {
                            if (driveFolder != null) {
                                callbackListener.Success(driveFolder.getDriveId());
                            }
                            showMessage(getString(R.string.file_created,
                                    driveFolder.getDriveId().encodeToString()));
                        })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Unable to create single folder file", e);
                        showMessage(CreateDirectory.this.getString(R.string.file_create_error));
                    }
                });
    }

    private void createFolderInFolder(String folderName, final DriveFolder parent, CallbackListener callbackListener) {
        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                .setTitle(folderName)
                .setMimeType(DriveFolder.MIME_TYPE)
                .setStarred(true)
                .build();
        getDriveResourceClient()
                .createFolder(parent, changeSet)
                .addOnSuccessListener(this,
                        driveFolder -> {
                            if (driveFolder != null) {
                                callbackListener.Success(driveFolder.getDriveId());
                            }
                            showMessage(getString(R.string.file_created,
                                    driveFolder.getDriveId().encodeToString()));
                        })
                .addOnFailureListener(this, e -> {
                    Log.e(TAG, "Unable to create folder in folder file", e);
                    showMessage(CreateDirectory.this.getString(R.string.file_create_error));
                });
    }

    private void deleteFolderExist(String folderName) {
        Query query = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.TITLE, folderName))
                .build();
        Task<MetadataBuffer> queryTask = getDriveResourceClient().query(query);
        queryTask
                .addOnSuccessListener(this,
                        metadataBuffer -> {
                            for (Metadata mss : metadataBuffer) {
                                if (mss.getTitle().equals(folderName)) {
                                    deleteFile(mss.getDriveId().asDriveFolder(), null);
                                }
                            }
                        })
                .addOnFailureListener(this, e -> {
                    Log.e(TAG, "Error retrieving files", e);
                });
    }

    private void deleteFileExist(String folderName) {
        Query query = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.TITLE, folderName))
                .build();
        Task<MetadataBuffer> queryTask = getDriveResourceClient().query(query);
        queryTask
                .addOnSuccessListener(this,
                        new OnSuccessListener<MetadataBuffer>() {
                            @Override
                            public void onSuccess(MetadataBuffer metadataBuffer) {
                                for (Metadata mss : metadataBuffer) {
                                    if (mss.getTitle().equals(folderName)) {
                                        deleteFile(null, mss.getDriveId().asDriveFile());
                                    }
                                }
                            }
                        })
                .addOnFailureListener(this, e -> Log.e(TAG, "Error retrieving files", e));
    }

    private void deleteFile(DriveFolder driveFolder, DriveFile driveFile) {
        getDriveResourceClient()
                .delete(driveFolder != null ? driveFolder : driveFile)
                .addOnSuccessListener(this,
                        aVoid -> showMessage(CreateDirectory.this.getString(R.string.file_deleted)))
                .addOnFailureListener(this, e -> {
                    Log.e(TAG, "Unable to delete file", e);
                    showMessage(CreateDirectory.this.getString(R.string.delete_failed));
                });
    }

    Task<DriveFolder> uploadFileInFolder = null;

    private void uploadFile(File filePath, DriveId driveId) {
        if (driveId == null)
            uploadFileInFolder = getDriveResourceClient().getRootFolder();
        final Task<DriveContents> createContentsTask = getDriveResourceClient().createContents();
        createContentsTask
                .continueWithTask(task -> {
                    DriveFolder parent;
                    if (driveId == null)
                        parent = uploadFileInFolder.getResult();
                    else
                        parent = driveId.asDriveFolder();
                    DriveContents contents = createContentsTask.getResult();
                    OutputStream outputStream = contents.getOutputStream();
//                    inFileName is path of file
                    final String inFileName = filePath.toString();

                    try {
                        File uploadFile = new File(inFileName);
                        FileInputStream fis = new FileInputStream(uploadFile);
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = fis.read(buffer)) > 0) {
                            outputStream.write(buffer, 0, length);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setTitle(filePath.getName())
                            .setStarred(false)
                            .build();

                    return getDriveResourceClient().createFile(parent, changeSet, contents);
                })
                .addOnSuccessListener(this,
                        driveFile -> {
                            CreateDirectory.this.showMessage(getString(R.string.file_created,
                                    driveFile.getDriveId().encodeToString()));
                            showMessage("File successfully uploaded.");
                            BackupConstant.hideDialog();
                        })

                .addOnFailureListener(this, e -> {
                    Log.e(TAG, "Unable to create file", e);
                    showMessage(CreateDirectory.this.getString(R.string.file_create_error));
                    BackupConstant.hideDialog();
                });
    }

    private void createChildFolder(DriveId driveId) {
        List<String> childLiat = BackupConstant.getChildFolder();
        for (String folderName : childLiat) {
            isFolderExist(folderName, new CallbackListener() {
                @Override
                public void Success(DriveId driveId) {
                    Log.d(TAG, "Success: Folder created -> ");
                }

                @Override
                public void Failure() {
                    createFolderInFolder(folderName, driveId.asDriveFolder(), new CallbackListener() {
                        @Override
                        public void Success(DriveId driveId) {
                            showMessage("folder successfully created "+driveId);
                            Log.d(TAG, "Success : -> " + driveId);
                        }

                        @Override
                        public void Failure() {
                            Toast.makeText(CreateDirectory.this, "Folder creation filed", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Failure: -> ");
                        }
                    });
                }
            });
        }

    }

    public interface CallbackListener {
        void Success(DriveId driveId);
        void Failure();
    }

    private void pickFile() {
        Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.setType("*/*");
        startActivityForResult(Intent.createChooser(chooseFile, "Choose a file"),
                BackupConstant.PICKFILE_RESULT_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BackupConstant.PICKFILE_RESULT_CODE && resultCode == Activity.RESULT_OK) {
            BackupConstant.showDialog(CreateDirectory.this, "uploading file.");
            Uri content_describer = data.getData();
            Log.d(TAG, "onActivityResult: " + getPath(content_describer));
            File filePath = new File(getPath(content_describer));
            fileName = filePath.getName();
            uploadFile(filePath, uploadDriveId);

        }
    }

    public String getPath(Uri uri) {
        String path = null;
        String[] projection = {MediaStore.Files.FileColumns.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);

        if (cursor == null) {
            path = uri.getPath();
        } else {
            cursor.moveToFirst();
            int column_index = cursor.getColumnIndexOrThrow(projection[0]);
            path = cursor.getString(column_index);
            cursor.close();
        }

        return ((path == null || path.isEmpty()) ? (uri.getPath()) : path);
    }

    public String retrieveDriveData(DriveFile driveFile, String file) throws ExecutionException, InterruptedException {
        //DB Path
        Task<DriveContents> openFileTask = getDriveResourceClient().openFile(driveFile, DriveFile.MODE_READ_ONLY);
//      stop task for work in background
//        DriveContents driveContents = Tasks.await(openFileTask);
        Task<Void> discard = openFileTask
                .continueWithTask(task -> {
                    DriveContents contents = task.getResult();
                    try {
                        ParcelFileDescriptor parcelFileDescriptor = contents.getParcelFileDescriptor();
                        FileInputStream fileInputStream = new FileInputStream(parcelFileDescriptor.getFileDescriptor());

                        // Open the empty db as the output stream
                        OutputStream output = new FileOutputStream(file);

                        // Transfer bytes from the inputfile to the outputfile
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = fileInputStream.read(buffer)) > 0) {
                            output.write(buffer, 0, length);
                        }

                        // Close the streams
                        output.flush();
                        output.close();
                        fileInputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return getDriveResourceClient().discardContents(contents);

                }).addOnSuccessListener(aVoid -> {
                    showMessage("File successfully downloaded.");
                    BackupConstant.hideDialog();
                });
//        Tasks.await(discard);
        return file;

    }

}
