
package com.dharmapoudel.samfix;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.CheckedTextView;

/**
 * An Activity which allows selecting the animator duration scale from a full list, accessed by
 * long pressing the quick action tile.
 */
public class AnimatorDurationActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_scale_dialog);
        float scale = AnimatorDurationUtil.getAnimatorScale(this);
        ((Checkable) findViewById(AnimatorDurationUtil.getScaleItemId(scale))).setChecked(true);
    }

    public void scaleClick(View v) {
        uncheckAllChildren((ViewGroup) v.getParent());
        ((CheckedTextView) v).setChecked(true);
        AnimatorDurationUtil.setAnimatorScale(this, AnimatorDurationUtil.getScale(v.getId()));
        finishAfterTransition();
    }

    public void cancel(View v) {
        finishAfterTransition();
    }

    private void uncheckAllChildren(@NonNull ViewGroup vg) {
        for (int i = vg.getChildCount() - 1; i >= 0; i--) {
            View child = vg.getChildAt(i);
            if (child instanceof Checkable) {
                ((Checkable) child).setChecked(false);
            }
        }
    }

}
