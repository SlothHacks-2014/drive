package com.sloth.drive.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;

public class SettingsActivity extends ActionBarActivity implements SeekBar.OnSeekBarChangeListener {

    private final GlobalMethods gm = new GlobalMethods(this);

    private SeekBar ratioBar;
    private TextView destinationText;
    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ratioBar = (SeekBar) findViewById(R.id.ratiobar);
        destinationText = (TextView) findViewById(R.id.destinationText);

        ratioBar.setOnSeekBarChangeListener(this);
        SharedPreferences preferences = getPreferences(0);

        int ratio = preferences.getInt(Constants.Strings.PREF_RATIO_KEY.getValue(), 50);
        String destination = preferences.getString(Constants.Strings.PREF_DESTINATION_KEY.getValue(),
                "");

        ratioBar.setProgress(ratio);
        destinationText.setText(destination);

        map = ((MapFragment) getFragmentManager()
                .findFragmentById(R.id.address_match_map)).getMap();

        destinationText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    Geocoder gc = new Geocoder(getApplicationContext());

                    map.clear();

                    try {
                        Address address = gc.getFromLocationName(destinationText
                                .getText().toString(), 1).get(0);

                        LatLng location =
                                new LatLng(address.getLatitude(), address.getLongitude());

                        map.addMarker(new MarkerOptions()
                                .position(location));

                        map.moveCamera(CameraUpdateFactory.newLatLng(location));
                        map.moveCamera(CameraUpdateFactory.zoomTo(11.5f));

                        getWindow().setSoftInputMode(WindowManager.LayoutParams
                                .SOFT_INPUT_STATE_HIDDEN);

                        destinationText.clearFocus();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    return true;
                }

                return false;
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    public boolean onContextItemSelected(MenuItem item) {
        onOptionsItemSelected(item);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onStop() {
        super.onStop();

        int ratio = ratioBar.getProgress();
        String destination = destinationText.getText().toString();
        SharedPreferences preferences = getPreferences(0);

        SharedPreferences.Editor editor = preferences.edit();

        editor.putInt(Constants.Strings.PREF_RATIO_KEY.getValue(), ratio);
        editor.putString(Constants.Strings.PREF_DESTINATION_KEY.getValue(), destination);

        editor.commit();
    }

    public void launchMap(View view) {
        Bundle data = new Bundle();
        data.putInt(Constants.Strings.BUNDLE_RATIO_KEY.getValue(), ratioBar.getProgress());
        data.putString(Constants.Strings.BUNDLE_DESTINATION_KEY.getValue(),
                destinationText.getText().toString());

        startActivity(new Intent(this, MapActivity.class).putExtras(data));
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {}

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

}
