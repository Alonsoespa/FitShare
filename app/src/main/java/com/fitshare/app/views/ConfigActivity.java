package com.fitshare.app.views;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.fitshare.app.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ConfigActivity extends AppCompatActivity {
    private EditText etNombre, etNuevaPassword;
    private Button btnGuardar;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_config);
        db = FirebaseFirestore.getInstance();

        etNombre = findViewById(R.id.etNombreConfig);
        etNuevaPassword = findViewById(R.id.etNuevaPassword);
        btnGuardar = findViewById(R.id.btnGuardarConfig);

        btnGuardar.setOnClickListener(v -> {
            String nuevoNombre = etNombre.getText().toString().trim();
            String nuevaPass = etNuevaPassword.getText().toString().trim();
            FirebaseUser user = auth.getCurrentUser();

            if (user != null) {
                if (!nuevoNombre.isEmpty()) {
                    db.collection("users").document(user.getUid())
                            .update("nombre", nuevoNombre);
                }

                if (!nuevaPass.isEmpty() && nuevaPass.length() >= 6) {
                    user.updatePassword(nuevaPass)
                            .addOnSuccessListener(aVoid -> Toast.makeText(this, "Contraseña actualizada", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
}