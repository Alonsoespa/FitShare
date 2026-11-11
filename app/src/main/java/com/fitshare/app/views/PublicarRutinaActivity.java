package com.fitshare.app.views;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.fitshare.app.R;
import com.fitshare.app.models.Rutina;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PublicarRutinaActivity extends AppCompatActivity {

    private EditText etNombreRutina, etGrupoMuscular, etDescripcion;
    private Button btnGuardarRutina;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Protección de ruta: verificar sesión activa
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            finish();
            return;
        }

        setContentView(R.layout.activity_publicar_rutina);
        db = FirebaseFirestore.getInstance();

        etNombreRutina = findViewById(R.id.etNombreRutina);
        etGrupoMuscular = findViewById(R.id.etGrupoMuscular);
        etDescripcion = findViewById(R.id.etDescripcion);
        btnGuardarRutina = findViewById(R.id.btnGuardarRutina);

        btnGuardarRutina.setOnClickListener(v -> guardarRutina());
    }

    private void guardarRutina() {
        String nombre = etNombreRutina.getText().toString().trim();
        String grupoMuscular = etGrupoMuscular.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        FirebaseUser user = auth.getCurrentUser();

        if (user == null || user.getEmail() == null) {
            Toast.makeText(this, "Sesión no válida", Toast.LENGTH_SHORT).show();
            return;
        }

        if (nombre.isEmpty() || descripcion.isEmpty()) {
            Toast.makeText(this, "Por favor completa el nombre y la descripción", Toast.LENGTH_SHORT).show();
            return;
        }

        String idRutina = UUID.randomUUID().toString();
        String correoUsuario = user.getEmail();

        // Mapa compatible tanto con el modelo Rutina como con el CoachDashboard (colección "routines")
        Map<String, Object> rutinaData = new HashMap<>();
        rutinaData.put("id", idRutina);
        rutinaData.put("nombre", nombre);
        rutinaData.put("grupoMuscular", grupoMuscular);
        rutinaData.put("descripcion", descripcion);
        rutinaData.put("usuarioEmail", correoUsuario);

        // Campos compatibles con CoachDashboardActivity
        rutinaData.put("coach_email", correoUsuario);
        rutinaData.put("alumno_email", correoUsuario);
        rutinaData.put("musculos", grupoMuscular);
        rutinaData.put("dias", nombre);
        rutinaData.put("instrucciones", descripcion);

        // Guardar en la colección unificada "routines"
        db.collection("routines").document(idRutina)
                .set(rutinaData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(PublicarRutinaActivity.this, "¡Rutina creada con éxito!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(PublicarRutinaActivity.this, "Error al guardar: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }
}