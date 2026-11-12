package com.fitshare.app.repositories;

import android.content.Context;
import androidx.appcompat.app.AlertDialog;
import com.google.firebase.firestore.FirebaseFirestore;
import com.fitshare.app.models.Rutina;

public class RutinaRepository {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void actualizarRutina(String idRutina, Rutina rutinaActualizada) {
        db.collection("rutinas").document(idRutina).set(rutinaActualizada);
    }

    public void eliminarRutinaConConfirmacion(Context context, String idRutina, Runnable onSuccess) {
        new AlertDialog.Builder(context)
                .setTitle("Confirmar eliminación")
                .setMessage("¿Estás seguro de que deseas eliminar esta rutina?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    db.collection("rutinas").document(idRutina)
                            .delete()
                            .addOnSuccessListener(aVoid -> onSuccess.run());
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}