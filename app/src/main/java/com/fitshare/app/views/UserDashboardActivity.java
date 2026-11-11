package com.fitshare.app.views;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.fitshare.app.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

public class UserDashboardActivity extends AppCompatActivity {
    private Button btnSubirFoto, btnCerrarSesion;
    private ListView lvRutinas;
    private ImageView ivProgressPhoto;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private ArrayList<String> listaRutinas;
    private ArrayAdapter<String> adapter;

    private final ActivityResultLauncher<String> selectorImagen = registerForActivityResult(
            new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) confirmarSubida(uri);
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Verificar si hay sesión activa
        if (com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser() == null) {
            // Si no hay sesión, manda al usuario al Login (MainActivity)
            startActivity(new android.content.Intent(this, MainActivity.class));
            finish(); // Cierra esta Activity para que no vuelva atrás presionando el botón "Back"
            return;
        }
        setContentView(R.layout.activity_user_dashboard);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        btnSubirFoto = findViewById(R.id.btnSubirFoto);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        lvRutinas = findViewById(R.id.lvRutinas);
        ivProgressPhoto = findViewById(R.id.ivProgressPhoto);

        listaRutinas = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaRutinas);
        lvRutinas.setAdapter(adapter);

        cargarFotoPerfil();
        buscarMiRutina();

        btnSubirFoto.setOnClickListener(v -> selectorImagen.launch("image/*"));

        btnCerrarSesion.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Cerrar Sesión")
                    .setMessage("¿Estás seguro que deseas salir?")
                    .setPositiveButton("Sí", (dialog, which) -> {
                        mAuth.signOut();
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    })
                    .setNegativeButton("No", null).show();
        });
    }

    private void confirmarSubida(Uri uri) {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar")
                .setMessage("¿Deseas enviar esta foto de progreso a tu coach?")
                .setPositiveButton("Sí, enviar", (dialog, which) -> procesarYSubirFotoBase64(uri))
                .setNegativeButton("Cancelar", null).show();
    }

    private void procesarYSubirFotoBase64(Uri uri) {
        Toast.makeText(this, "Procesando imagen...", Toast.LENGTH_SHORT).show();
        try {
            InputStream imageStream = getContentResolver().openInputStream(uri);
            Bitmap bitmapOriginal = BitmapFactory.decodeStream(imageStream);

            int anchoDestino = 400;
            int altoDestino = (bitmapOriginal.getHeight() * anchoDestino) / bitmapOriginal.getWidth();
            Bitmap bitmapReducido = Bitmap.createScaledBitmap(bitmapOriginal, anchoDestino, altoDestino, true);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmapReducido.compress(Bitmap.CompressFormat.JPEG, 70, baos);
            byte[] imageBytes = baos.toByteArray();
            String fotoBase64 = Base64.encodeToString(imageBytes, Base64.DEFAULT);

            String userId = mAuth.getCurrentUser().getUid();
            db.collection("users").document(userId).update("foto_base64", fotoBase64)
                    .addOnSuccessListener(a -> {
                        Toast.makeText(this, "¡Tu coach ya puede ver tu progreso!", Toast.LENGTH_LONG).show();
                        ivProgressPhoto.setImageBitmap(bitmapReducido);
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Error al guardar progreso", Toast.LENGTH_SHORT).show());

        } catch (Exception e) {
            Toast.makeText(this, "Error al procesar la imagen", Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarFotoPerfil() {
        String userId = mAuth.getCurrentUser().getUid();
        db.collection("users").document(userId).get().addOnSuccessListener(doc -> {
            if (doc.contains("foto_base64")) {
                String base64String = doc.getString("foto_base64");
                if (base64String != null && !base64String.isEmpty()) {
                    try {
                        byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        ivProgressPhoto.setImageBitmap(decodedByte);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void buscarMiRutina() {
        String miCorreo = mAuth.getCurrentUser().getEmail();
        db.collection("routines").whereEqualTo("alumno_email", miCorreo).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                listaRutinas.clear();
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    String rutinaFormateada = "💪 Músculos: " + doc.getString("musculos") +
                            "\n📅 Días: " + doc.getString("dias") +
                            "\n📝 Instrucciones: " + doc.getString("instrucciones") +
                            "\n👨‍🏫 Coach: " + doc.getString("coach_email");
                    listaRutinas.add(rutinaFormateada);
                }
                adapter.notifyDataSetChanged();

                if (listaRutinas.isEmpty()) {
                    listaRutinas.add("\nAún no tienes rutinas asignadas.\n\nPídele a tu coach que asigne una a tu correo: " + miCorreo);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }
}