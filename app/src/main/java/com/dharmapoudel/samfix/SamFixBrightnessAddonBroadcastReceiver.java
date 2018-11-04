package com.dharmapoudel.samfix;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SamFixBrightnessAddonBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String act = intent.getAction();
            if(act != null && act.equals(context.getResources().getString(R.string.samfix_brightness_broadcast_intent)) ) {
                boolean value = intent.getBooleanExtra("brightness_value", false);

                //save the value to the preferences
                Preferences pref = new Preferences(context);
                pref.savePreference("pref_disable_max_brightness_warning", value);

                Log.i(SamFixBrightnessAddonBroadcastReceiver.class.getSimpleName(), "SamFix brightness broadcast sent from addon is received with value : " + value);
            }
        }
    }
