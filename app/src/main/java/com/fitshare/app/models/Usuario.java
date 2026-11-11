package com.fitshare.app.models;

import com.google.firebase.firestore.PropertyName;

public class Usuario {
    private String uid;
    private String nombre;
    private String correo;
    private String rol;
    private String coachEmail;

    // Constructor vacío obligatorio para que Firestore pueda instanciar la clase
    public Usuario() {}

    public Usuario(String uid, String nombre, String correo, String rol, String coachEmail) {
        this.uid = uid;
        this.nombre = nombre;
        this.correo = correo;
        this.rol = rol;
        this.coachEmail = coachEmail;
    }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    // Anotaciones PropertyName para vincular coachEmail en Java con coach_email o coachEmail en Firestore
    @PropertyName("coach_email")
    public String getCoachEmail() { return coachEmail; }

    @PropertyName("coach_email")
    public void setCoachEmail(String coachEmail) { this.coachEmail = coachEmail; }
}