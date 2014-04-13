package com.sloth.drive.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.maps.model.LatLng;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * Created by arithehun on 4/12/14.
 */
public class GlobalMethods {
    /**
     * The context to operate in
     */
    private Activity context;

    public GlobalMethods(Activity context) {
        this.context = context;
    }

    public String putHttpRequest(String value, String url, LatLng coords, Header... headers) {
        HttpClient client = new DefaultHttpClient();
        HttpPut request = new HttpPut(url);

        JSONObject coordinates = new JSONObject(), marker = new JSONObject();
        try {
            coordinates.put("lat", coords.latitude);
            coordinates.put("lng", coords.longitude);

            if(value.equals("startLocation")) {
                marker.put("id", 185005387);
            }
            marker.put(value, coordinates);

            request.setEntity(new ByteArrayEntity(marker.toString().getBytes("UTF8")));

        } catch (JSONException e) {
            e.printStackTrace();
        } catch(UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        for(int i = 0; i < headers.length; i++) {
            request.addHeader(headers[i]);
        }

        return sendRequest(client, request);
    }

    public String postHttpRequest(String value, String url, LatLng coords, Header... headers) {
        HttpClient client = new DefaultHttpClient();
        HttpPost request = new HttpPost(url);

        JSONObject coordinates = new JSONObject(), marker = new JSONObject();
        try {
            coordinates.put("lat", coords.latitude);
            coordinates.put("lng", coords.longitude);

            if(value.equals("startLocation")) {
                marker.put("id", 185005387);
            }
            marker.put(value, coordinates);

            request.setEntity(new ByteArrayEntity(marker.toString().getBytes("UTF8")));

        } catch (JSONException e) {
            e.printStackTrace();
        } catch(UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        for(int i = 0; i < headers.length; i++) {
            request.addHeader(headers[i]);
        }

        return sendRequest(client, request);
    }

    public String getHttpRequest(String url) {
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(url);

        return sendRequest(client, request);
    }

    /**
     * Get an HTTP request
     * @return The http response
     */
    private String sendRequest(HttpClient client, HttpUriRequest request) {

        try {
            HttpResponse res = client.execute(request);

            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(res.getEntity().getContent()));

            String result = "", line = "";
            while((line = reader.readLine()) != null) {
                result += line;
            }

            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }
}
