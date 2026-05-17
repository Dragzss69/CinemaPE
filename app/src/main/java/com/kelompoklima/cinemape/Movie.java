package com.kelompoklima.cinemape;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Movie implements Serializable {

    @SerializedName("id")
    private String id;

    @SerializedName("judul")
    private String judul;

    @SerializedName("ringkasan")
    private String ringkasan;

    @SerializedName("gambar_poster")
    private String gambarPoster;

    @SerializedName("tanggal_rilis")
    private long tanggalRilis;

    @SerializedName("skor_rating")
    private String skorRating;

    @SerializedName("kategori")
    private String kategori;

    @SerializedName("userId")
    private String userId;

    @SerializedName("url_trailer")
    private String urlTrailer;

    public Movie() {
    }

    public Movie(String judul, String ringkasan) {
        this.judul = judul;
        this.ringkasan = ringkasan;
    }

    // --- Getter dan Setter ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getJudul() { return judul; }
    public void setJudul(String judul) { this.judul = judul; }
    public String getRingkasan() { return ringkasan; }
    public void setRingkasan(String ringkasan) { this.ringkasan = ringkasan; }
    public String getGambarPoster() { return gambarPoster; }
    public void setGambarPoster(String gambarPoster) { this.gambarPoster = gambarPoster; }
    public long getTanggalRilis() { return tanggalRilis; }
    public void setTanggalRilis(long tanggalRilis) { this.tanggalRilis = tanggalRilis; }
    public String getSkorRating() { return skorRating; }
    public void setSkorRating(String skorRating) { this.skorRating = skorRating; }
    public String getKategori() { return kategori; }
    public void setKategori(String kategori) { this.kategori = kategori; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getUrlTrailer() { return urlTrailer; }
    public void setUrlTrailer(String urlTrailer) { this.urlTrailer = urlTrailer; }
}