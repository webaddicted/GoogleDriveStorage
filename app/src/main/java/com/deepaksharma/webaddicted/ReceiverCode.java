package com.deepaksharma.webaddicted;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.deepaksharma.webaddicted.ui.folder.HomeActivity;

/**
 * Created by deepaksharma on 27/8/18.
 */

public class ReceiverCode extends BroadcastReceiver {
    String TAG = ReceiverCode.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: ");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "onReceive: ertetetrete");
                Intent intent1 = new Intent(context.getApplicationContext(), HomeActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent1);
            }
        }, 7000);
    }
}
