package com.kelompoklima.cinemape;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText edtUsername, edtEmail, edtPassword;
    private Button btnRegister;
    private TextView tvToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtUsername = findViewById(R.id.edtRegUsername);
        edtEmail = findViewById(R.id.edtRegEmail);
        edtPassword = findViewById(R.id.edtRegPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvToLogin = findViewById(R.id.tvToLogin);

        btnRegister.setOnClickListener(v -> {
            String username = edtUsername.getText().toString();
            String email = edtEmail.getText().toString();
            String password = edtPassword.getText().toString();

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Semua data harus diisi", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(this, "Password minimal 6 karakter", Toast.LENGTH_SHORT).show();
                return;
            }

            User userBaru = new User();
            userBaru.setUsername(username);
            userBaru.setEmail(email);
            userBaru.setPassword(password);

            ApiService.register(userBaru, new ApiService.ApiCallback<User>() {
                @Override
                public void onSuccess(User result) {
                    Toast.makeText(RegisterActivity.this, "Pendaftaran berhasil, silakan login", Toast.LENGTH_SHORT).show();
                    finish(); // Kembali ke halaman Login
                }

                @Override
                public void onError(String errorMessage) {
                    Toast.makeText(RegisterActivity.this, "Gagal daftar: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        });

        tvToLogin.setOnClickListener(v -> {
            finish(); // Kembali ke LoginActivity
        });
    }
}
