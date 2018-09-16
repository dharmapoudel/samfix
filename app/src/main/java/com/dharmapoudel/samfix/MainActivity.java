package com.dharmapoudel.samfix;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //action bar settings
        ActionBar actionBar = getSupportActionBar();
        //remove the shadow
        actionBar.setElevation(0);

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

            //set greyscale switch
            View greyScaleToggle = findViewById(R.id.greyscale_toggle);
            boolean isGreyScaleEnabled = Util.isGreyscaleEnabled(this);
            greyScaleToggle.setBackground(getDrawable(isGreyScaleEnabled? R.drawable.toggle_on: R.drawable.toggle_off));


            //set max volume switch
            View maxVolumeToggle = findViewById(R.id.max_volume_toggle);
            boolean isMaxVolumeWarningDisabled = Util.isMaxVolumeWarningDisabled(this);
            maxVolumeToggle.setBackground(getDrawable(isMaxVolumeWarningDisabled? R.drawable.toggle_on: R.drawable.toggle_off));


            //set max brightness switch
            View maxBrightnessToggle = findViewById(R.id.max_volume_toggle);
            boolean isMaxBrightnessWarningDisabled = Util.isMaxBrightnessWarningDisabled(this);
            maxBrightnessToggle.setBackground(getDrawable(isMaxBrightnessWarningDisabled? R.drawable.toggle_on: R.drawable.toggle_off));

        }



    }

    public void toggleGreyScale(View v){

        View greyScaleToggle = v.findViewById(R.id.greyscale_toggle);
        boolean isGreyScaleEnabled = Util.isGreyscaleEnabled(this);
        greyScaleToggle.setBackground(getDrawable(isGreyScaleEnabled? R.drawable.toggle_off: R.drawable.toggle_on));
        Util.toggleGreyscale(this, !isGreyScaleEnabled);
    }

    public void toggleMaxVolume(View v){
        View maxVolumeToggle = v.findViewById(R.id.max_volume_toggle);
        boolean isMaxVolumeEnabled = Util.isMaxVolumeWarningDisabled(this);
        maxVolumeToggle.setBackground(getDrawable(isMaxVolumeEnabled? R.drawable.toggle_off: R.drawable.toggle_on));
        Util.toggleMaxVolumeWarning(this, !isMaxVolumeEnabled);
    }

    public void toggleMaxBrightness(View v){

        View maxVolumeToggle = v.findViewById(R.id.max_brightness_toggle);
        boolean isMaxBrightnessEnabled = Util.isMaxBrightnessWarningDisabled(this);
        maxVolumeToggle.setBackground(getDrawable(isMaxBrightnessEnabled? R.drawable.toggle_off: R.drawable.toggle_on));
        Util.toggleMaxBrightnessWarning(this, !isMaxBrightnessEnabled);
    }
}
