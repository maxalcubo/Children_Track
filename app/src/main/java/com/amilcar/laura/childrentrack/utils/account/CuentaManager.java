package com.amilcar.laura.childrentrack.utils.account;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CuentaManager {
    private static CuentaManager singleton;

    private CuentaManager(){

    }
    public static CuentaManager getInstance(){
        if(singleton == null){
            singleton = new CuentaManager();
        }
        return singleton;
    }
    private String generarCuenta(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("usuarios").push();
        databaseReference.setValue(true);

        return databaseReference.getKey();
    }
    private void establecerCuentaId(Context context, String id){
        SharedPreferences sharedPref = context.getSharedPreferences("cuenta", Context.MODE_PRIVATE);
        sharedPref.edit().putString("id", id).commit();
    }

    public String obtenerId(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences("cuenta", Context.MODE_PRIVATE);
        return sharedPref.getString("id", null);
    }
    public void intentarCrearId(Context context){
        if(obtenerId(context) == null){
            String id = generarCuenta();
            establecerCuentaId(context, id);
        }
    }

    public void registrarHijo(Context context, String id){
        SharedPreferences sharedPref = context.getSharedPreferences("cuenta", Context.MODE_PRIVATE);
        sharedPref.edit().putString("idHijo", id).commit();
    }

    public String obtenerIdHijo(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences("cuenta", Context.MODE_PRIVATE);
        return sharedPref.getString("idHijo", null);
    }

    public boolean hijoRegistrado(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences("cuenta", Context.MODE_PRIVATE);
        return sharedPref.getString("idHijo", null) != null;
    }

}
