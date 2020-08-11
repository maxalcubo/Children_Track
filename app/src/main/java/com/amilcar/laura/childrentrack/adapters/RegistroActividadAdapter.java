package com.amilcar.laura.childrentrack.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.amilcar.laura.childrentrack.R;
import com.amilcar.laura.childrentrack.activities.VideoPlayer;
import com.amilcar.laura.childrentrack.models.firebase.Acontecimiento;

import java.util.ArrayList;
import java.util.Date;

public class RegistroActividadAdapter extends BaseAdapter {
    ArrayList<Acontecimiento> acontecimientos;

    public RegistroActividadAdapter(ArrayList<Acontecimiento> acontecimientos){
        this.acontecimientos = acontecimientos;
    }

    @Override
    public int getCount() {
        return acontecimientos.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_acontecimiento, null);
        TextView txtTitulo = v.findViewById(R.id.txtTitulo);
        TextView txtDescripcion = v.findViewById(R.id.txtDescripcion);

        txtTitulo.setText(acontecimientos.get(position).titulo);
        txtDescripcion.setText(acontecimientos.get(position).descripcion + "\n" + new Date(acontecimientos.get(position).fecha).toLocaleString());

        if(acontecimientos.get(position).urlMedia != null){
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(parent.getContext(), VideoPlayer.class);
                    intent.putExtra("path", acontecimientos.get(position).urlMedia);
                    intent.putExtra("opcion", VideoPlayer.SOLO_VER);
                    parent.getContext().startActivity(intent);
                }
            });
        }

        return v;
    }
}
