package com.dharmapoudel.samfix;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.provider.Settings.Global;
import android.provider.Settings.Secure;
import android.provider.Settings.System;
import android.widget.Toast;

public class Util {
    private static final String PERMISSION = "android.permission.WRITE_SECURE_SETTINGS";
    private static final String COMMAND    = "adb shell pm grant " + BuildConfig.APPLICATION_ID + " " + PERMISSION;
    private static final String SU_COMMAND = "su -c pm grant " + BuildConfig.APPLICATION_ID + " " + PERMISSION;

    private static final String DISPLAY_DALTONIZER_ENABLED = "accessibility_display_daltonizer_enabled";
    private static final String DISPLAY_DALTONIZER         = "accessibility_display_daltonizer";

    private static final String AUDIO_SAFE_VOLUME_STATE    = "audio_safe_volume_state";
    private static final String MAX_BRIGHTNESS_DIALOG    = "shown_max_brightness_dialog";

    public static boolean hasPermission(Context context) {
        return context.checkCallingOrSelfPermission(PERMISSION) == PackageManager.PERMISSION_GRANTED;
    }

    public static Dialog createTipsDialog(final Context context) {
        return new AlertDialog.Builder(context, android.R.style.Theme_Material_Light_Dialog_Alert)
                .setTitle(R.string.tips_title)
                .setMessage(context.getString(R.string.tips, COMMAND))
                .setNegativeButton(R.string.tips_ok, null)
                .setPositiveButton(R.string.tips_copy, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ClipData clipData = ClipData.newPlainText(COMMAND, COMMAND);
                        ClipboardManager manager = (ClipboardManager) context.getSystemService(Service.CLIPBOARD_SERVICE);
                        manager.setPrimaryClip(clipData);
                        Toast.makeText(context, R.string.copy_done, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNeutralButton("root", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            Runtime.getRuntime().exec(SU_COMMAND).waitFor();
                            toggleGreyscale(context, !isGreyscaleEnabled(context));
                        } catch (Exception e) {
                            Toast.makeText(context, R.string.root_failure, Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                })
                .create();
    }

    public static boolean isGreyscaleEnabled(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        return Secure.getInt(contentResolver, DISPLAY_DALTONIZER_ENABLED, 0) == 1
                && Secure.getInt(contentResolver, DISPLAY_DALTONIZER, 0) == 0;
    }

    public static void toggleGreyscale(Context context, boolean value) {
        ContentResolver contentResolver = context.getContentResolver();
        Secure.putInt(contentResolver, DISPLAY_DALTONIZER_ENABLED, value ? 0 : 1);
        Secure.putInt(contentResolver, DISPLAY_DALTONIZER, value ? -1 : 0);
    }

    public static void toggleMaxVolumeWarning(Context context, boolean value) {
        Global.putInt(context.getContentResolver(), AUDIO_SAFE_VOLUME_STATE, value ? 3 : 2);
    }

    public static void toggleMaxBrightnessWarning(Context context, boolean value) {

        System.putInt(context.getContentResolver(), MAX_BRIGHTNESS_DIALOG, value ? 0 : 1);
    }

    public static void setAnimationScale(Context context, float value) {
        Global.putFloat(context.getContentResolver(), "window_animation_scale", value);
        Global.putFloat(context.getContentResolver(), "transition_animation_scale", value);
        Global.putFloat(context.getContentResolver(), "animator_duration_scale", value);
    }
}
