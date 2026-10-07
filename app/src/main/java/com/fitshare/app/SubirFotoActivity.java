package com.fitshare.app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class SubirFotoActivity extends AppCompatActivity {

    ImageView imgPreview;
    Button btnSeleccionarFoto, btnSubirFoto;
    EditText etDescripcionFoto;
    Uri imagenSeleccionada;
    FirebaseAuth auth;
    FirebaseFirestore db;

    ActivityResultLauncher<String> seleccionarImagen = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    imagenSeleccionada = uri;
                    imgPreview.setImageURI(uri);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subir_foto);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        imgPreview = findViewById(R.id.imgPreview);
        btnSeleccionarFoto = findViewById(R.id.btnSeleccionarFoto);
        btnSubirFoto = findViewById(R.id.btnSubirFoto);
        etDescripcionFoto = findViewById(R.id.etDescripcionFoto);

        btnSeleccionarFoto.setOnClickListener(v -> {
            seleccionarImagen.launch("image/*");
        });

        btnSubirFoto.setOnClickListener(v -> {
            String descripcion = etDescripcionFoto.getText().toString().trim();

            if (imagenSeleccionada == null) {
                Toast.makeText(this, "Selecciona una foto primero", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(descripcion)) {
                Toast.makeText(this, "Agrega una descripción", Toast.LENGTH_SHORT).show();
                return;
            }

            btnSubirFoto.setEnabled(false);
            btnSubirFoto.setText("Subiendo...");

            try {
                InputStream inputStream = getContentResolver().openInputStream(imagenSeleccionada);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                // Reducir tamaño para que quepa en Firestore
                Bitmap resized = Bitmap.createScaledBitmap(bitmap, 400, 600, true);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                resized.compress(Bitmap.CompressFormat.JPEG, 60, baos);
                byte[] imageBytes = baos.toByteArray();
                String base64Image = Base64.encodeToString(imageBytes, Base64.DEFAULT);

                String email = auth.getCurrentUser().getEmail();

                Map<String, Object> foto = new HashMap<>();
                foto.put("imagenBase64", base64Image);
                foto.put("descripcion", descripcion);
                foto.put("usuarioEmail", email);
                foto.put("timestamp", System.currentTimeMillis());

                db.collection("fotos")
                        .add(foto)
                        .addOnSuccessListener(docRef -> {
                            Toast.makeText(this, "Foto subida exitosamente", Toast.LENGTH_SHORT).show();
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            btnSubirFoto.setEnabled(true);
                            btnSubirFoto.setText("Subir Foto");
                            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });

            } catch (Exception e) {
                btnSubirFoto.setEnabled(true);
                btnSubirFoto.setText("Subir Foto");
                Toast.makeText(this, "Error al procesar imagen", Toast.LENGTH_SHORT).show();
            }
        });
    }
}