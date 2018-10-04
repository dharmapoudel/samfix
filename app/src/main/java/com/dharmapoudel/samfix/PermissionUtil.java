package com.dharmapoudel.samfix;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public final class PermissionUtil {

    final static int WRITE_EXTERNAL = 25;

    final static String[] storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};


    public static boolean isStoragePermissionAvailable(Context context){
        return checkPermissions(context, storagePermissions);
    }


    private static boolean checkPermissions(Context context, String... permissions) {
        for (String permission : permissions) {
            if (!checkPermission(context, permission)) {
                return false;
            }
        }
        return true;
    }

    private static boolean checkPermission(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestPermissions(Activity activity, int permissionId) {
        ActivityCompat.requestPermissions(activity, storagePermissions, permissionId);
    }

    public static void askForPermission(final Activity activity) {
        if (!PermissionUtil.isStoragePermissionAvailable(activity)) {
            PermissionUtil.requestPermissions(activity, WRITE_EXTERNAL);
        }
    }
}