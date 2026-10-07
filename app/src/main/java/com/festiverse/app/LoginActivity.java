package com.festiverse.app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.festiverse.app.util.Prefs;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Prefs.applySavedTheme(this);
        super.onCreate(savedInstanceState);

        if (Prefs.isLoggedIn(this)) {
            goToMain();
            return;
        }

        setContentView(R.layout.activity_login);

        EditText email = findViewById(R.id.loginEmail);
        EditText password = findViewById(R.id.loginPassword);
        Button loginBtn = findViewById(R.id.loginButton);
        TextView goSignup = findViewById(R.id.goSignup);

        loginBtn.setOnClickListener(v -> {
            String e = email.getText().toString().trim();
            String p = password.getText().toString();

            if (TextUtils.isEmpty(e) || TextUtils.isEmpty(p)) {
                Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (com.festiverse.app.data.SupabaseClient.isConfigured()) {
                // Real account check against Supabase Auth
                loginBtn.setEnabled(false);
                loginBtn.setText("Please wait\u2026");
                com.festiverse.app.data.SupabaseClient.signIn(e, p, (ok, result) -> {
                    loginBtn.setEnabled(true);
                    loginBtn.setText(R.string.login);
                    if (ok) {
                        Prefs.createAccount(this, result, e, p); // cache locally
                        Prefs.setLoggedIn(this, true);
                        Toast.makeText(this, "Welcome back, " + result + "!",
                                Toast.LENGTH_SHORT).show();
                        goToMain();
                    } else {
                        Toast.makeText(this, result, Toast.LENGTH_LONG).show();
                    }
                });
                return;
            }

            // Offline demo fallback (Supabase not configured yet)
            if (!Prefs.accountExists(this)) {
                Toast.makeText(this, "No account found — please sign up first", Toast.LENGTH_LONG).show();
                return;
            }
            if (Prefs.checkLogin(this, e, p)) {
                Prefs.setLoggedIn(this, true);
                Toast.makeText(this, "Welcome back, " + Prefs.getUserName(this) + "!", Toast.LENGTH_SHORT).show();
                goToMain();
            } else {
                Toast.makeText(this, "Incorrect email or password", Toast.LENGTH_SHORT).show();
            }
        });

        goSignup.setOnClickListener(v ->
                startActivity(new Intent(this, SignupActivity.class)));
    }

    private void goToMain() {
        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }
}
