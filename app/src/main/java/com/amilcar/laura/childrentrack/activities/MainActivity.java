package com.amilcar.laura.childrentrack.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.amilcar.laura.childrentrack.services.MovimientoService;
import com.amilcar.laura.childrentrack.R;
import com.amilcar.laura.childrentrack.services.OrientationService;
import com.amilcar.laura.childrentrack.services.TrackingService;

public class MainActivity extends AppCompatActivity {
    public static final boolean SERVERTRACE = false;
    private static final int PERMISSIONS_REQUEST = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btnStart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTrackerService();
            }
        });

        //request necessary permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.RECORD_AUDIO},
                    PERMISSIONS_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode == PERMISSIONS_REQUEST){
            boolean permited = true;
            for(int grantResult : grantResults){
                if(grantResult != PackageManager.PERMISSION_GRANTED){
                    permited = false;
                    //request necessary permits, again
                }
            }
        }
    }


    private void startTrackerService() {
        startService(new Intent(this, TrackingService.class));
        startService(new Intent(this, MovimientoService.class));
        startService(new Intent(this, OrientationService.class));
        Toast.makeText(this, "Services started!", Toast.LENGTH_SHORT).show();
    }

}
