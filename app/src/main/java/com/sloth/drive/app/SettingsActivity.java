package com.sloth.drive.app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;

public class SettingsActivity extends ActionBarActivity implements SeekBar.OnSeekBarChangeListener {

    private final GlobalMethods gm = new GlobalMethods(this);

    private SeekBar ratioBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ratioBar = (SeekBar) findViewById(R.id.ratiobar);

        ratioBar.setOnSeekBarChangeListener(this);
        SharedPreferences preferences = getPreferences(0);

        int ratio = preferences.getInt(Constants.Strings.PREF_RATIO_KEY.getValue(), 50);

        ratioBar.setProgress(ratio);
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
        SharedPreferences preferences = getPreferences(0);

        SharedPreferences.Editor editor = preferences.edit();

        editor.putInt(Constants.Strings.PREF_RATIO_KEY.getValue(), ratio);

        editor.commit();
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
