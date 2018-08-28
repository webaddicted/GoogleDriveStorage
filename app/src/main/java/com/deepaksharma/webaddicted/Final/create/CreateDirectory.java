package com.deepaksharma.webaddicted.Final.create;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.deepaksharma.webaddicted.utils.BackupConstant;
import com.deepaksharma.webaddicted.GlobalClass;
import com.deepaksharma.webaddicted.ui.hidden.HiddenActivity;
import com.deepaksharma.webaddicted.R;
import com.deepaksharma.webaddicted.utils.PathUtil;
import com.deepaksharma.webaddicted.utils.Utilities;
import com.deepaksharma.webaddicted.databinding.ActivityCreateDirectoryBinding;
import com.deepaksharma.webaddicted.db.DBUtilites;
import com.deepaksharma.webaddicted.db.entity.UserInfo;
import com.deepaksharma.webaddicted.preference.PreferenceConstant;
import com.deepaksharma.webaddicted.preference.PreferenceUtil;
import com.deepaksharma.webaddicted.ui.folder.BaseDemoActivity;
import com.deepaksharma.webaddicted.ui.folder.HomeActivity;
import com.google.android.gms.drive.DriveId;
import java.io.File;
import java.util.concurrent.ExecutionException;
import androidx.work.WorkContinuation;
import static com.deepaksharma.webaddicted.utils.BackUpUtility.deleteWork;

