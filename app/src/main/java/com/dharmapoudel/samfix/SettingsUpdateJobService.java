package com.dharmapoudel.samfix;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.widget.Toast;

public class SettingsUpdateJobService extends JobService {

    @Override
    public boolean onStartJob(final JobParameters params) {
        Context context = this.getApplicationContext();
        Preferences pref = new Preferences(context);

        //Toast.makeText(context, "Running scheduled job", Toast.LENGTH_SHORT).show();


            if(pref.pref_disable_max_volume_warning )
                Util.toggleMaxVolumeWarning(context, false);

            if(pref.pref_disable_max_brightness_warning )
                Util.toggleMaxBrightnessWarning(context, false);

            /*if(pref.pref_enable_greyscale )
                Util.toggleMaxBrightnessWarning(context, false);*/

            //AnimatorDurationUtil.setAnimatorScale(context, pref.pref_animation_duration);


        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
