package com.dharmapoudel.samfix;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dharmapoudel.samfix.autobackup.BackupReceiver;

import static android.content.Intent.FLAG_RECEIVER_FOREGROUND;
import static com.dharmapoudel.samfix.BootIntentReceiver.scheduleSettingsUpdateJob;
import static com.dharmapoudel.samfix.BootIntentReceiver.unscheduleSettingsUpdateJob;

public class MainActivity extends AppCompatActivity {

    private Preferences pref;
    private Context context;
    private static Context mContext;

    private SamFixBrightnessAddonBroadcastReceiver samFixBrightnessAddonBroadcastReceiver;
    private BackupReceiver backupReceiver;
    private PremiumCheckBroadcastReceiver premiumCheckReceiver;
    private static boolean licenseCheckBroadcastSent;


    private static View maxBrightnessToggle, data, backup, location_popup, bt_popup, wifi_popup, sync_popup;


    private static final String TAG = MainActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //action bar settings
        ActionBar actionBar = getSupportActionBar();
        actionBar.setElevation(0);
        actionBar.hide();


        //register the samfix brightness addon broadcast receiver
        samFixBrightnessAddonBroadcastReceiver = new SamFixBrightnessAddonBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(getResources().getString(R.string.samfix_brightness_broadcast_intent));
        registerReceiver(samFixBrightnessAddonBroadcastReceiver, filter);

        backupReceiver = new BackupReceiver();
        IntentFilter i = new IntentFilter();
        i.addAction("android.intent.action.PACKAGE_ADDED");
        registerReceiver(backupReceiver, i);

        premiumCheckReceiver = new PremiumCheckBroadcastReceiver();
        IntentFilter i2 = new IntentFilter();
        i2.addAction(getString(R.string.samfix_filter_action));
        registerReceiver(premiumCheckReceiver, i2);


        mContext = this;
        context = getApplicationContext();
        pref = new Preferences(context);

