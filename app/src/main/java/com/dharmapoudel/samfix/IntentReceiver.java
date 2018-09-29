package com.dharmapoudel.samfix;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class IntentReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Preferences pref = new Preferences(context);

        String action = intent.getAction();

        Toast.makeText(context, "Received boot completed ", Toast.LENGTH_SHORT).show();

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
            Log.i("IntentReceiver", "Received boot completed ");
            Intent delayedIntent = new Intent(context, IntentReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, delayedIntent, 0);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 30000, pendingIntent);
        }
    }
}
