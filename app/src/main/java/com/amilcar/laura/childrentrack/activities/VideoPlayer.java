package com.amilcar.laura.childrentrack.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.amilcar.laura.childrentrack.R;
import com.amilcar.laura.childrentrack.utils.firebase.FileSender;

public class VideoPlayer extends AppCompatActivity {

    public static final int VER_Y_ENVIAR = 1, SOLO_VER = 2;

    VideoView videoView;
    String videoPath;
    MediaController mediaController;

    int opcion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        videoView = findViewById(R.id.videoView);
        mediaController = new MediaController(this);
        videoView.setMediaController(mediaController);

        videoPath = getIntent().getStringExtra("path");
        opcion = getIntent().getIntExtra("opcion", VER_Y_ENVIAR);
        videoView.setVideoPath(videoPath);

        //seekTo 1, para que haya una imagen previa en el reproductor
        videoView.seekTo(1);
        videoView.post(new Runnable() {
            @Override
            public void run() {
                //show(0) para que los controles del video esten visibles
                mediaController.show(0);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(opcion == VER_Y_ENVIAR) {
            getMenuInflater().inflate(R.menu.enviar_video, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.menu_op_enviar_video){
            Toast.makeText(this, "Enviando video al servidor", Toast.LENGTH_SHORT).show();
            new FileSender(videoPath, FileSender.VIDEO).send(this);
        }
        return true;
    }
}
