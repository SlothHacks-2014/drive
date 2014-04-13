package com.sloth.drive.app;

import android.content.Intent;
import android.graphics.Color;
import android.location.*;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MapActivity extends ActionBarActivity implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerClickListener
{
    private static final String METRO = "Metro";
    private static final String TAXI = "Taxi";
    private static final String LYFT = "Lyft";

    private Marker lastMarkerShown;

    private LocationClient loc;
    private MapFragment mapFragment;
    private GoogleMap map;

    private GlobalMethods gm = new GlobalMethods(this);
    private Bundle savedInstanceState;

    private List<Marker> lyftMarkers = new ArrayList<Marker>();
    private List<Marker> metros = new ArrayList<Marker>();

    private LyftLoader previousLoader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.savedInstanceState = getIntent().getExtras();
        setContentView(R.layout.activity_map);

        loc = new LocationClient(this, this, this);
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.route_map);

        map = mapFragment.getMap();

        map.setMyLocationEnabled(true);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.map, menu);
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
        while (loc.getLastLocation() == null) {}

        Location location = loc.getLastLocation();

        map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(),
                location.getLongitude())));

        map.moveCamera(CameraUpdateFactory.zoomTo(11.5f));

        map.setOnMarkerClickListener(this);

        /*new RouteLoader().execute("http://services.commuterapi.com/TransitODataService.svc" +
                "/GeoAreas('" + location.getLatitude() + "%7C" +
                location.getLongitude() + "%7C1.0')/Routes%20?api_key=" +
                "YOUR_APPLICATION_KEY&$format=json");*/

        new VehicleLoader().execute
                ("http://api.metro.net/agencies/lametro/routes/704/stops/");

        new ToLocationDrawer().execute(location.getLatitude() + "," + location.getLongitude(),
                savedInstanceState.getString(Constants.Strings.BUNDLE_DESTINATION_KEY.getValue())
                        .replaceAll(" ", "%20"));

        Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                new LyftLoader().execute("https://api.lyft.com/users/185005387/location");
            }
        }, 0, 500);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if(lastMarkerShown != null) {
            if(marker.getTitle().equals(lastMarkerShown.getTitle())) {
                if(marker.getSnippet().equals(METRO)) {
                    startActivity(getPackageManager().getLaunchIntentForPackage(Constants
                    .Strings.METRO_PACKAGE.getValue()));
                }

                else if(marker.getSnippet().equals(LYFT)) {
                    startActivity(new Intent(getPackageManager()
                            .getLaunchIntentForPackage(Constants.Strings.LYFT_PACKAGE.getValue())));
                }
                return true;
            }
        }

        lastMarkerShown = marker;

        return false;
    }

    @Override
    public void onDisconnected() {
        // Do nothing
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Do nothing
    }

    private class ToLocationDrawer extends AsyncTask<String, Void, JSONArray> {
        @Override
        protected JSONArray doInBackground(String... strings) {
            String request = "http://maps.googleapis.com/maps/api/directions/json?sensor=true&origin=" +
                    strings[0] + "&destination=" + strings[1];

            String result = gm.getHttpRequest(request);

            JSONArray steps = null;
            try {
                steps = new JSONObject(result).getJSONArray("routes").getJSONObject(0)
                        .getJSONArray("legs").getJSONObject(0).getJSONArray("steps");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return steps;
        }

        @Override
        protected void onPostExecute(JSONArray steps) {
            super.onPostExecute(steps);

            PolylineOptions po = new PolylineOptions();

            for(int i = 0; i < steps.length(); i++) {
                try {
                    JSONObject location = steps.getJSONObject(i)
                            .getJSONObject("end_location");

                    LatLng coords = new LatLng(location.getDouble("lat"),
                            location.getDouble("lng"));

                    po.add(coords);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            po.geodesic(true);
            po.color(Color.BLUE);

            map.addPolyline(po);
        }
    }

    private class LyftLoader extends AsyncTask<String, Void, JSONArray> {
        @Override
        protected JSONArray doInBackground(String... strings) {
            try {
                Header contentType = new BasicHeader("Content-Type", "application/json");
                Header auth = new BasicHeader("Authorization", "fbAccessToken CAAD6nt9dOocBAPGpMipuieMBiaUkLZCEE7FQHZCvT0s1UqH6ZAXW0ZCaET7ZCkuUVTRBLm9nVsLvlxR7PMyJplYZB3Fuuko6JLBolyOcoVd75ukMEZBi4xwbWJ7QB16KEqAJE0WK1n2CMzrCEfrZAtUYDIXKK4V6rQlR3VScaDSnPkGAAeOND62QCefALgdr22s7b5VuOPueSojN9ZBwov6j1zEEnYjKsQ1Ba3vEWX3uzfgZDZD");

                String result = gm.putHttpRequest("marker", strings[0], new LatLng(loc.getLastLocation().getLatitude(),
                        loc.getLastLocation().getLongitude()), contentType, auth);
                        //34.07030187403868, -118.44679702073337), contentType, auth);
                JSONObject jo = new JSONObject(result);
                return jo.getJSONArray("drivers");
            } catch (JSONException e) {
                e.printStackTrace();
            } catch(IllegalStateException e) {

            }

            return null;
        }

        @Override
        protected void onPostExecute(JSONArray drivers) {
            super.onPostExecute(drivers);

            for (int i = 0; i < lyftMarkers.size(); i++) {
                lyftMarkers.get(i).remove();
            }


            lyftMarkers.clear();
            try {
                for(int i = 0; i < drivers.length(); i++) {
                    JSONObject driver = drivers.getJSONObject(i);

                    Marker marker = map.addMarker(new MarkerOptions()
                            .position(new LatLng(driver.getDouble("lat"),
                                    driver.getDouble("lng")))
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.lyft))
                            .title(LYFT)
                            .snippet(LYFT));

                    lyftMarkers.add(marker);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    private class VehicleLoader extends AsyncTask<String, Void, JSONArray> {
        @Override
        protected JSONArray doInBackground(String... strings) {
            String result = gm.getHttpRequest(strings[0]);

            try {
                JSONObject jo = new JSONObject(result);

                JSONArray vehicles = jo.getJSONArray("items");

                return vehicles;
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            super.onPostExecute(jsonArray);

            map.clear();
            PolylineOptions route = new PolylineOptions();
            route.color(Color.GREEN);
            route.geodesic(true);
            route.width(2f);

            try {
                for(int i = 0; i < jsonArray.length(); i++) {
                    double latitude = jsonArray.getJSONObject(i).getDouble("latitude");
                    double longitude = jsonArray.getJSONObject(i).getDouble("longitude");

                    String name = jsonArray.getJSONObject(i).getString("display_name");

                    route.add(new LatLng(latitude, longitude));

                    Marker marker = map.addMarker(new MarkerOptions()
                            .position(new LatLng(latitude, longitude))
                            .title(name)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.bus))
                            .snippet(METRO)
                    );

                    metros.add(marker);

                    //map.addPolyline(route);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void changeMetros(View view) {
        boolean show = ((CheckBox) view).isChecked();
        showMetro(show);
    }

    public void showMetro(boolean show) {
        for(Marker marker : metros) {
            marker.setVisible(show);
        }
    }

    public void purchaseRide(View view) {

        Intent purchaseIntent = new Intent(this, CongratzActivity.class);
        
        purchaseIntent.putExtra(Constants.Strings.SERVICE.getValue(),
                Constants.Ints.LYFT.getValue());

        startActivity(purchaseIntent);
    }
}
