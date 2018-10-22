package com.dharmapoudel.samfix.tiles;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.widget.Toast;

import com.dharmapoudel.samfix.Preferences;
import com.dharmapoudel.samfix.Util;

@TargetApi(Build.VERSION_CODES.N)
public class DataToggleTileService extends TileService {

    private SharedPreferences mSharedPreferences;
    private Preferences preferences;

    @Override
    public void onClick() {
        super.onClick();

        preferences = new Preferences(getApplicationContext());
        if(preferences.supportEnabled) {

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

            Util.toggleData(this, oldState == Tile.STATE_ACTIVE);
        } else {
            Toast.makeText(getApplicationContext(), "Only available after supporting!", Toast.LENGTH_SHORT).show();
        }
    }

    private void setState(int state) {
        Tile tile = getQsTile();
        tile.setState(state);
        tile.updateTile();
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean supportEnabled = mSharedPreferences.getBoolean("samfix", false);
        if(supportEnabled) {
            boolean dataOn = Util.isDataToggled(this);
            setState(dataOn ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
        }
    }
}
