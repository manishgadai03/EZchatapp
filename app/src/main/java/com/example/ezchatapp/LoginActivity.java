package com.example.ezchatapp;



import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private boolean isPasswordVisible = false;
    private FirebaseAuth mAuth;
    private EditText loginEmail;
    private EditText loginPassword;
    private Button loginButton;
    private TextView create;
    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        mAuth = FirebaseAuth.getInstance();


        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {

            updateUI(currentUser);
        }


        loginEmail = findViewById(R.id.loginpemail);
        loginPassword = findViewById(R.id.loginpassword);
        loginButton = findViewById(R.id.Login);
        create=findViewById(R.id.create);
        final Drawable eyeClosedDrawable = ContextCompat.getDrawable(this, R.drawable.hidden);
        final Drawable eyeOpenDrawable = ContextCompat.getDrawable(this, R.drawable.show);



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

        loginButton.setOnClickListener(v -> {
            String email = loginEmail.getText().toString().trim();
            String password = loginPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please fill in both fields.", Toast.LENGTH_SHORT).show();
                return;
            }

            signInUser(email, password);
        });
        loginPassword.setCompoundDrawablesWithIntrinsicBounds(null, null, eyeOpenDrawable, null);


        loginPassword.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {

                if (event.getRawX() >= (loginPassword.getRight() - loginPassword.getCompoundDrawables()[2].getBounds().width())) {
                    togglePasswordVisibility(loginPassword, eyeClosedDrawable, eyeOpenDrawable);
                    return true;
                }
            }
            return false;
        });
    }
    private void togglePasswordVisibility(EditText passwordEditText, Drawable eyeClosedDrawable, Drawable eyeOpenDrawable) {
        if (isPasswordVisible) {

            loginPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            loginPassword.setCompoundDrawablesWithIntrinsicBounds(null, null, eyeClosedDrawable, null);
            isPasswordVisible = false;
        } else {

            loginPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            loginPassword.setCompoundDrawablesWithIntrinsicBounds(null, null, eyeOpenDrawable, null);
            isPasswordVisible = true;
        }

        passwordEditText.setSelection(passwordEditText.getText().length());
    }


    private void signInUser(String email, String password) {


        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {

                        Log.d(TAG, "signInWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    } else {

                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(LoginActivity.this, "Authentication failed: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }

                });
    }


    private void updateUI(FirebaseUser user) {
        if (user != null) {

            intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {

        }
    }
}
