package com.sloth.drive.app;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.TextView;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class SettingsActivity extends ActionBarActivity implements SeekBar.OnSeekBarChangeListener {

    private final GlobalMethods gm = new GlobalMethods(this);

    private SeekBar ratioBar;
    private TextView destinationText;

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

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {}

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

}
