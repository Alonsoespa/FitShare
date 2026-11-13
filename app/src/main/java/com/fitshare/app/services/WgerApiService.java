package com.fitshare.app.services;

import com.fitshare.app.models.EjercicioApiResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WgerApiService {
    @GET("exerciseinfo/")
    Call<EjercicioApiResponse> obtenerEjercicios(
            @Query("language") int language, // 2 = Inglés, 4 = Español
            @Query("limit") int limit
    );
}