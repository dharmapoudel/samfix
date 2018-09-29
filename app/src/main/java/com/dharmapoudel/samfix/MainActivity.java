package com.dharmapoudel.samfix;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

public class MainActivity extends AppCompatActivity implements  BillingProcessor.IBillingHandler {

    private BillingProcessor bp;

    String PRODUCT_ID = "samfix";
    private String LICENSE_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtJHFpzqbwoVhAMVCpfo/5AU+bbesraxCKTz4uCwCzg4YzLSuQHVkttPSFaiPPTce3iTOWgJpXu2nw8e8vPnSHd0tJwlx2QJq2MW5vgq7l3oNyLY+2us6NbDMJDdfBPedEIrk7VN914ehQ29Qn+Yb7kZvR2SEKdaMl0EzhAlutBIJGKhTSIi5jBqkB9d3r6K9X7xhP7SbLT+JjQCWD+g1X2ey/RAVrZtipf70re0TLA6z6K44+WHqLKqn9G+z1DplmwhiI+EC8QvhGP6cXvehk3bS52P9p23wQZY9zHdHGN+ApfeDqigv/NlWHqSHLVCcHyoz7GrK2vOFjK9kvQuU4QIDAQAB";

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
        bp = new BillingProcessor(this, LICENSE_KEY, this);
        bp.initialize();

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


            Context context = getApplicationContext();
            Preferences pref = new Preferences(context);

            //set grey scale switch
            if(pref.pref_enable_greyscale ) {
                Util.toggleMaxBrightnessWarning(context, false);
                View greyScaleToggle = findViewById(R.id.greyscale_toggle);
                greyScaleToggle.setBackground(getDrawable(R.drawable.toggle_on));
            }


            //set max volume switch
            if(pref.pref_disable_max_volume_warning ) {
                Util.toggleMaxVolumeWarning(context, false);
                View maxVolumeToggle = findViewById(R.id.max_volume_toggle);
                maxVolumeToggle.setBackground(getDrawable(R.drawable.toggle_on));
            }


            //set max brightness switch
            if(pref.pref_disable_max_brightness_warning ) {
                Util.toggleMaxBrightnessWarning(context, false);
                View maxBrightnessToggle = findViewById(R.id.max_brightness_toggle);
                maxBrightnessToggle.setBackground(getDrawable(R.drawable.toggle_on));
            }

        }

        addRateAppTouchListener();
        addSendEmailTouchListener();
        //addSupportDevelopmentTouchListener();

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

        /*findViewById(R.id.donate).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                boolean isBillingProcessorAvailable = bp.isIabServiceAvailable(MainActivity.this);
                boolean isOneTimePurchaseSupported = bp.isOneTimePurchaseSupported();
                if (isBillingProcessorAvailable && isOneTimePurchaseSupported) {
                    bp.purchase(MainActivity.this, PRODUCT_ID, null);
                }
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

    public void toggleMaxVolume(View v){
        Context context = getApplicationContext();
        Preferences pref = new Preferences(context);
        pref.savePreference("pref_disable_max_volume_warning", !pref.pref_disable_max_volume_warning);

        View maxVolumeToggle = v.findViewById(R.id.max_volume_toggle);
        maxVolumeToggle.setBackground(getDrawable(!pref.pref_disable_max_volume_warning? R.drawable.toggle_on: R.drawable.toggle_off));
        Util.toggleMaxVolumeWarning(this, pref.pref_disable_max_volume_warning);
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
    }
}
