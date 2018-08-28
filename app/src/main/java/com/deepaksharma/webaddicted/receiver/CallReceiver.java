package com.deepaksharma.webaddicted.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.deepaksharma.webaddicted.ui.folder.HomeActivity;

/**
 * Created by deepaksharma on 28/8/18.
 */

public class CallReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startActivity(new Intent(context.getApplicationContext(), HomeActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }
}
