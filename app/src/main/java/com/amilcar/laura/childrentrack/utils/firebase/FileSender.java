package com.amilcar.laura.childrentrack.utils.firebase;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.amilcar.laura.childrentrack.utils.account.CuentaManager;
import com.amilcar.laura.childrentrack.models.firebase.Acontecimiento;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class FileSender {
    public static final int AUDIO = 1, VIDEO = 2;
    private String fileName;
    private int opcion;


    public FileSender(String fileName, int opcion){
        this.fileName = fileName;
        this.opcion = opcion;
    }

    public void send(Context context){
        final DatabaseReference df = FirebaseDatabase.getInstance().getReference(CuentaManager.getInstance().obtenerIdHijo(context) + "/acontecimientos").push();
        InputStream stream = null;
        try {
            stream = new FileInputStream(new File(fileName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        final StorageReference fileReference = FirebaseStorage.getInstance().getReference().child(df.getKey() + (opcion == VIDEO ? ".mp4" : ".mp3"));

        UploadTask uploadTask = fileReference.putStream(stream);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //success
                if(opcion == AUDIO) {
                    df.setValue(new Acontecimiento("El niño dejo de moverse", "El niño ha estado en un mismo lugar mas de un minuto, grabamos el sonido del ambiente para que lo puedas escuchar. Haga click", System.currentTimeMillis(), taskSnapshot.getDownloadUrl().toString()));
                }else{
                    df.setValue(new Acontecimiento("El niño le ha enviado un video", "Haga click para poder ver el video", System.currentTimeMillis(), taskSnapshot.getDownloadUrl().toString()));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("alv", e.getMessage());
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                Log.i("pogreso", taskSnapshot.getBytesTransferred() + " - " + taskSnapshot.getTotalByteCount());
            }
        });

        fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.i("alv", uri.toString());
            }
        });
    }
}
