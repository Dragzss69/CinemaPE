package com.kelompoklima.cinemape;

import com.github.kittinunf.fuel.Fuel;
import com.github.kittinunf.fuel.core.FuelError;
import com.github.kittinunf.fuel.core.Handler;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ApiService mengelola komunikasi dengan MockAPI untuk data film.
 * Fitur login, registrasi, dan simpan favorit ditangani secara lokal melalui SessionManager.
 */
public class ApiService {
    private static final String BASE_URL = "https://68ff8dfbe02b16d1753e765d.mockapi.io/";
    private static final String RESOURCE = "film";
    private static final Gson gson = new Gson();

    public interface ApiCallback<T> {
        void onSuccess(T result);
        void onError(String errorMessage);
    }

    // --- Bagian Movie (CRUD) ---

    public static void getAllMovie(ApiCallback<List<Movie>> callback) {
        String endpoint = BASE_URL + RESOURCE;

        Fuel.INSTANCE.get(endpoint, null).responseString(new Handler<String>() {
            @Override
            public void success(String response) {
                try {
                    List<Movie> list = gson.fromJson(response,
                            new TypeToken<ArrayList<Movie>>(){}.getType());
                    callback.onSuccess(list);
                } catch (Exception e) {
                    callback.onError("Gagal memproses data: " + e.getMessage());
                }
            }

            @Override
            public void failure(FuelError error) {
                callback.onError("Koneksi gagal: " + error.getMessage());
            }
        });
    }

    public static void createMovie(Movie movieBaru, ApiCallback<Movie> callback) {
        String jsonBody = gson.toJson(movieBaru);

        Fuel.INSTANCE.post(BASE_URL + RESOURCE, null)
                .body(jsonBody, StandardCharsets.UTF_8)
                .header(Map.of("Content-Type", "application/json"))
                .responseString(new Handler<String>() {
                    @Override
                    public void success(String response) {
                        Movie result = gson.fromJson(response, Movie.class);
                        callback.onSuccess(result);
                    }
                    @Override
                    public void failure(FuelError error) {
                        callback.onError(error.getMessage());
                    }
                });
    }

    public static void updateMovie(String id, Movie movieUpdated, ApiCallback<Movie> callback) {
        String jsonBody = gson.toJson(movieUpdated);

        Fuel.INSTANCE.put(BASE_URL + RESOURCE + "/" + id, null)
                .body(jsonBody, StandardCharsets.UTF_8)
                .header(Map.of("Content-Type", "application/json"))
                .responseString(new Handler<String>() {
                    @Override
                    public void success(String response) {
                        Movie result = gson.fromJson(response, Movie.class);
                        callback.onSuccess(result);
                    }
                    @Override
                    public void failure(FuelError error) {
                        callback.onError(error.getMessage());
                    }
                });
    }

    public static void deleteMovie(String id, ApiCallback<Void> callback) {
        Fuel.INSTANCE.delete(BASE_URL + RESOURCE + "/" + id, null)
                .responseString(new Handler<String>() {
                    @Override
                    public void success(String response) {
                        callback.onSuccess(null);
                    }
                    @Override
                    public void failure(FuelError error) {
                        callback.onError(error.getMessage());
                    }
                });
    }
}
