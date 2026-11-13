package com.fitshare.app.views;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.Gravity;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fitshare.app.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CoachDashboardActivity extends AppCompatActivity {
    private ListView lvDatos;
    private Button btnAsignar, btnAlumnos, btnCerrar, btnVerCatalogoApi; // <-- Añadido botón para API
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private ArrayList<String> listaDatos;
    private ArrayList<String> listaIds;
    private ArrayAdapter<String> adapter;
    private boolean viendoRutinas = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Protección de ruta: Verificar si hay sesión activa
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_coach_dashboard);

        db = FirebaseFirestore.getInstance();
        lvDatos = findViewById(R.id.lvDatosCoach);
        btnAsignar = findViewById(R.id.btnAsignarRutina);
        btnAlumnos = findViewById(R.id.btnVerEstudiantes);
        btnCerrar = findViewById(R.id.btnCerrarSesionCoach);

        // Inicializar el botón del catálogo de la API (Asegúrate de agregarlo en tu activity_coach_dashboard.xml)
        btnVerCatalogoApi = findViewById(R.id.btnVerCatalogoApi);

        listaDatos = new ArrayList<>();
        listaIds = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaDatos);
        lvDatos.setAdapter(adapter);

        cargarRutinas();

        btnAsignar.setOnClickListener(v -> mostrarDialogoRutina(null, "", "", "", ""));
        btnAlumnos.setOnClickListener(v -> cargarAlumnos());

        // Evento para abrir el catálogo de ejercicios consumido por la API REST (Retrofit + Glide)
        if (btnVerCatalogoApi != null) {
            btnVerCatalogoApi.setOnClickListener(v -> {
                startActivity(new Intent(this, CatalogoEjerciciosActivity.class));
            });
        }

        btnCerrar.setOnClickListener(v -> {
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

        lvDatos.setOnItemClickListener((parent, view, position, id) -> {
            if (position < listaIds.size()) {
                String docId = listaIds.get(position);
                if (viendoRutinas) {
                    opcionesRutina(docId);
                } else {
                    verProgresoAlumno(docId);
                }
            }
        });
    }

    private void cargarRutinas() {
        viendoRutinas = true;
        if (mAuth.getCurrentUser() == null) return;
        String miCorreo = mAuth.getCurrentUser().getEmail();

        db.collection("routines").get().addOnCompleteListener(task -> {
            listaDatos.clear();
            listaIds.clear();
            if (task.isSuccessful() && task.getResult() != null) {
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    String coachEmail = doc.getString("coach_email");
                    String usuarioEmail = doc.getString("usuarioEmail");
                    String alumnoEmail = doc.getString("alumno_email");

                    // Mostrar si el coach fue el creador o si está asignada a él
                    if ((miCorreo != null) && (miCorreo.equals(coachEmail) || miCorreo.equals(usuarioEmail) || miCorreo.equals(alumnoEmail))) {
                        listaIds.add(doc.getId());

                        String alumno = alumnoEmail != null ? alumnoEmail : (usuarioEmail != null ? usuarioEmail : "Sin usuario");
                        String titulo = doc.getString("dias") != null ? doc.getString("dias") : doc.getString("nombre");
                        if (titulo == null) titulo = "Rutina General";

                        listaDatos.add("Usuario/Alumno: " + alumno + "\nRutina/Días: " + titulo);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void cargarAlumnos() {
        viendoRutinas = false;
        db.collection("users").get().addOnCompleteListener(task -> {
            listaDatos.clear();
            listaIds.clear();
            if (task.isSuccessful() && task.getResult() != null) {
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    String rol = doc.getString("rol");
                    if ("user".equalsIgnoreCase(rol) || "usuario".equalsIgnoreCase(rol)) {
                        listaIds.add(doc.getId());
                        String nombre = doc.getString("nombre") != null ? doc.getString("nombre") : "Usuario";
                        String correo = doc.getString("correo") != null ? doc.getString("correo") : doc.getString("email");
                        listaDatos.add(nombre + "\n" + correo);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void opcionesRutina(String docId) {
        new AlertDialog.Builder(this)
                .setTitle("Opciones de Rutina")
                .setItems(new String[]{"Editar", "Eliminar"}, (dialog, which) -> {
                    if (which == 0) {
                        db.collection("routines").document(docId).get().addOnSuccessListener(doc -> {
                            if (doc.exists()) {
                                String alu = doc.getString("alumno_email") != null ? doc.getString("alumno_email") : doc.getString("usuarioEmail");
                                String mus = doc.getString("musculos") != null ? doc.getString("musculos") : doc.getString("grupoMuscular");
                                String dia = doc.getString("dias") != null ? doc.getString("dias") : doc.getString("nombre");
                                String ins = doc.getString("instrucciones") != null ? doc.getString("instrucciones") : doc.getString("descripcion");

                                mostrarDialogoRutina(docId, alu, mus, dia, ins);
                            }
                        });
                    } else {
                        new AlertDialog.Builder(this)
                                .setTitle("Confirmar")
                                .setMessage("¿Borrar esta rutina permanentemente?")
                                .setPositiveButton("Sí, borrar", (d, w) -> {
                                    db.collection("routines").document(docId).delete().addOnSuccessListener(v -> {
                                        Toast.makeText(this, "Rutina eliminada", Toast.LENGTH_SHORT).show();
                                        cargarRutinas();
                                    });
                                }).setNegativeButton("Cancelar", null).show();
                    }
                }).show();
    }

    private void mostrarDialogoRutina(String docId, String alu, String mus, String dia, String ins) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(docId == null ? "Nueva Rutina" : "Editar Rutina");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 40, 40, 40);

        EditText etAlumno = new EditText(this); etAlumno.setHint("Correo del Alumno"); etAlumno.setText(alu); layout.addView(etAlumno);
        EditText etMusculos = new EditText(this); etMusculos.setHint("Músculos / Grupo"); etMusculos.setText(mus); layout.addView(etMusculos);
        EditText etDias = new EditText(this); etDias.setHint("Días / Nombre"); etDias.setText(dia); layout.addView(etDias);
        EditText etInst = new EditText(this); etInst.setHint("Instrucciones / Descripción"); etInst.setText(ins); layout.addView(etInst);

        builder.setView(layout);
        builder.setPositiveButton("Guardar", (dialog, which) -> {
            if (mAuth.getCurrentUser() == null) return;
            String correoCoach = mAuth.getCurrentUser().getEmail();
            String correoAlumno = etAlumno.getText().toString().trim();
            String musculosTxt = etMusculos.getText().toString().trim();
            String diasTxt = etDias.getText().toString().trim();
            String instTxt = etInst.getText().toString().trim();

            Map<String, Object> rutina = new HashMap<>();
            rutina.put("coach_email", correoCoach);
            rutina.put("alumno_email", correoAlumno);
            rutina.put("musculos", musculosTxt);
            rutina.put("dias", diasTxt);
            rutina.put("instrucciones", instTxt);

            // Mapeo duplicado para mantener compatibilidad con el modelo Rutina
            rutina.put("usuarioEmail", correoAlumno);
            rutina.put("grupoMuscular", musculosTxt);
            rutina.put("nombre", diasTxt);
            rutina.put("descripcion", instTxt);

            if (docId == null) {
                String idGenerado = UUID.randomUUID().toString();
                rutina.put("id", idGenerado);
                db.collection("routines").document(idGenerado).set(rutina).addOnSuccessListener(v -> {
                    asignarAlumnoACoach(correoAlumno, correoCoach);
                    Toast.makeText(this, "Rutina asignada exitosamente", Toast.LENGTH_SHORT).show();
                    cargarRutinas();
                });
            } else {
                db.collection("routines").document(docId).update(rutina).addOnSuccessListener(v -> {
                    Toast.makeText(this, "Rutina actualizada", Toast.LENGTH_SHORT).show();
                    cargarRutinas();
                });
            }
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void verProgresoAlumno(String userId) {
        db.collection("users").document(userId).get().addOnSuccessListener(doc -> {
            if (!doc.exists()) return;

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            String nombreAlumno = doc.getString("nombre") != null ? doc.getString("nombre") : "Alumno";
            builder.setTitle("Progreso: " + nombreAlumno);

            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(32, 32, 32, 32);
            layout.setGravity(Gravity.CENTER);

            ImageView ivFoto = new ImageView(this);
            ivFoto.setLayoutParams(new LinearLayout.LayoutParams(500, 500));

            String base64String = doc.getString("foto_base64");
            if (base64String != null && base64String.trim().length() > 0) {
                try {
                    byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    ivFoto.setImageBitmap(decodedByte);
                    layout.addView(ivFoto);
                } catch (Exception e) {
                    TextView tvError = new TextView(this);
                    tvError.setText("Error al cargar la imagen.");
                    layout.addView(tvError);
                }
            } else {
                TextView tvSinFoto = new TextView(this);
                tvSinFoto.setText("El alumno no ha subido fotos.");
                layout.addView(tvSinFoto);
            }

            builder.setView(layout);
            builder.setPositiveButton("Cerrar", null);
            builder.show();
        });
    }

    private void asignarAlumnoACoach(String correoAlumno, String correoCoach) {
        if (correoAlumno.isEmpty()) return;

        db.collection("users")
                .whereEqualTo("correo", correoAlumno)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        String docId = queryDocumentSnapshots.getDocuments().get(0).getId();
                        db.collection("users").document(docId)
                                .update("coachEmail", correoCoach)
                                .addOnSuccessListener(aVoid ->
                                        Toast.makeText(this, "Alumno vinculado al coach", Toast.LENGTH_SHORT).show()
                                );
                    }
                });
    }
}