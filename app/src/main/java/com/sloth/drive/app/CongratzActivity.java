package com.sloth.drive.app;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;

public class CongratzActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_congratz);

        int icon = getIntent().getExtras().getInt(Constants.Strings.SERVICE.getValue());

        ImageView logo = (ImageView) findViewById(R.id.final_token);

        logo.setImageResource(icon);
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
        startActivity(new Intent(this, ChatActivity.class));
    }

}
