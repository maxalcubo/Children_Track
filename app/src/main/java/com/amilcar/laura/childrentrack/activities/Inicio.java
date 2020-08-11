package com.amilcar.laura.childrentrack.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.Menu;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amilcar.laura.childrentrack.utils.account.CuentaManager;
import com.amilcar.laura.childrentrack.R;
import com.amilcar.laura.childrentrack.services.MovimientoService;
import com.amilcar.laura.childrentrack.services.OrientationService;
import com.amilcar.laura.childrentrack.services.TrackingService;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class Inicio extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    public static final boolean SERVERTRACE = false;
    private static final int PERMISSIONS_REQUEST = 100;
    private static String[] PERMISOS_SERVICIOS = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.RECORD_AUDIO
    };

    DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);


        navigationView.setNavigationItemSelectedListener(this);
        toggle.syncState();

        CuentaManager.getInstance().intentarCrearId(this);

        String id = CuentaManager.getInstance().obtenerId(this);

        TextView txtMiId = findViewById(R.id.txt_mi_id);
        txtMiId.setText((id == null) ? "Vuelva a ingresar para que su ID se genere automaticamente. (Se necesita coneccion a Internet)" : ("Mi ID es: " + id));

        if(CuentaManager.getInstance().hijoRegistrado(this)) {
            TextView txtHijoId = findViewById(R.id.txt_hijo_id);
            txtHijoId.setText("El Id de su hijo es: " + CuentaManager.getInstance().obtenerIdHijo(this));
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.inicio, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {

        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawer.closeDrawer(GravityCompat.START);
        switch (item.getItemId()) {

            case R.id.nav_ver_mapa: {
            Intent intent = new Intent(Inicio.this, VerHijoPosicion.class);
            startActivity(intent);
            return true;
        }
        case R.id.nav_grabar_video: {
            Intent intent = new Intent(Inicio.this, CameraActivity.class);
            startActivity(intent);
            return true;
        }
        case R.id.nav_registrar_hijo: {
            registrarHijo();
            return true;
        }
        case R.id.nav_registro_acontecimientos: {
            Intent intent = new Intent(Inicio.this, RegistroActividad.class);
            startActivity(intent);
            return true;
        }
    }

        return false;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_iniciar_servicios: {
                tratarIniciarServicios();
                break;
            }
        }

        return true;
    }

    private void tratarIniciarServicios(){
        if(!hasPermissionsGranted(PERMISOS_SERVICIOS)){
            ActivityCompat.requestPermissions(this, PERMISOS_SERVICIOS, PERMISSIONS_REQUEST);
        }else{
            iniciarServicios();
        }
    }

    private void iniciarServicios() {
        startService(new Intent(this, TrackingService.class));
        startService(new Intent(this, MovimientoService.class));
        startService(new Intent(this, OrientationService.class));
        Toast.makeText(this, "Services started!", Toast.LENGTH_SHORT).show();
    }
    private boolean hasPermissionsGranted(String[] permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode == PERMISSIONS_REQUEST){
            if (grantResults.length != PERMISOS_SERVICIOS.length) {
                Toast.makeText(this, "La aplicacion necesita esos permisos", Toast.LENGTH_SHORT).show();
            }else{
                iniciarServicios();
            }
        }
    }

    public void registrarHijo(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Escriba el id de su hijo");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("Por ejemplo: -MEdxf4fefg4wef...");
        if(CuentaManager.getInstance().hijoRegistrado(this)){
            input.setText(CuentaManager.getInstance().obtenerIdHijo(this));
        }
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String id = input.getText().toString().trim();
                Toast.makeText(Inicio.this, id, Toast.LENGTH_SHORT).show();
                CuentaManager.getInstance().registrarHijo(Inicio.this, id);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

}
