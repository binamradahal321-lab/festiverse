package com.festiverse.app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.festiverse.app.util.Prefs;

public class SignupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Prefs.applySavedTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        EditText name = findViewById(R.id.signupName);
        EditText email = findViewById(R.id.signupEmail);
        EditText password = findViewById(R.id.signupPassword);
        EditText confirm = findViewById(R.id.signupConfirm);
        Button signupBtn = findViewById(R.id.signupButton);
        TextView goLogin = findViewById(R.id.goLogin);

        signupBtn.setOnClickListener(v -> {
            String n = name.getText().toString().trim();
            String e = email.getText().toString().trim();
            String p = password.getText().toString();
            String c = confirm.getText().toString();

            if (TextUtils.isEmpty(n) || TextUtils.isEmpty(e)
                    || TextUtils.isEmpty(p) || TextUtils.isEmpty(c)) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(e).matches()) {
                Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                return;
            }
            if (p.length() < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!p.equals(c)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            if (com.festiverse.app.data.SupabaseClient.isConfigured()) {
                signupBtn.setEnabled(false);
                signupBtn.setText("Please wait\u2026");
                com.festiverse.app.data.SupabaseClient.signUp(n, e, p, (ok, result) -> {
                    signupBtn.setEnabled(true);
                    signupBtn.setText(R.string.signup);
                    if (ok) {
                        Prefs.createAccount(this, n, e, p); // cache locally
                        Prefs.setLoggedIn(this, true);
                        Toast.makeText(this, "Account created — welcome, " + n + "!",
                                Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(this, MainActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        finish();
                    } else {
                        Toast.makeText(this, result, Toast.LENGTH_LONG).show();
                    }
                });
                return;
            }

            // Offline demo fallback (Supabase not configured yet)
            Prefs.createAccount(this, n, e, p);
            Prefs.setLoggedIn(this, true);
            Toast.makeText(this, "Account created — welcome, " + n + "!", Toast.LENGTH_SHORT).show();

            Intent i = new Intent(this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        });

        goLogin.setOnClickListener(v -> finish());
    }
}
