package com.fitshare.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RutinaAdapter extends RecyclerView.Adapter<RutinaAdapter.RutinaViewHolder> {

    List<Rutina> listaRutinas;

    public RutinaAdapter(List<Rutina> listaRutinas) {
        this.listaRutinas = listaRutinas;
    }

    @NonNull
    @Override
    public RutinaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_rutina, parent, false);
        return new RutinaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RutinaViewHolder holder, int position) {
        Rutina rutina = listaRutinas.get(position);
        holder.tvNombre.setText(rutina.getNombre());
        holder.tvGrupo.setText(rutina.getGrupoMuscular());
        holder.tvDescripcion.setText(rutina.getDescripcion());
        holder.tvUsuario.setText("Por: " + rutina.getUsuarioEmail());
    }

    @Override
    public int getItemCount() {
        return listaRutinas.size();
    }

    static class RutinaViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvGrupo, tvDescripcion, tvUsuario;

        public RutinaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvGrupo = itemView.findViewById(R.id.tvGrupo);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcion);
            tvUsuario = itemView.findViewById(R.id.tvUsuario);
        }
    }
}