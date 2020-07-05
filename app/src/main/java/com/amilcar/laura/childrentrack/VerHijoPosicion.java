package com.amilcar.laura.childrentrack;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.amilcar.laura.childrentrack.modelosfirebase.Posicion;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class VerHijoPosicion extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    Marker marcadorHijo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_hijo_posicion);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        LatLng peru = new LatLng(-15.464396, -70.1433611);
        marcadorHijo = mMap.addMarker(new MarkerOptions().position(peru).title("Carlitos"));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(16));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(peru));

        final String path = "posicionHijo";
        FirebaseDatabase.getInstance().getReference(path).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Posicion p = dataSnapshot.getValue(Posicion.class);
                marcadorHijo.setPosition(new LatLng(p.latitud, p.longitud));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
