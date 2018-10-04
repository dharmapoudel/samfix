package com.dharmapoudel.samfix.autobackup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.dharmapoudel.samfix.Preferences;

public class BackupReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Preferences pref = new Preferences(context);
        if (pref.pref_auto_backup) {
            String pkg = intent.getData().getSchemeSpecificPart();
            Log.i("BackupReeciver", pkg);

            BackupTask backupTask = new BackupTask(context);
            backupTask.execute(pkg);
        }
    }
}
