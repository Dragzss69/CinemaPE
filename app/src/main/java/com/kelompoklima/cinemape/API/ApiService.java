package com.kelompoklima.cinemape.API;

import com.github.kittinunf.fuel.Fuel;
import com.github.kittinunf.fuel.core.FuelError;
import com.github.kittinunf.fuel.core.Handler;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kelompoklima.cinemape.Model.Movie;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ApiService adalah kelas utilitas untuk menangani semua request jaringan (HTTP) ke MockAPI.
 * Menggunakan library Fuel untuk koneksi dan GSON untuk parsing data JSON.
 */
public class ApiService {
    // Base URL dari MockAPI
    private static final String BASE_URL = "https://68ff8dfbe02b16d1753e765d.mockapi.io/";
    // Nama endpoint untuk sumber daya film
    private static final String RESOURCE = "film";
    private static final Gson gson = new Gson();

    /**
     * Interface callback untuk mengirimkan hasil request (sukses/gagal) kembali ke pemanggil (Fragment/Activity).
     */
    public interface ApiCallback<T> {
        void onSuccess(T result);
        void onError(String errorMessage);
    }

    // --- Bagian Movie (CRUD - Create, Read, Update, Delete) ---

    /**
     * Mengambil semua daftar film dari server.
     */
    public static void getAllMovie(ApiCallback<List<Movie>> callback) {
        String endpoint = BASE_URL + RESOURCE;

        // Melakukan request GET
        Fuel.INSTANCE.get(endpoint, null).responseString(new Handler<String>() {
            @Override
            public void success(String response) {
                try {
                    // Parsing JSON array menjadi List objek Movie
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

    /**
     * Mengirim data film baru ke server.
     */
    public static void createMovie(Movie movieBaru, ApiCallback<Movie> callback) {
        String jsonBody = gson.toJson(movieBaru);

        // Melakukan request POST dengan body JSON
        Fuel.INSTANCE.post(BASE_URL + RESOURCE, null)
                .body(jsonBody, StandardCharsets.UTF_8)
                .header(Map.of("Content-Type", "application/json"))
                .responseString(new Handler<String>() {
                    @Override
                    public void success(String response) {
                        // Parsing respons sukses menjadi satu objek Movie
                        Movie result = gson.fromJson(response, Movie.class);
                        callback.onSuccess(result);
                    }
                    @Override
                    public void failure(FuelError error) {
                        callback.onError(error.getMessage());
                    }
                });
    }

    /**
     * Memperbarui data film yang sudah ada berdasarkan ID.
     */
    public static void updateMovie(String id, Movie movieUpdated, ApiCallback<Movie> callback) {
        String jsonBody = gson.toJson(movieUpdated);

        // Melakukan request PUT ke endpoint spesifik ID
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

    /**
     * Menghapus data film dari server berdasarkan ID.
     */
    public static void deleteMovie(String id, ApiCallback<Void> callback) {
        // Melakukan request DELETE
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