public class CreateDirectory extends BaseDemoActivity implements View.OnClickListener {
    String TAG = CreateDirectory.class.getSimpleName();
    ActivityCreateDirectoryBinding createDirectoryBinding;
    CreateViewModel createViewModel;
    DriveId uploadDriveId;
    String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        createDirectoryBinding = DataBindingUtil.setContentView(this, R.layout.activity_create_directory);
        createViewModel = ViewModelProviders.of(this).get(CreateViewModel.class);
        createDirectoryBinding.createDb.setOnClickListener(this);
        createDirectoryBinding.createDirectory.setOnClickListener(this);
        createDirectoryBinding.deleteDirectory.setOnClickListener(this);
        createDirectoryBinding.drivePage.setOnClickListener(this);
        createDirectoryBinding.downloadFile.setOnClickListener(this);
        createDirectoryBinding.uploadFile.setOnClickListener(this);
        createDirectoryBinding.createFolder.setOnClickListener(this);
        createDirectoryBinding.uploadWorker.setOnClickListener(this);
        createDirectoryBinding.retriveWorker.setOnClickListener(this);
        createDirectoryBinding.removeMediaDb.setOnClickListener(this);
        createDirectoryBinding.hiddenFiles.setOnClickListener(this);
        createViewModel.initWorker();
//      all drive api work on background thread
//     Task.await is used to pull driver process in main thread
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.createDirectory:
                createViewModel.createFolderStructure(getDriveResourceClient());
                break;
            case R.id.deleteDirectory:
                if (createDirectoryBinding.edtDeleteDirectory.getText().toString() != null &&
                        createDirectoryBinding.edtDeleteDirectory.getText().toString().length() > 0)
                    createViewModel.deleteFolderExist(createDirectoryBinding.edtDeleteDirectory.getText().toString(), getDriveResourceClient());
                else Utilities.showMessage("Please enter field.");
                break;
            case R.id.uploadFile:
                uploadFile();
                break;
            case R.id.downloadFile:
                downloadFile();
                break;
            case R.id.drivePage:
                startActivity(new Intent(this, HomeActivity.class));
                break;
            case R.id.createFolder:
                createHiddenFolder();
                break;
            case R.id.createDb:
                createDb();
                break;
            case R.id.uploadWorker:
                deleteWork();
                WorkContinuation continuation =
                        createViewModel.mWorkManger.beginWith(createViewModel.checkGoogleSignIn);
                continuation = continuation.then(createViewModel.createDirectory);
                continuation = continuation.then(createViewModel.mediaUploadRequest);
                continuation = continuation.then(createViewModel.dbUploadDbRequest);
                continuation.enqueue();
                PreferenceUtil.getInstance().setPreference(PreferenceConstant.WORKER_CHECK_SIGN_IN, createViewModel.checkGoogleSignIn.getStringId());
                PreferenceUtil.getInstance().setPreference(PreferenceConstant.WORKER_CREATE_DIRECTORY, createViewModel.createDirectory.getStringId());
                PreferenceUtil.getInstance().setPreference(PreferenceConstant.WORKER_UPLOAD_MEDIA, createViewModel.mediaUploadRequest.getStringId());
                PreferenceUtil.getInstance().setPreference(PreferenceConstant.WORKER_UPLOAD_DB, createViewModel.dbUploadDbRequest.getStringId());
                break;
            case R.id.retriveWorker:
                deleteWork();
                WorkContinuation retriveContinuation =
                        createViewModel.mWorkManger.beginWith(createViewModel.checkGoogleSignIn);
                retriveContinuation = retriveContinuation.then(createViewModel.createDirectory);
                retriveContinuation = retriveContinuation.then(createViewModel.dbRetriveRequest);
                retriveContinuation = retriveContinuation.then(createViewModel.mediaRetrieveRequest);
                retriveContinuation.enqueue();
                PreferenceUtil.getInstance().setPreference(PreferenceConstant.WORKER_CHECK_SIGN_IN, createViewModel.checkGoogleSignIn.getStringId());
                PreferenceUtil.getInstance().setPreference(PreferenceConstant.WORKER_CREATE_DIRECTORY, createViewModel.createDirectory.getStringId());
                PreferenceUtil.getInstance().setPreference(PreferenceConstant.WORKER_RETRIVE_DB, createViewModel.dbRetriveRequest.getStringId());
                PreferenceUtil.getInstance().setPreference(PreferenceConstant.WORKER_RETRIVE_MEDIA, createViewModel.mediaRetrieveRequest.getStringId());
                break;
            case R.id.removeMediaDb:
                DBUtilites.getMediaDao().mediaDeleteData();
                showMessage("Media all details removed.");
                break;
            case R.id.hiddenFiles:
             startActivity(new Intent(this, HiddenActivity.class));
                break;
        }
    }

    private void createDb() {
        for (int i = 0; i < 5; i++) {
            UserInfo userInfo = new UserInfo();
            userInfo.setName("Deepak_" + System.currentTimeMillis());
            userInfo.setMobileno("9024061407_" + System.currentTimeMillis());
            DBUtilites.getDbDao().insertUser(userInfo);
        }
        showMessage("Db Successfully created.");
    }

    private void createHiddenFolder() {
        createViewModel.createHideFolder("Deepak1115", getDriveResourceClient(), new CallbackListener() {
            @Override
            public void Success(DriveId driveId) {
                showMessage("Folder created successfully.");
            }

            @Override
            public void Failure() {
                showMessage("Folder creation failed.");
            }
        });
    }

    private void downloadFile() {
        if (fileName != null) {
            createViewModel.isFolderExist(fileName, getDriveResourceClient(), new CallbackListener() {
                @Override
                public void Success(DriveId driveId) {
                    try {
                        BackupConstant.showDialog(CreateDirectory.this, "Downloading...");
                        createViewModel.retrieveDriveData(driveId.asDriveFile(), BackupConstant.getParentFolder().toString() + "/" + fileName, getDriveResourceClient());
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
        } else {
            showMessage("please upload file first");
        }
    }

    private void uploadFile() {
        createViewModel.isFolderExist(BackupConstant.parentFolderName, getDriveResourceClient(), new CallbackListener() {
            @Override
            public void Success(DriveId driveId) {
                uploadDriveId = driveId;
                if (isDriveInitialized()) {
                    showMessage("isDriveInitialized() true");
                    init();
                    pickFile();
                } else {
                    showMessage("isDriveInitialized() false");
                    init();
                }
            }

            @Override
            public void Failure() {
                showMessage("Folder not exist.");
                createViewModel.createFolderStructure(getDriveResourceClient());
            }
        });
    }

    @Override
    protected void onDriveClientReady() {
    }

    public interface CallbackListener {
        void Success(DriveId driveId);

        void Failure();
    }

    private void pickFile() {
        Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.setType("*/*");
        chooseFile.putExtra("return-data", false);
        startActivityForResult(Intent.createChooser(chooseFile, "Choose a file"),
                BackupConstant.PICKFILE_RESULT_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BackupConstant.PICKFILE_RESULT_CODE && resultCode == Activity.RESULT_OK) {
            BackupConstant.showDialog(CreateDirectory.this, "uploading file.");
            Uri content_describer = data.getData();
            Log.d(TAG, "onActivityResult: " + PathUtil.getPath(this, content_describer));
            File filePath = PathUtil.getPath(this, content_describer);
            fileName = filePath.getName();
            createViewModel.uploadFile(filePath, uploadDriveId, getDriveResourceClient());
        }
    }

    public static String getPath(Uri uri) {
        String path = null;
        String[] projection = {MediaStore.Files.FileColumns.DATA};
        Cursor cursor = GlobalClass.getInstance().getContentResolver().query(uri, projection, null, null, null);

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

}