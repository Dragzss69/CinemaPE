package com.kelompoklima.cinemape;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;

/**
 * LoginActivity mengelola proses autentikasi masuk pengguna.
 * Mengecek kecocokan input dengan data yang tersimpan di SharedPreferences melalui SessionManager.
 */
public class LoginActivity extends AppCompatActivity {

    private TextInputEditText edtUsername, edtPassword;
    private Button btnLogin;
    private TextView tvToRegister;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inisialisasi SessionManager untuk mengelola status login
        sessionManager = new SessionManager(this);

        // Cek Sesi: Jika user sudah login, langsung arahkan ke MainActivity
        if (sessionManager.isLoggedIn()) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish(); // Tutup LoginActivity
        }

        // Inisialisasi komponen UI dari layout XML
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvToRegister = findViewById(R.id.tvToRegister);

        // Logika saat tombol Login diklik
        btnLogin.setOnClickListener(v -> {
            String username = edtUsername.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            // Validasi: Pastikan input tidak kosong
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Username dan password harus diisi", Toast.LENGTH_SHORT).show();
                return;
            }

            // Verifikasi Login melalui SessionManager
            if (sessionManager.checkLogin(username, password)) {
                // Jika Berhasil: Buat sesi login dan simpan username
                sessionManager.createLoginSession(username);
                Toast.makeText(this, "Login Berhasil!", Toast.LENGTH_SHORT).show();
                
                // Pindah ke halaman utama
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            } else {
                // Jika Gagal: Tampilkan pesan error
                Toast.makeText(this, "Username atau password salah", Toast.LENGTH_SHORT).show();
            }
        });

        // Navigasi ke halaman pendaftaran (RegisterActivity)
        tvToRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }
}
