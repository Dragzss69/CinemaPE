package com.kelompoklima.cinemape;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;

/**
 * LoginActivity menangani proses masuk pengguna ke aplikasi.
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

        // Inisialisasi SessionManager untuk mengecek status login
        sessionManager = new SessionManager(this);

        // Jika user sudah login sebelumnya, langsung arahkan ke halaman utama (MainActivity)
        if (sessionManager.isLoggedIn()) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish(); // Tutup LoginActivity agar tidak bisa di-back
        }

        // Menghubungkan variabel dengan elemen UI yang ada di layout XML
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvToRegister = findViewById(R.id.tvToRegister);

        // Aksi ketika tombol Login diklik
        btnLogin.setOnClickListener(v -> {
            String username = edtUsername.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            // Validasi: memastikan input tidak kosong
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Username dan password harus diisi", Toast.LENGTH_SHORT).show();
                return;
            }

            // Memeriksa apakah username dan password cocok dengan data yang tersimpan di HP
            if (sessionManager.checkLogin(username, password)) {
                // Jika sukses: simpan status login ke dalam sesi
                sessionManager.createLoginSession(username);
                Toast.makeText(this, "Login Berhasil!", Toast.LENGTH_SHORT).show();
                
                // Pindah ke halaman utama (MainActivity)
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish(); // Agar user tidak bisa kembali ke halaman login dengan tombol back
            } else {
                // Jika gagal: beritahu user bahwa data salah
                Toast.makeText(this, "Username atau password salah", Toast.LENGTH_SHORT).show();
            }
        });

        // Berpindah ke halaman pendaftaran (RegisterActivity)
        tvToRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }
}
