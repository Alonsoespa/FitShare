package com.fitshare.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    Button btnLogout, btnPublicarRutina, btnSubirFoto, btnPerfil;
    RecyclerView recyclerRutinas;
    FirebaseAuth auth;
    FirebaseFirestore db;
    List<Rutina> listaRutinas;
    RutinaAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnLogout = findViewById(R.id.btnLogout);
        btnPublicarRutina = findViewById(R.id.btnPublicarRutina);
        btnSubirFoto = findViewById(R.id.btnSubirFoto);
        btnPerfil = findViewById(R.id.btnPerfil);
        recyclerRutinas = findViewById(R.id.recyclerRutinas);

        listaRutinas = new ArrayList<>();
        adapter = new RutinaAdapter(listaRutinas);
        recyclerRutinas.setLayoutManager(new LinearLayoutManager(this));
        recyclerRutinas.setAdapter(adapter);

        cargarRutinas();

        btnLogout.setOnClickListener(v -> {
            auth.signOut();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        btnPublicarRutina.setOnClickListener(v -> {
            startActivity(new Intent(this, PublicarRutinaActivity.class));
        });

        btnSubirFoto.setOnClickListener(v -> {
            startActivity(new Intent(this, SubirFotoActivity.class));
        });

        btnPerfil.setOnClickListener(v -> {
            startActivity(new Intent(this, PerfilActivity.class));
        });
    }

    void cargarRutinas() {
        db.collection("rutinas")
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null || value == null) return;
                    listaRutinas.clear();
                    for (QueryDocumentSnapshot doc : value) {
                        Rutina r = doc.toObject(Rutina.class);
                        r.setId(doc.getId());
                        listaRutinas.add(r);
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarRutinas();
    }
}