package com.vayonics;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.vayonics.adapter.FoodAdapter;
import com.vayonics.model.FoodItem;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderActivity extends AppCompatActivity {

    RecyclerView foodRecyclerView;
    List<FoodItem> foodItems;
    FusedLocationProviderClient locationProvider;
    DatabaseReference ordersRef;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        foodRecyclerView = findViewById(R.id.foodRecyclerView);
        locationProvider = LocationServices.getFusedLocationProviderClient(this);
        ordersRef = FirebaseDatabase.getInstance("https://vayodelivery-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("orders");
        auth = FirebaseAuth.getInstance();

        foodItems = Arrays.asList(
                new FoodItem("Veg Burger", "Fresh lettuce with cheese", 120),
                new FoodItem("Cheese Pizza", "Loaded with mozzarella", 220),
                new FoodItem("Samosa", "Spicy potato filled", 20),
                new FoodItem("Paneer Roll", "Soft roti with masala paneer", 80)
        );

        FoodAdapter adapter = new FoodAdapter(this, foodItems, this::placeOrder);
        foodRecyclerView.setAdapter(adapter);
    }

    private void placeOrder(FoodItem item) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            return;
        }

        locationProvider.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                assert auth.getCurrentUser() != null;
                String userId = auth.getCurrentUser().getUid();
                String orderId = ordersRef.push().getKey();

                Map<String, Object> orderData = new HashMap<>();
                orderData.put("userId", userId);
                orderData.put("foodItem", item.getName());
                orderData.put("price", item.getPrice());
                orderData.put("lat", location.getLatitude());
                orderData.put("lng", location.getLongitude());
                orderData.put("timestamp", ServerValue.TIMESTAMP);
                orderData.put("status", "waiting_for_drone");

                assert orderId != null;
                ordersRef.child(orderId).setValue(orderData)
                        .addOnSuccessListener(aVoid -> Toast.makeText(this, "Order Placed!", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Location not available", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
