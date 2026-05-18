package com.kelompoklima.cinemape;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;

/**
 * RegisterActivity menangani proses pendaftaran pengguna baru.
 * Data disimpan secara lokal di SharedPreferences melalui SessionManager.
 */
public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText edtUsername, edtPassword;
    private Button btnRegister;
    private TextView tvToLogin;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Inisialisasi SessionManager untuk menyimpan data pendaftaran
        sessionManager = new SessionManager(this);

        // Menghubungkan variabel dengan komponen UI di layout XML
        edtUsername = findViewById(R.id.edtRegUsername);
        edtPassword = findViewById(R.id.edtRegPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvToLogin = findViewById(R.id.tvToLogin);

        // Logika saat tombol Daftar diklik
        btnRegister.setOnClickListener(v -> {
            String username = edtUsername.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            // Validasi: Pastikan semua kolom diisi
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Semua data harus diisi", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validasi: Panjang password minimal 6 karakter demi keamanan
            if (password.length() < 6) {
                Toast.makeText(this, "Password minimal 6 karakter", Toast.LENGTH_SHORT).show();
                return;
            }

            // Simpan data pendaftaran ke SharedPreferences
            sessionManager.registerUser(username, password);
            
            Toast.makeText(RegisterActivity.this, "Pendaftaran berhasil, silakan login", Toast.LENGTH_SHORT).show();
            
            // Tutup halaman pendaftaran dan kembali ke halaman Login
            finish(); 
        });

        // Kembali ke halaman Login jika user sudah memiliki akun
        tvToLogin.setOnClickListener(v -> {
            finish();
        });
    }
}
