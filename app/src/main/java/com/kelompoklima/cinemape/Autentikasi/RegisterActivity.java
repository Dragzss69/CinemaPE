package com.kelompoklima.cinemape.Autentikasi;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.kelompoklima.cinemape.R;
import com.kelompoklima.cinemape.Session.SessionManager;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText edtUsername, edtPassword;
    private Button btnRegister;
    private TextView tvToLogin;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        sessionManager = new SessionManager(this);

        edtUsername = findViewById(R.id.edtRegUsername);
        edtPassword = findViewById(R.id.edtRegPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvToLogin = findViewById(R.id.tvToLogin);

        btnRegister.setOnClickListener(v -> {
            String username = edtUsername.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Semua data harus diisi", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(this, "Password minimal 6 karakter", Toast.LENGTH_SHORT).show();
                return;
            }

            sessionManager.registerUser(username, password);
            
            Toast.makeText(RegisterActivity.this, "Pendaftaran berhasil, silakan login", Toast.LENGTH_SHORT).show();

            finish(); 
        });

        tvToLogin.setOnClickListener(v -> {
            finish();
        });
    }
}
