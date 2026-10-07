package com.fitshare.app;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class FotoAdapter extends RecyclerView.Adapter<FotoAdapter.FotoViewHolder> {

    List<Foto> listaFotos;

    public FotoAdapter(List<Foto> listaFotos) {
        this.listaFotos = listaFotos;
    }

    @NonNull
    @Override
    public FotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_foto, parent, false);
        return new FotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FotoViewHolder holder, int position) {
        Foto foto = listaFotos.get(position);
        holder.tvDescripcionFoto.setText(foto.getDescripcion());
        holder.tvUsuarioFoto.setText("Por: " + foto.getUsuarioEmail());

        try {
            byte[] decodedBytes = Base64.decode(foto.getImagenBase64(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            holder.imgFoto.setImageBitmap(bitmap);
        } catch (Exception e) {
            holder.imgFoto.setImageResource(android.R.drawable.ic_menu_gallery);
        }
    }

    @Override
    public int getItemCount() {
        return listaFotos.size();
    }

    static class FotoViewHolder extends RecyclerView.ViewHolder {
        ImageView imgFoto;
        TextView tvDescripcionFoto, tvUsuarioFoto;

        public FotoViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFoto = itemView.findViewById(R.id.imgFoto);
            tvDescripcionFoto = itemView.findViewById(R.id.tvDescripcionFoto);
            tvUsuarioFoto = itemView.findViewById(R.id.tvUsuarioFoto);
        }
    }
}