package com.dharmapoudel.samfix;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;

import static com.dharmapoudel.samfix.BootIntentReceiver.scheduleSettingsUpdateJob;
import static com.dharmapoudel.samfix.BootIntentReceiver.unscheduleSettingsUpdateJob;

public class MainActivity extends AppCompatActivity implements  BillingProcessor.IBillingHandler {

    private BillingProcessor bp;
    private Preferences pref;
    private Context context;

    private View dataToggle, autoBackupToggle, btWifiToggle, locationToggle;


    private static final String TAG = MainActivity.class.getSimpleName();

    String PRODUCT_ID = "samfix";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //action bar settings
        ActionBar actionBar = getSupportActionBar();
        //remove the shadow
        actionBar.setElevation(0);
        actionBar.hide();

        //initialize billing
        String LICENSE_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAx//FhYCIEAQAgj1yfnaD1nLcI41fWkkx05wefBOGK3RP5pFRoE6w1c63uxf7hmHjLbdm8oAqAvIgekadrmaSnt5aiAk3W8GdGFuLBdP1TgvfCFwAboBle/0Bj/Cr2kkQnUl39TkVmMqe6cUhz/W0pH3kn/BLfd9nsdBhy6AVFnpECfoc+pNZiJ5zEMWL67JGiHXG6/4BTGZ0AYEFsLhjVw/R5SSiLRqqbcMZeb50Iu5sULiACanJtTH4VYcr92FxqYWGeHXjkrcG37YJyiWt+ez7H+9bEKDF0GPgFmzx+bXU6apKhm9hbpnhwBiDA2vv5t1iQLwVjvDNMpft4ltlZQIDAQAB";
        bp = new BillingProcessor(this, LICENSE_KEY, this);
        bp.initialize();


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

            //automatically add to accessibility services
            updateAccessibilityServices();

            //set grey scale switch
            if(pref.pref_enable_greyscale) {
                Util.toggleMaxBrightnessWarning(context, false);
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
            if(pref.pref_disable_max_brightness_warning ) {
                Util.toggleMaxBrightnessWarning(context, false);
                View maxBrightnessToggle = findViewById(R.id.max_brightness_toggle);
                maxBrightnessToggle.setBackground(getDrawable(R.drawable.toggle_on));
            }

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

            //set bluetooth wifi switch
            btWifiToggle = findViewById(R.id.btwifi_popup_toggle);
            if(pref.pref_no_popup_on_bt_wifi) {
                btWifiToggle.setBackground(getDrawable(R.drawable.toggle_on));
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

    }

    @Override
    protected void onResume() {
        super.onResume();

        //update toggle with correct scale value
        updateAnimationScale();
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
                boolean isBillingProcessorAvailable = bp.isIabServiceAvailable(MainActivity.this);
                boolean isOneTimePurchaseSupported = bp.isOneTimePurchaseSupported();
                if (isBillingProcessorAvailable && isOneTimePurchaseSupported) {
                    bp.purchase(MainActivity.this, PRODUCT_ID, null);
                }
            }
        });
        /*findViewById(R.id.support).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.paypal.me/dharmapoudel"));
                startActivity(browserIntent);
            }
        });*/
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

    public void toggleData(View v){
        boolean dataOn = Util.isDataToggled(this);
        View dataToggle = v.findViewById(R.id.data_toggle);
        dataToggle.setBackground(getDrawable(dataOn ? R.drawable.toggle_off: R.drawable.toggle_on));
        Util.toggleData(this, dataOn);
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
        Context context = getApplicationContext();
        Preferences pref = new Preferences(context);
        pref.savePreference("pref_disable_max_brightness_warning", !pref.pref_disable_max_brightness_warning);

        View maxVolumeToggle = v.findViewById(R.id.max_brightness_toggle);
        maxVolumeToggle.setBackground(getDrawable(!pref.pref_disable_max_brightness_warning? R.drawable.toggle_on: R.drawable.toggle_off));
        Util.toggleMaxBrightnessWarning(this, pref.pref_disable_max_brightness_warning);
    }

    public void setAnimationScale(View v){
        Intent intent = new Intent(this, AnimatorDurationActivity.class);
        startActivity(intent);

    }

    public void toggleAutoBackup(View v){
        Context context = getApplicationContext();
        Preferences pref = new Preferences(context);
        pref.savePreference("pref_auto_backup", !pref.pref_auto_backup);

        PermissionUtil.askForPermission(this);

        boolean autoBackupEnabled = pref.pref_auto_backup;

        View toggle = v.findViewById(R.id.backup_toggle);
        toggle.setBackground(getDrawable(!autoBackupEnabled? R.drawable.toggle_on: R.drawable.toggle_off));
    }

    public void toggleBTWifiPopup(View v){
        Context context = getApplicationContext();
        Preferences pref = new Preferences(context);
        pref.savePreference("pref_no_popup_on_bt_wifi", !pref.pref_no_popup_on_bt_wifi);

        View toggle = v.findViewById(R.id.btwifi_popup_toggle);
        toggle.setBackground(getDrawable(!pref.pref_no_popup_on_bt_wifi? R.drawable.toggle_on: R.drawable.toggle_off));
    }

    public void toggleLocationPopup(View v){
        Context context = getApplicationContext();
        Preferences pref = new Preferences(context);
        pref.savePreference("pref_no_popup_gm_location", !pref.pref_no_popup_gm_location);

        View greyScaleToggle = v.findViewById(R.id.location_popup_toggle);
        greyScaleToggle.setBackground(getDrawable(!pref.pref_no_popup_gm_location? R.drawable.toggle_on: R.drawable.toggle_off));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.thank_you), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPurchaseHistoryRestored() {
        //Toast.makeText(getApplicationContext(), "History Restored!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBillingError(int errorCode, @Nullable Throwable error) {
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.next_time), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBillingInitialized() {
        //Toast.makeText(getApplicationContext(), "Billing initialized!", Toast.LENGTH_SHORT).show();
        View data = findViewById(R.id.data);
        View backup = findViewById(R.id.backup);
        View location_popup = findViewById(R.id.location_popup);
        View btwifi_popup = findViewById(R.id.btwifi_popup);

        data.setAlpha(0.5f);
        data.setEnabled(false);
        backup.setAlpha(0.5f);
        backup.setEnabled(false);
        location_popup.setAlpha(0.5f);
        location_popup.setEnabled(false);
        btwifi_popup.setAlpha(0.5f);
        btwifi_popup.setEnabled(false);

        SharedPreferences.Editor editor = pref.getEditor();
        editor.putBoolean(PRODUCT_ID, false);


        if(bp.isPurchased(PRODUCT_ID)){
            if (bp.loadOwnedPurchasesFromGoogle()) {

                editor.putBoolean(PRODUCT_ID, true);

                data.setAlpha(1.0f);
                data.setEnabled(true);
                data.setVisibility(View.VISIBLE);

                backup.setAlpha(1.0f);
                backup.setEnabled(true);
                backup.setVisibility(View.VISIBLE);

                location_popup.setAlpha(1.0f);
                location_popup.setEnabled(true);
                location_popup.setVisibility(View.VISIBLE);

                btwifi_popup.setAlpha(1.0f);
                btwifi_popup.setEnabled(true);
                btwifi_popup.setVisibility(View.VISIBLE);
            }
        }
        editor.apply();
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
}
