package com.fitshare.app.services;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.fitshare.app.models.Usuario;

public class AuthService {
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void registrarUsuario(String nombre, String correo, String password, OnCompleteListener<Void> onComplete) {
        auth.createUserWithEmailAndPassword(correo, password).addOnSuccessListener(authResult -> {
            String uid = authResult.getUser().getUid();
            Usuario nuevoUsuario = new Usuario(uid, nombre, correo, "usuario", "");

            db.collection("users").document(uid).set(nuevoUsuario)
                    .addOnCompleteListener(onComplete);
        });
    }

    public void recuperarPassword(String correo, OnCompleteListener<Void> onComplete) {
        auth.sendPasswordResetEmail(correo).addOnCompleteListener(onComplete);
    }
}