package com.sloth.drive.app;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.TextView;

<<<<<<< HEAD
import org.apache.http.client.HttpClient;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
=======
>>>>>>> 1072d284f005bfdbc280121410818edcc59c910f
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

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
        String destination = preferences.getString(Constants.Strings.PREF_DESTINATION_KEY.getValue(), "123 Example Way, Pleasantville, CA 12345");

        ratioBar.setProgress(ratio);
        destinationText.setText(destination);

        new RestTest().execute("http://api.metro.net/agencies/lametro/stops/6033/predictions/");
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

    private class RestTest extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            HttpClient client = new HttpClient();
            
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            System.out.println(s);
        }
    }
}
