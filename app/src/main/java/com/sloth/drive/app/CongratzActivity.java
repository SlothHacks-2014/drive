package com.sloth.drive.app;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.json.JSONArray;

import java.util.Timer;
import java.util.TimerTask;

public class CongratzActivity extends ActionBarActivity implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerClickListener
{

    private GlobalMethods gm = new GlobalMethods(this);
    private LocationClient loc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_congratz);

        int icon = getIntent().getExtras().getInt(Constants.Strings.SERVICE.getValue());

        ImageView logo = (ImageView) findViewById(R.id.final_token);

        logo.setImageResource(icon);

        loc = new LocationClient(this, this, this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        loc.connect();


    }

    @Override
    protected void onStop() {
        super.onStop();
        loc.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        while (loc.getLastLocation() == null) {}

        Location location = loc.getLastLocation();

        new LyftRequester().execute("https://api.lyft.com/rides");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.congratz, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void openLyft(View view) {
        startActivity(new Intent(getPackageManager()
                .getLaunchIntentForPackage(Constants.Strings.LYFT_PACKAGE.getValue())));
    }

    public void openChat(View view) {
        startActivity(new Intent(this, CarpoolActivity.class));
    }

    private class LyftRequester extends AsyncTask<String, Void, JSONArray> {
        @Override
        protected JSONArray doInBackground(String... strings) {
            try {
            Header contentType = new BasicHeader("Content-Type", "application/json");
            Header auth = new BasicHeader("Authorization", "fbAccessToken CAAD6nt9dOocBAPGpMipuieMBiaUkLZCEE7FQHZCvT0s1UqH6ZAXW0ZCaET7ZCkuUVTRBLm9nVsLvlxR7PMyJplYZB3Fuuko6JLBolyOcoVd75ukMEZBi4xwbWJ7QB16KEqAJE0WK1n2CMzrCEfrZAtUYDIXKK4V6rQlR3VScaDSnPkGAAeOND62QCefALgdr22s7b5VuOPueSojN9ZBwov6j1zEEnYjKsQ1Ba3vEWX3uzfgZDZD");

            String result = gm.postHttpRequest("startLocation", strings[0], new LatLng(loc.getLastLocation().getLatitude(),
                    loc.getLastLocation().getLongitude()), contentType, auth);

            }
            catch (Exception e) {
                // Do nothing
            }

            return new JSONArray();
        }

        @Override
        protected void onPostExecute(JSONArray drivers) {
            super.onPostExecute(drivers);

        }
    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }
}
