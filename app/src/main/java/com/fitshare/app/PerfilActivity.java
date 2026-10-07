package com.fitshare.app;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class PerfilActivity extends AppCompatActivity {

    TextView tvEmailPerfil;
    RecyclerView recyclerMisRutinas, recyclerMisFotos;
    FirebaseAuth auth;
    FirebaseFirestore db;
    List<Rutina> misRutinas;
    List<Foto> misFotos;
    RutinaAdapter rutinaAdapter;
    FotoAdapter fotoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        tvEmailPerfil = findViewById(R.id.tvEmailPerfil);
        recyclerMisRutinas = findViewById(R.id.recyclerMisRutinas);
        recyclerMisFotos = findViewById(R.id.recyclerMisFotos);

        String email = auth.getCurrentUser().getEmail();
        tvEmailPerfil.setText(email);

        misRutinas = new ArrayList<>();
        misFotos = new ArrayList<>();

        rutinaAdapter = new RutinaAdapter(misRutinas);
        fotoAdapter = new FotoAdapter(misFotos);

        recyclerMisRutinas.setLayoutManager(new LinearLayoutManager(this));
        recyclerMisRutinas.setAdapter(rutinaAdapter);

        recyclerMisFotos.setLayoutManager(new LinearLayoutManager(this));
        recyclerMisFotos.setAdapter(fotoAdapter);

        cargarMisRutinas(email);
        cargarMisFotos(email);
    }

    void cargarMisRutinas(String email) {
        db.collection("rutinas")
                .whereEqualTo("usuarioEmail", email)
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(query -> {
                    misRutinas.clear();
                    for (QueryDocumentSnapshot doc : query) {
                        Rutina r = doc.toObject(Rutina.class);
                        r.setId(doc.getId());
                        misRutinas.add(r);
                    }
                    rutinaAdapter.notifyDataSetChanged();
                });
    }

    void cargarMisFotos(String email) {
        db.collection("fotos")
                .whereEqualTo("usuarioEmail", email)
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(query -> {
                    misFotos.clear();
                    for (QueryDocumentSnapshot doc : query) {
                        Foto f = doc.toObject(Foto.class);
                        f.setId(doc.getId());
                        misFotos.add(f);
                    }
                    fotoAdapter.notifyDataSetChanged();
                });
    }
}