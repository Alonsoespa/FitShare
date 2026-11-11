package com.fitshare.app.views;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fitshare.app.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private EditText etName, etEmail, etPassword;
    private Button btnRegister;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private final String COACH_1 = "braulioespa23@gmail.com";
    private final String COACH_2 = "elipespa23@gmail.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        etName = findViewById(R.id.etNameReg);
        etEmail = findViewById(R.id.etEmailReg);
        etPassword = findViewById(R.id.etPasswordReg);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> registrarUsuario());
    }

    private void registrarUsuario() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String pwd = etPassword.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || pwd.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                String userId = mAuth.getCurrentUser().getUid();
                String rol = (email.equalsIgnoreCase(COACH_1) || email.equalsIgnoreCase(COACH_2)) ? "coach" : "user";

                Map<String, Object> userMap = new HashMap<>();
                userMap.put("nombre", name);
                userMap.put("correo", email);
                userMap.put("rol", rol);
                userMap.put("uid", userId);
                userMap.put("foto_base64", ""); // Inicializamos el espacio para la foto comprimida

                db.collection("users").document(userId).set(userMap).addOnSuccessListener(a -> {
                    Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                    finish();
                });
            } else {
                Toast.makeText(this, "Error de registro", Toast.LENGTH_SHORT).show();
            }
        });
    }
}