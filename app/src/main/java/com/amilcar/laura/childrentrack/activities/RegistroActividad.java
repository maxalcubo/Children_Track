package com.amilcar.laura.childrentrack.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

import com.amilcar.laura.childrentrack.utils.account.CuentaManager;
import com.amilcar.laura.childrentrack.R;
import com.amilcar.laura.childrentrack.adapters.RegistroActividadAdapter;
import com.amilcar.laura.childrentrack.models.firebase.Acontecimiento;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class RegistroActividad extends AppCompatActivity {

    ListView listView;

    ArrayList<Acontecimiento> acontecimientos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_actividad);

        listView = findViewById(R.id.lista);
        acontecimientos = new ArrayList<>();

        final RegistroActividadAdapter registroActividadAdapter = new RegistroActividadAdapter(acontecimientos);
        FirebaseDatabase.getInstance().getReference(CuentaManager.getInstance().obtenerIdHijo(this) + "/acontecimientos").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                acontecimientos.add(dataSnapshot.getValue(Acontecimiento.class));
                registroActividadAdapter.notifyDataSetChanged();
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

        listView.setAdapter(registroActividadAdapter);
    }
}