        licenseCheckBroadcastSent = false;

    }

    @Override
    protected void onResume() {
        super.onResume();
        mContext = this;
        context = getApplicationContext();
        pref = new Preferences(context);

        if (!Util.hasPermission(this)) {

            Dialog dialog = Util.createTipsDialog(this);
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    finish();
                }
            });
            dialog.show();

        } else {

            sendLicenseCheckBroadCast();

            //Toast.makeText(getApplicationContext(), "Billing initialized!", Toast.LENGTH_SHORT).show();
            data = findViewById(R.id.data);
            backup = findViewById(R.id.backup);
            location_popup = findViewById(R.id.location_popup);
            bt_popup = findViewById(R.id.bt_popup);
            wifi_popup = findViewById(R.id.wifi_popup);
            sync_popup = findViewById(R.id.sync_popup);

            if(!pref.pref_license_check_broadcast_value){
                disableToggles();
            }

            //automatically add to accessibility services
            updateAccessibilityServices();

            //set grey scale switch
            if(pref.pref_enable_greyscale) {
                //Util.toggleMaxBrightnessWarning(context, false);
                View greyScaleToggle = findViewById(R.id.greyscale_toggle);
                greyScaleToggle.setBackground(getDrawable(R.drawable.toggle_on));
            }

            //set the data toggle switch
            if(Util.isDataToggled(context)) {
                Util.toggleData(context, false);
                View dataToggle = findViewById(R.id.data_toggle);
                dataToggle.setBackground(getDrawable(R.drawable.toggle_on));
            }

            //set max volume warning switch
            if(pref.pref_disable_max_volume_warning) {
                Util.toggleMaxVolumeWarning(context, false);
                View maxVolumeToggle = findViewById(R.id.max_volume_toggle);
                maxVolumeToggle.setBackground(getDrawable(R.drawable.toggle_on));
            }

            //set max brightness warning switch
            maxBrightnessToggle = findViewById(R.id.max_brightness_toggle);
            if(pref.pref_disable_max_brightness_warning ) {
                maxBrightnessToggle = findViewById(R.id.max_brightness_toggle);
                maxBrightnessToggle.setBackground(getDrawable(R.drawable.toggle_on));
            }

            View dataToggle, autoBackupToggle, btToggle, wifiToggle, locationToggle, syncToggle;

            //set the data toggle switch
            dataToggle = findViewById(R.id.data_toggle);
            if(Util.isDataToggled(context)) {
                Util.toggleData(context, false);
                dataToggle.setBackground(getDrawable(R.drawable.toggle_on));
            }

            //set app auto backup switch
            autoBackupToggle = findViewById(R.id.backup_toggle);
            if(pref.pref_auto_backup) {
                autoBackupToggle.setBackground(getDrawable(R.drawable.toggle_on));
            }

            //set bluetooth switch
            btToggle = findViewById(R.id.bt_popup_toggle);
            if(pref.pref_no_popup_on_bt) {
                btToggle.setBackground(getDrawable(R.drawable.toggle_on));
            }

            //set wifi switch
            wifiToggle = findViewById(R.id.wifi_popup_toggle);
            if(pref.pref_no_popup_on_wifi) {
                wifiToggle.setBackground(getDrawable(R.drawable.toggle_on));
            }

            //set sync switch
            syncToggle = findViewById(R.id.sync_popup_toggle);
            if(pref.pref_no_popup_on_sync) {
                syncToggle.setBackground(getDrawable(R.drawable.toggle_on));
            }

            //set the location switch
            locationToggle = findViewById(R.id.location_popup_toggle);
            if(pref.pref_no_popup_gm_location) {
                locationToggle.setBackground(getDrawable(R.drawable.toggle_on));
            }
        }

        addRateAppTouchListener();
        addSendEmailTouchListener();
        addSupportDevelopmentTouchListener();
        updateAnimationScale();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(samFixBrightnessAddonBroadcastReceiver != null)
            unregisterReceiver(samFixBrightnessAddonBroadcastReceiver);

        if(backupReceiver!= null)
            unregisterReceiver(backupReceiver);

        if(premiumCheckReceiver != null)
            unregisterReceiver(premiumCheckReceiver);
    }

    private void updateAnimationScale(){
        float animationScale = AnimatorDurationUtil.getAnimatorScale(getApplicationContext());
        View animationScaleToggle = findViewById(R.id.animation_scale_toggle);
        animationScaleToggle.setBackground(getDrawable((animationScale != 0f)? R.drawable.toggle_on: R.drawable.toggle_off));
        ((TextView)findViewById(R.id.animation_scale_description)).setText("Animation scale is set to " + animationScale);
    }

    private void addSendEmailTouchListener() {
        findViewById(R.id.email).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"dharmapoudel1@gmail.com"});
                i.putExtra(Intent.EXTRA_SUBJECT, "SamFix");
                try {
                    startActivity(Intent.createChooser(i, "Send Email..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(MainActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void addSupportDevelopmentTouchListener() {
        findViewById(R.id.support).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                /*boolean isBillingProcessorAvailable = bp.isIabServiceAvailable(MainActivity.this);
                boolean isOneTimePurchaseSupported = bp.isOneTimePurchaseSupported();
                if (isBillingProcessorAvailable && isOneTimePurchaseSupported) {
                    bp.purchase(MainActivity.this, PRODUCT_ID, null);
                }*/
            }
        });
    }

    private void addRateAppTouchListener() {
        findViewById(R.id.rate).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://labs.xda-developers.com/store/app/com.dharmapoudel.samfix"));
                startActivity(intent);
            }
        });

    }

    public void toggleGreyScale(View v){
        Context context = getApplicationContext();
        Preferences pref = new Preferences(context);
        pref.savePreference("pref_enable_greyscale", !pref.pref_enable_greyscale);

        View greyScaleToggle = v.findViewById(R.id.greyscale_toggle);
        greyScaleToggle.setBackground(getDrawable(!pref.pref_enable_greyscale? R.drawable.toggle_on: R.drawable.toggle_off));
        Util.toggleGreyScale(this, pref.pref_enable_greyscale);

    }

    public void toggleMaxVolume(View v){
        Context context = getApplicationContext();
        Preferences pref = new Preferences(context);
        pref.savePreference("pref_disable_max_volume_warning", !pref.pref_disable_max_volume_warning);

        View maxVolumeToggle = v.findViewById(R.id.max_volume_toggle);
        maxVolumeToggle.setBackground(getDrawable(pref.pref_disable_max_volume_warning? R.drawable.toggle_off: R.drawable.toggle_on));
        Util.toggleMaxVolumeWarning(this, pref.pref_disable_max_volume_warning);

        if(pref.pref_disable_max_volume_warning)
            unscheduleSettingsUpdateJob(context);
        else
            scheduleSettingsUpdateJob(context);
    }

    public void toggleMaxBrightness(View v){
        PackageManager pm = context.getPackageManager();
        boolean isInstalled = Util.isPackageInstalled("com.dharmapoudel.samfix.addon", pm);
        if(isInstalled) {
            Preferences pref = new Preferences(context);
            pref.savePreference("pref_disable_max_brightness_warning", !pref.pref_disable_max_brightness_warning);

            View maxBrightnessToggle = v.findViewById(R.id.max_brightness_toggle);
            maxBrightnessToggle.setBackground(getDrawable(!pref.pref_disable_max_brightness_warning? R.drawable.toggle_on: R.drawable.toggle_off));
            Util.toggleMaxBrightnessWarning(this, pref.pref_disable_max_brightness_warning);
        } else {
            Toast.makeText(context, "Install SamFix Addon to enable this feature", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://labs.xda-developers.com/store/app/com.dharmapoudel.samfix.addon"));
            context.startActivity(intent);
        }
    }

    public void setAnimationScale(View v){
        Intent intent = new Intent(this, AnimatorDurationActivity.class);
        startActivity(intent);

    }

    public void checkLicense(){
        PackageManager pm = context.getPackageManager();
        boolean isInstalled = Util.isPackageInstalled("com.dharmapoudel.proapp", pm);
        if(isInstalled) {
            if(!licenseCheckBroadcastSent) {
                //pref.savePreference("license_check_broadcast_sent", true);
                licenseCheckBroadcastSent = true;
                Intent intent = new Intent(getApplicationContext().getString(R.string.pro_app_filter_action));
                intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                intent.setFlags(FLAG_RECEIVER_FOREGROUND);
                context.sendBroadcast(intent);
                Log.e(Util.class.getSimpleName(), "Samfix License Check Brightness broadcast sent!");
            }
        }else {
            Preferences pref = new Preferences(context);
            pref.savePreference("pref_license_check_broadcast_value", false);
            licenseCheckBroadcastSent = false;

            Toast.makeText(context, "Install Pro Key to enable this feature", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.dharmapoudel.proapp"));
            context.startActivity(intent);
        }
    }
    public void sendLicenseCheckBroadCast(){
        PackageManager pm = getApplicationContext().getPackageManager();
        boolean isInstalled = Util.isPackageInstalled("com.dharmapoudel.proapp", pm);
        if(isInstalled) {
            if(!licenseCheckBroadcastSent) {
                //pref.savePreference("license_check_broadcast_sent", true);
                licenseCheckBroadcastSent = true;
                Intent intent = new Intent(getApplicationContext().getString(R.string.pro_app_filter_action));
                intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                intent.addFlags(FLAG_RECEIVER_FOREGROUND);
                intent.setFlags(FLAG_RECEIVER_FOREGROUND);
                context.sendBroadcast(intent);
                Log.e(Util.class.getSimpleName(), "Samfix License Check Brightness broadcast sent!");
            }
        } else {
            licenseCheckBroadcastSent = false;
            Preferences pref = new Preferences(context);
            pref.savePreference("pref_license_check_broadcast_value", false);
        }
    }

    public void toggleData(View v){
        Preferences pref = new Preferences(v.getContext());
        if(pref.pref_license_check_broadcast_value) {
            boolean dataOn = Util.isDataToggled(this);
            View dataToggle = v.findViewById(R.id.data_toggle);
            dataToggle.setBackground(getDrawable(dataOn ? R.drawable.toggle_off : R.drawable.toggle_on));
            Util.toggleData(this, dataOn);
        } else {
            checkLicense();
            //Toast.makeText(context, "Please scroll to the bottom and donate to enable", Toast.LENGTH_SHORT).show();
        }
    }

    public void toggleAutoBackup(View v){
        Preferences pref = new Preferences(v.getContext());
        if(pref.pref_license_check_broadcast_value) {
            pref.savePreference("pref_auto_backup", !pref.pref_auto_backup);

            PermissionUtil.askForPermission(this);

            boolean autoBackupEnabled = pref.pref_auto_backup;

            View toggle = v.findViewById(R.id.backup_toggle);
            toggle.setBackground(getDrawable(!autoBackupEnabled ? R.drawable.toggle_on : R.drawable.toggle_off));
        } else {
            checkLicense();
            //Toast.makeText(context, "Please scroll to the bottom and donate to enable", Toast.LENGTH_SHORT).show();
        }
    }

    public void toggleBTPopup(View v){
        Preferences pref = new Preferences(v.getContext());
        if(pref.pref_license_check_broadcast_value) {
            pref.savePreference("pref_no_popup_on_bt", !pref.pref_no_popup_on_bt);

            View toggle = v.findViewById(R.id.bt_popup_toggle);
            toggle.setBackground(getDrawable(!pref.pref_no_popup_on_bt ? R.drawable.toggle_on : R.drawable.toggle_off));
        } else {
            checkLicense();
            //Toast.makeText(context, "Please scroll to the bottom and donate to enable", Toast.LENGTH_SHORT).show();
        }
    }

    public void toggleWifiPopup(View v){
        Preferences pref = new Preferences(v.getContext());
        if(pref.pref_license_check_broadcast_value) {
            pref.savePreference("pref_no_popup_on_wifi", !pref.pref_no_popup_on_wifi);

            View toggle = v.findViewById(R.id.wifi_popup_toggle);
            toggle.setBackground(getDrawable(!pref.pref_no_popup_on_wifi ? R.drawable.toggle_on : R.drawable.toggle_off));
        } else {
            checkLicense();
            //Toast.makeText(context, "Please scroll to the bottom and donate to enable", Toast.LENGTH_SHORT).show();
        }
    }

    public void toggleSyncPopup(View v){
        if(pref.pref_license_check_broadcast_value) {
            Context context = getApplicationContext();
            Preferences pref = new Preferences(context);
            pref.savePreference("pref_no_popup_on_sync", !pref.pref_no_popup_on_sync);

            View toggle = v.findViewById(R.id.sync_popup_toggle);
            toggle.setBackground(getDrawable(!pref.pref_no_popup_on_sync ? R.drawable.toggle_on : R.drawable.toggle_off));
        } else {
            checkLicense();
            //Toast.makeText(context, "Please scroll to the bottom and donate to enable", Toast.LENGTH_SHORT).show();
        }
    }

    public void toggleLocationPopup(View v){
        if(pref.pref_license_check_broadcast_value) {
            Context context = getApplicationContext();
            Preferences pref = new Preferences(context);
            pref.savePreference("pref_no_popup_gm_location", !pref.pref_no_popup_gm_location);

            View greyScaleToggle = v.findViewById(R.id.location_popup_toggle);
            greyScaleToggle.setBackground(getDrawable(!pref.pref_no_popup_gm_location ? R.drawable.toggle_on : R.drawable.toggle_off));
        } else {
            checkLicense();
            //Toast.makeText(context, "Please scroll to the bottom and donate to enable", Toast.LENGTH_SHORT).show();
        }
    }


    public static void enableToggles() {

        data.setAlpha(1.0f);
        backup.setAlpha(1.0f);
        location_popup.setAlpha(1.0f);
        bt_popup.setAlpha(1.0f);
        wifi_popup.setAlpha(1.0f);
        sync_popup.setAlpha(1.0f);
    }

    public static void disableToggles() {
        data.setAlpha(0.5f);
        backup.setAlpha(0.5f);
        location_popup.setAlpha(0.5f);
        bt_popup.setAlpha(0.5f);
        wifi_popup.setAlpha(0.5f);
        sync_popup.setAlpha(0.5f);
    }

    public static void resetToggles(Context context){
        Preferences pref = new Preferences(context);
        pref.savePreference("pref_auto_backup", false);
        pref.savePreference("pref_no_popup_gm_location", false);
        pref.savePreference("pref_no_popup_on_sync", false);
        pref.savePreference("pref_no_popup_on_wifi", false);
        pref.savePreference("pref_no_popup_on_bt", false);
        pref.savePreference("pref_license_check_broadcast_value", false);
    }



    /*public void onAccessibilityEnableClick(View v) {
        Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(intent);
    }*/

    public void updateAccessibilityServices() {

        String enabledServices = Util.getEnabledAccessibilityServices(context);
        if(!enabledServices.contains("samfix")){
            enabledServices += ":com.dharmapoudel.samfix/com.dharmapoudel.samfix.SamFixAccessibilityService";
            Util.setEnabledAccessibilityServices(context, enabledServices);
        }


        /*AccessibilityManager am = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> runningServices = am.getInstalledAccessibilityServiceList();
        for (AccessibilityServiceInfo service : runningServices) {
            Log.i(TAG, service.getId());
        }*/
    }

    public static Context getContext(){
        return mContext;
    }



    public static class SamFixBrightnessAddonBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String act = intent.getAction();
            if(act != null && act.equals(context.getResources().getString(R.string.samfix_brightness_broadcast_intent)) ) {
                boolean value = intent.getBooleanExtra("brightness_value", false);

                //save the value to the preferences
                Preferences pref = new Preferences(context);
                pref.savePreference("pref_disable_max_brightness_warning", value);


                if(maxBrightnessToggle != null) {
                    maxBrightnessToggle.setBackground(getContext().getDrawable(pref.pref_disable_max_brightness_warning ? R.drawable.toggle_on : R.drawable.toggle_off));
                }

                Log.i(SamFixBrightnessAddonBroadcastReceiver.class.getSimpleName(), "SamFix brightness broadcast sent from addon is received with value : " + value);
            }
        }
    }


    public static class PremiumCheckBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String act = intent.getAction();
            if(act != null && act.equals(context.getResources().getString(R.string.samfix_filter_action)) ) {
                boolean value = "true".equalsIgnoreCase(intent.getStringExtra("calibration_value"));

                //save the value to the preferences
                Preferences pref = new Preferences(context);
                pref.savePreference("pref_license_check_broadcast_value", value);
                licenseCheckBroadcastSent = false;

                if(value){
                    enableToggles();
                } else {
                    disableToggles();
                    resetToggles(context);
                }

                Log.i(PremiumCheckBroadcastReceiver.class.getSimpleName(), "SamFix license checked broadcast sent from pro  is received with value : " + value);
            }
        }
    }
}
