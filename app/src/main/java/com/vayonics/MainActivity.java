package com.vayonics;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {


    FirebaseAuth auth;
    Button logoutButton;
    TextView welcomeText;
    Button placeOrderButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();

        logoutButton = findViewById(R.id.logoutButton);
        welcomeText = findViewById(R.id.welcomeText);

        placeOrderButton = findViewById(R.id.placeOrderButton);

        logoutButton.setOnClickListener(v -> {
            auth.signOut(); // Logs out user
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // clear back stack
            startActivity(intent);
        });

        placeOrderButton.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, OrderActivity.class));
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }
}
