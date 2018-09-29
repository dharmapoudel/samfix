package com.dharmapoudel.samfix.tiles;

import android.annotation.TargetApi;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import com.dharmapoudel.samfix.AnimatorDurationUtil;


@TargetApi(Build.VERSION_CODES.N)
public class AnimatorDurationTileService extends TileService {

    public AnimatorDurationTileService() { }


    @Override
    public void onClick() {
        int index = (AnimatorDurationUtil.getIndex(AnimatorDurationUtil.getAnimatorScale(this)) + 1) % 7;
        AnimatorDurationUtil.setAnimatorScale(this, AnimatorDurationUtil.scales[index]);
        setState();
    }

    private void setState() {
        int index = AnimatorDurationUtil.getIndex(AnimatorDurationUtil.getAnimatorScale(this));

        final Tile tile = getQsTile();
        tile.setIcon(Icon.createWithResource(getApplicationContext(), AnimatorDurationUtil.scaleIcons[index]));
        tile.updateTile();
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
        setState();
    }

}
