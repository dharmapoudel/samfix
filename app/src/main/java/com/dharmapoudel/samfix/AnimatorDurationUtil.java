package com.dharmapoudel.samfix;

import android.content.Context;
import android.provider.Settings;
import android.support.annotation.FloatRange;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

public class AnimatorDurationUtil {

    private static final String TAG = "AnimatorDurationUtil";


    public static float[] scales = {0f, 0.25f, 0.5f, 1f, 1.5f, 2f, 5f, 10f};
    public static int[] scaleIcons = {
            R.drawable.ic_animator_duration_off,
            R.drawable.ic_animator_duration_1_4x,
            R.drawable.ic_animator_duration_half_x,
            R.drawable.ic_animator_duration_1x,
            R.drawable.ic_animator_duration_1_5x,
            R.drawable.ic_animator_duration_2x,
            R.drawable.ic_animator_duration_5x,
            R.drawable.ic_animator_duration_10x
    };


    public static int getIndex(float scale) {
        int index = 0;
        if (scale <= 0f) {
            index = 0;
        } else if (scale <= 0.25f) {
            index = 1;
        } else if (scale <= 0.5f) {
            index = 2;
        } else if (scale <= 1f) {
            index = 3;
        } else if (scale <= 1.5f) {
            index = 4;
        } else if (scale <= 2f) {
            index = 5;
        } else if (scale <= 5f) {
            index = 6;
        } else if (scale <= 10f) {
            index = 7;
        }
        return index;
    }

    public static float getScale(@IdRes int id) {
        switch (id) {
            case R.id.scale_off:    return 0f;
            case R.id.scale_1_4:    return 0.25f;
            case R.id.scale_0_5:    return 0.5f;
            case R.id.scale_1:      return 1.0f;
            case R.id.scale_1_5:    return 1.5f;
            case R.id.scale_2:      return 2f;
            case R.id.scale_5:      return 5f;
            case R.id.scale_10:     return 10f;
            default:                return 1.0f;
        }
    }

    public static @IdRes int getScaleItemId(@FloatRange(from = 0.0, to = 10.0) float scale) {
        if (scale <= 0f) {
            return R.id.scale_off;
        } else if (scale <= 0.25f) {
            return R.id.scale_1_4;
        } else if (scale <= 0.5f) {
            return R.id.scale_0_5;
        } else if (scale <= 1f) {
            return R.id.scale_1;
        } else if (scale <= 1.5f) {
            return R.id.scale_1_5;
        } else if (scale <= 2f) {
            return R.id.scale_2;
        } else if (scale <= 5f) {
            return R.id.scale_5;
        } else {
            return R.id.scale_10;
        }
    }

    public static float getAnimatorScale(Context context) {
        float scale = 1f;
        try {
            scale = Settings.Global.getFloat(context.getContentResolver(), Settings.Global.ANIMATOR_DURATION_SCALE);
        } catch (Settings.SettingNotFoundException e) {
            Log.e(TAG, "Could not read Animator Duration Scale setting", e);
        }
        return scale;
    }

    public static boolean setAnimatorScale(@NonNull Context context, @FloatRange(from = 0.0, to = 10.0) float scale) {
        try {

            Settings.Global.putFloat(context.getContentResolver(), Settings.Global.ANIMATOR_DURATION_SCALE, scale);
            Settings.Global.putFloat(context.getContentResolver(), Settings.Global.TRANSITION_ANIMATION_SCALE, scale);
            Settings.Global.putFloat(context.getContentResolver(), Settings.Global.WINDOW_ANIMATION_SCALE, scale);
            return true;

        } catch (SecurityException se) {

            String message = context.getString(R.string.tips_title);
            Toast.makeText(context.getApplicationContext(), message, Toast.LENGTH_LONG).show();
            Log.d(TAG, message);
            return false;

        }
    }

}
