package com.fitshare.app.views;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fitshare.app.R;
import com.fitshare.app.adapters.EjercicioApiAdapter;
import com.fitshare.app.models.EjercicioApiResponse;
import com.fitshare.app.services.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CatalogoEjerciciosActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EjercicioApiAdapter adapter;
    private List<EjercicioApiResponse.EjercicioApi> listaEjercicios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalogo_ejercicios);

        recyclerView = findViewById(R.id.recyclerEjerciciosApi);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listaEjercicios = new ArrayList<>();
        adapter = new EjercicioApiAdapter(this, listaEjercicios);
        recyclerView.setAdapter(adapter);

        cargarEjerciciosDesdeApi();
    }

    private void cargarEjerciciosDesdeApi() {
        Toast.makeText(this, "Cargando ejercicios desde la API...", Toast.LENGTH_SHORT).show();

        // Consumiendo servicio web REST mediante Retrofit (Idioma Inglés=2 o Español=4, límite de 20 resultados)
        RetrofitClient.getApiService().obtenerEjercicios(2, 20).enqueue(new Callback<EjercicioApiResponse>() {
            @Override
            public void onResponse(Call<EjercicioApiResponse> call, Response<EjercicioApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaEjercicios.clear();
                    listaEjercicios.addAll(response.body().getResults());
                    adapter.notifyDataSetChanged();
                } else {
                    // Cámbiarlo por esto:
                    Toast.makeText(CatalogoEjerciciosActivity.this, "Error en la respuesta del servidor", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<EjercicioApiResponse> call, Throwable t) {
                Toast.makeText(CatalogoEjerciciosActivity.this, "Fallo de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}