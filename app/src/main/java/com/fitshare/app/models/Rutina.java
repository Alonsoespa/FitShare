package com.fitshare.app.models;

public class Rutina {
    private String id;
    private String nombre;
    private String grupoMuscular;
    private String descripcion;
    private String usuarioEmail;

    // Constructor vacío obligatorio para Firestore
    public Rutina() {}

    // Constructor completo con los 5 parámetros
    public Rutina(String id, String nombre, String grupoMuscular, String descripcion, String usuarioEmail) {
        this.id = id;
        this.nombre = nombre;
        this.grupoMuscular = grupoMuscular;
        this.descripcion = descripcion;
        this.usuarioEmail = usuarioEmail;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNombre() { return nombre; }
    public String getGrupoMuscular() { return grupoMuscular; }
    public String getDescripcion() { return descripcion; }
    public String getUsuarioEmail() { return usuarioEmail; }
}