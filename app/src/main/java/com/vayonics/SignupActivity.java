package com.vayonics;

import static com.vayonics.utils.Validator.isStrongPassword;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class SignupActivity extends AppCompatActivity {

    EditText emailEditText, passwordEditText;
    Button signupButton;
    TextView goToLogin;
    FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        auth = FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        signupButton = findViewById(R.id.loginButton); // reuse same ID
        goToLogin = findViewById(R.id.goToLogin);

        signupButton.setText("Sign Up");  // This is to Set the text in the button to Signup and Not login

        signupButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailEditText.setError("Invalid email format");
                return;
            }

            if (!isStrongPassword(password)) {
                passwordEditText.setError("Password must be at least 8 characters and include a letter, digit & special character");
                return;
            }

            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(this, MainActivity.class));
                            finish();
                        } else {
                            Exception e = task.getException();
                            if (e instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(this, "User already exists. Please login instead.", Toast.LENGTH_SHORT).show();
                            } else if (e instanceof FirebaseAuthWeakPasswordException) {
                                Toast.makeText(this, "Weak password. Please choose a stronger one.", Toast.LENGTH_SHORT).show();
                            } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(this, "Invalid email format.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, "Signup failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                    });
        });

        goToLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}
