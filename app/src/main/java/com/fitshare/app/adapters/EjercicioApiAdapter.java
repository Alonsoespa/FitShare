package com.fitshare.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.fitshare.app.R;
import com.fitshare.app.models.EjercicioApiResponse.EjercicioApi;
import java.util.List;

public class EjercicioApiAdapter extends RecyclerView.Adapter<EjercicioApiAdapter.ViewHolder> {

    private final Context context;
    private final List<EjercicioApi> lista;

    public EjercicioApiAdapter(Context context, List<EjercicioApi> lista) {
        this.context = context;
        this.lista = lista;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_ejercicio_api, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EjercicioApi ejercicio = lista.get(position);
        holder.tvNombre.setText(ejercicio.getNombreVisible());
        holder.tvCategoria.setText(ejercicio.getCategory() != null ? ejercicio.getCategory().name : "General");

        String url = ejercicio.getUrlImagen();
        if (url != null && !url.isEmpty()) {
            Glide.with(context)
                    .load(url)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_dialog_alert)
                    .into(holder.ivEjercicio);
        } else {
            holder.ivEjercicio.setImageResource(android.R.drawable.ic_menu_gallery);
        }
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvCategoria;
        ImageView ivEjercicio;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreEjercicio);
            tvCategoria = itemView.findViewById(R.id.tvCategoriaEjercicio);
            ivEjercicio = itemView.findViewById(R.id.ivEjercicioApi);
        }
    }
}