package com.fitshare.app;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class PublicarRutinaActivity extends AppCompatActivity {

    EditText etNombreRutina, etGrupoMuscular, etDescripcion;
    Button btnGuardarRutina;
    FirebaseAuth auth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publicar_rutina);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etNombreRutina = findViewById(R.id.etNombreRutina);
        etGrupoMuscular = findViewById(R.id.etGrupoMuscular);
        etDescripcion = findViewById(R.id.etDescripcion);
        btnGuardarRutina = findViewById(R.id.btnGuardarRutina);

        btnGuardarRutina.setOnClickListener(v -> {
            String nombre = etNombreRutina.getText().toString().trim();
            String grupo = etGrupoMuscular.getText().toString().trim();
            String descripcion = etDescripcion.getText().toString().trim();

            if (TextUtils.isEmpty(nombre) || TextUtils.isEmpty(grupo) || TextUtils.isEmpty(descripcion)) {
                Toast.makeText(this, "Llena todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            String email = auth.getCurrentUser().getEmail();

            Map<String, Object> rutina = new HashMap<>();
            rutina.put("nombre", nombre);
            rutina.put("grupoMuscular", grupo);
            rutina.put("descripcion", descripcion);
            rutina.put("usuarioEmail", email);
            rutina.put("timestamp", System.currentTimeMillis());

            db.collection("rutinas")
                    .add(rutina)
                    .addOnSuccessListener(ref -> {
                        Toast.makeText(this, "Rutina publicada", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }
}