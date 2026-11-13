package com.fitshare.app.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class EjercicioApiResponse {
    @SerializedName("results")
    private List<EjercicioApi> results;

    public List<EjercicioApi> getResults() {
        return results;
    }

    public static class EjercicioApi {
        private int id;
        private Categoria category;
        private List<Traduccion> translations;
        private List<ImagenEjercicio> images;

        public int getId() { return id; }
        public Categoria getCategory() { return category; }
        public List<Traduccion> getTranslations() { return translations; }
        public List<ImagenEjercicio> getImages() { return images; }

        // Helper para obtener el nombre limpio
        public String getNombreVisible() {
            if (translations != null && !translations.isEmpty()) {
                return translations.get(0).name;
            }
            return "Ejercicio #" + id;
        }

        // Helper para obtener la URL de la primera imagen disponible
        public String getUrlImagen() {
            if (images != null && !images.isEmpty()) {
                return images.get(0).image;
            }
            return null;
        }
    }

    public static class Categoria {
        public String name;
    }

    public static class Traduccion {
        public String name;
        public String description;
    }

    public static class ImagenEjercicio {
        public String image;
    }
}