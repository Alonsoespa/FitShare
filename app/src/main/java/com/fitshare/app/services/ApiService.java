package com.fitshare.app.services;

import android.os.Handler;
import android.os.Looper;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executors;

public class ApiService {
    public interface ApiCallback {
        void onSuccess(String jsonResponse);
        void onError(Exception e);
    }

    public static void obtenerEjercicios(ApiCallback callback) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                URL url = new URL("https://wger.de/api/v2/exercise/?language=2&limit=10");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    reader.close();

                    new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(sb.toString()));
                } else {
                    new Handler(Looper.getMainLooper()).post(() -> callback.onError(new Exception("Error de respuesta: " + responseCode)));
                }
                conn.disconnect();
            } catch (Exception e) {
                new Handler(Looper.getMainLooper()).post(() -> callback.onError(e));
            }
        });
    }
}