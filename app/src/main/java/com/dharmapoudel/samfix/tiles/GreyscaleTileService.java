package com.dharmapoudel.samfix.tiles;

import android.annotation.TargetApi;
import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import com.dharmapoudel.samfix.Util;

@TargetApi(Build.VERSION_CODES.N)
public class GreyscaleTileService extends TileService {

    @Override
    public void onClick() {
        super.onClick();

        if (!Util.hasPermission(this)) {
            showDialog(Util.createTipsDialog(this));
            return;
        }

        int oldState = getQsTile().getState();
        if (oldState == Tile.STATE_ACTIVE) {
            setState(Tile.STATE_INACTIVE);
        } else {
            setState(Tile.STATE_ACTIVE);
        }

        Util.toggleGreyScale(this, oldState == Tile.STATE_ACTIVE);
    }


    private void setState(int state) {
        Tile tile = getQsTile();
        tile.setState(state);
        tile.updateTile();
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
        boolean greyScaleEnable = Util.isGreyScaleEnabled(this);
        setState(greyScaleEnable ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
    }
}
