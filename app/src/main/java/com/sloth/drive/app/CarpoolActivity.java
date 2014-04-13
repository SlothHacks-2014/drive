package com.sloth.drive.app;

import android.location.Location;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

public class CarpoolActivity extends ActionBarActivity implements
    GooglePlayServicesClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener,
    GoogleMap.OnMarkerClickListener
{

    private Firebase fb;

    private final String latitude = "lat";
    private final String longitude = "lng";
    private final String message = "msg";

    private EditText messageText;
    private GoogleMap map;

    private LocationClient loc;
    private Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carpool);

        messageText = (EditText) findViewById(R.id.broadcast_message);
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.message_map)).getMap();

        loc = new LocationClient(this, this, this);

        fb = new Firebase("https://ridr.firebaseio.com/ridr");

        fb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                map.clear();

                HashMap<String, String> data =
                        (HashMap<String, String>) dataSnapshot.getValue();

                double lat = Double.parseDouble(data.get(latitude));
                double lng = Double.parseDouble(data.get(longitude));

                LatLng coords = new LatLng(lat, lng);

                map.moveCamera(CameraUpdateFactory.newLatLng(coords));

                Date today = new Date();

                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");

                Marker msgMarker = map.addMarker(new MarkerOptions()
                .position(coords)
                .title(sdf.format(today))
                .snippet((String) data.get(message)));

                msgMarker.showInfoWindow();

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                // Do nothing

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        loc.connect();

        messageText.requestFocus();
    }

    @Override
    protected void onStop() {
        super.onStop();
        loc.disconnect();
    }

    public void broadCastMessage(View view) {
        HashMap<String, String> broadcastValues = new HashMap<String, String>();

        broadcastValues.put(latitude, "" + location.getLatitude());
        broadcastValues.put(longitude, "" + location.getLongitude());
        broadcastValues.put(message, messageText.getText().toString());

        fb.setValue(broadcastValues);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.carpool, menu);
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

    @Override
    public void onConnected(Bundle bundle) {
        while(loc.getLastLocation() == null) {}

        location = loc.getLastLocation();

        map.moveCamera(CameraUpdateFactory.zoomTo(13));
        map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(),
                location.getLongitude())));
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
