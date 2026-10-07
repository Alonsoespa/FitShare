package com.fitshare.app;

public class Foto {
    private String id;
    private String imagenBase64;
    private String descripcion;
    private String usuarioEmail;
    private long timestamp;

    public Foto() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getImagenBase64() { return imagenBase64; }
    public void setImagenBase64(String imagenBase64) { this.imagenBase64 = imagenBase64; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getUsuarioEmail() { return usuarioEmail; }
    public void setUsuarioEmail(String usuarioEmail) { this.usuarioEmail = usuarioEmail; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}