package com.amilcar.laura.childrentrack.activities;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.amilcar.laura.childrentrack.utils.account.CuentaManager;
import com.amilcar.laura.childrentrack.R;
import com.amilcar.laura.childrentrack.models.firebase.Posicion;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

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

        LatLng peru = new LatLng(-15.464396, -70.1433611);
        mMap.moveCamera(CameraUpdateFactory.zoomTo(16));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(peru));

        final Polyline polyline = mMap.addPolyline(new PolylineOptions());
        final ArrayList<LatLng> puntos = new ArrayList<>();

        if(CuentaManager.getInstance().hijoRegistrado(this)) {
            marcadorHijo = mMap.addMarker(new MarkerOptions().position(peru));
            final String path = CuentaManager.getInstance().obtenerIdHijo(this) + "/posicion";

            FirebaseDatabase.getInstance().getReference(path).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Posicion p = dataSnapshot.getValue(Posicion.class);
                    puntos.add(new LatLng(p.latitud, p.longitud));
                    marcadorHijo.setPosition(new LatLng(p.latitud, p.longitud));
                    polyline.setPoints(puntos);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}
