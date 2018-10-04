package com.dharmapoudel.samfix;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

public class BootIntentReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Preferences pref = new Preferences(context);

        String action = intent.getAction();

        Toast.makeText(context, "Received boot completed " + action, Toast.LENGTH_SHORT).show();

        if (action == null) {

            if(pref.pref_disable_max_volume_warning )
                Util.toggleMaxVolumeWarning(context, false);

            if(pref.pref_disable_max_brightness_warning )
                Util.toggleMaxBrightnessWarning(context, false);

            if(pref.pref_enable_greyscale )
                Util.toggleMaxBrightnessWarning(context, false);

            //AnimatorDurationUtil.setAnimatorScale(context, pref.pref_animation_duration);


        } else if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            //Toast.makeText(context, "----------------Received boot completed --------------", Toast.LENGTH_SHORT).show();
            Log.i("BootIntentReceiver", "Received boot completed ");
            Intent delayedIntent = new Intent(context, BootIntentReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, delayedIntent, 0);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 30000, pendingIntent);

            //schedule
            if(pref.pref_disable_max_volume_warning ) {
                scheduleSettingsUpdateJob(context);
            }
        }
    }


    public static void scheduleSettingsUpdateJob(Context context) {
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.cancelAll();
        jobScheduler.schedule(new JobInfo.Builder(0, new ComponentName(context, SettingsUpdateJobService.class))
                .setPersisted(false)
                .setPeriodic(TimeUnit.HOURS.toMillis(1))
                .setRequiresDeviceIdle(false)
                .setRequiresCharging(false)
                .build());
    }

    public static void unscheduleSettingsUpdateJob(Context context) {
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.cancelAll();
    }
}
