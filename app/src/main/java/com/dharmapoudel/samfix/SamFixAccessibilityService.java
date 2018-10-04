package com.dharmapoudel.samfix;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

public class SamFixAccessibilityService extends AccessibilityService {

    private Preferences mPrefs;

    private static final String TAG = SamFixAccessibilityService.class.getSimpleName();

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        mPrefs = new Preferences(this);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        Log.i(TAG, accessibilityEvent.toString());
        mPrefs = new Preferences(this);


        if (mPrefs.pref_no_popup_on_bt_wifi) {
            if (accessibilityEvent.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
                String accessibilityEventPackageName = (String) accessibilityEvent.getPackageName();
                if (accessibilityEventPackageName.equals("com.android.settings")) {
                    if (accessibilityEvent.getClassName().toString().equals("android.app.Dialog")) {
                        List<CharSequence> texts = accessibilityEvent.getText();
                        if (texts.get(0) != null && texts.get(0).toString().toLowerCase().equals("bluetooth")) {
                            List<AccessibilityNodeInfo> nodeInfos = getRootInActiveWindow().findAccessibilityNodeInfosByViewId("android:id/button1");
                            for (AccessibilityNodeInfo nodeInfo : nodeInfos)
                                nodeInfo.performAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_CLICK.getId());
                        }

                    }

                    if (accessibilityEvent.getClassName().toString().equals("com.samsung.android.settings.wifi.WifiPickerDialog")) {
                        List<CharSequence> texts = accessibilityEvent.getText();
                        if (texts.get(0) != null && texts.get(0).toString().toLowerCase().equals("wi-fi")) {
                            List<AccessibilityNodeInfo> nodeInfos = getRootInActiveWindow().findAccessibilityNodeInfosByViewId("com.android.settings:id/wifi_picker_dialog_cancel");
                            for (AccessibilityNodeInfo nodeInfo : nodeInfos)
                                nodeInfo.performAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_CLICK.getId());
                        }

                    }
                }
            }
        }

        if (mPrefs.pref_no_popup_gm_location) {
            if (accessibilityEvent.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
                if (accessibilityEvent.getClassName().toString().equals("com.google.android.location.settings.LocationSettingsCheckerActivity")) {
                    clickButton("android:id/button2");
                } else if (accessibilityEvent.getClassName().toString().equals("com.google.android.location.network.ConfirmAlertActivity")) {
                    clickButton("android:id/button1");
                }
            }
        }


    }

    void clickButton(final String viewId) {
        final List<AccessibilityNodeInfo> nodeInfos = getRootInActiveWindow().findAccessibilityNodeInfosByViewId(viewId);
        for (AccessibilityNodeInfo nodeInfo : nodeInfos)
            nodeInfo.performAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_CLICK.getId());
    }

    @Override
    public void onInterrupt() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG , "onStartCommand");
        mPrefs = new Preferences(this);
        return super.onStartCommand(intent, flags, startId);
    }

}
