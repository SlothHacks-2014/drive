package com.sloth.drive.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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

    /**
     * Get an HTTP request
     * @param url The url to request
     * @return The http response
     */
    public String getHttpRequest(String url) {

        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(url);

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
