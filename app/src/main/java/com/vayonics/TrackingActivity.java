package com.vayonics;

import android.animation.ValueAnimator;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.firebase.database.*;

public class TrackingActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker droneMarker;
    private Polyline routeLine;
    private LatLng lastDroneLatLng = null;

    private String droneId;
    private LatLng userLatLng;
    private boolean hasNotified = false;

    private TextView distanceTextView, etaTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);

        droneId = getIntent().getStringExtra("droneId");
        double userLat = getIntent().getDoubleExtra("userLat", 0);
        double userLng = getIntent().getDoubleExtra("userLng", 0);
        userLatLng = new LatLng(userLat, userLng);

        distanceTextView = findViewById(R.id.distanceTextView);
        etaTextView = findViewById(R.id.etaTextView);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        createNotificationChannel();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        mMap.addMarker(new MarkerOptions()
                .position(userLatLng)
                .title("You")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

        // 👇 FIX: Use full DB URL
        DatabaseReference droneRef = FirebaseDatabase.getInstance(
                        "https://vayodelivery-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("drones").child(droneId);

        // 👂 Real-time listener
        droneRef.addValueEventListener(new ValueEventListener() {
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Double lat = snapshot.child("currentlatitude").getValue(Double.class);
                Double lng = snapshot.child("currentlongitude").getValue(Double.class);

                if (lat != null && lng != null) {
                    LatLng droneLatLng = new LatLng(lat, lng);

                    updateDroneMarker(droneLatLng);
                    drawRouteToUser(droneLatLng);
                    zoomToFitBoth(droneLatLng, userLatLng);

                    float distance = distanceBetween(userLatLng, droneLatLng);
                    int speed = 6;
                    int eta = (int) (distance / speed);

                    distanceTextView.setText("Distance: " + Math.round(distance) + " m");
                    etaTextView.setText("ETA: " + eta + " sec");

                    if (distance < 30 && !hasNotified) {
                        hasNotified = true;
                        showNotification("Drone has arrived!", "Your food is here 🍔");
                    }

                    Log.d("DronePosition", "Lat: " + lat + ", Lng: " + lng);
                }
            }

            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TrackingActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateDroneMarker(LatLng newPos) {
        Bitmap droneBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_drone);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(droneBitmap, 96, 96, false);
        BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(scaledBitmap);

        if (droneMarker == null) {
            droneMarker = mMap.addMarker(new MarkerOptions()
                    .position(newPos)
                    .title("Drone")
                    .icon(icon));
        } else {
            animateMarkerMovement(droneMarker, lastDroneLatLng, newPos);
        }

        lastDroneLatLng = newPos;
    }

    private void animateMarkerMovement(Marker marker, LatLng start, LatLng end) {
        if (start == null || marker == null) {
            marker.setPosition(end);
            return;
        }

        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.setDuration(1000);
        animator.addUpdateListener(valueAnimator -> {
            float v = (float) valueAnimator.getAnimatedValue();
            double lat = start.latitude * (1 - v) + end.latitude * v;
            double lng = start.longitude * (1 - v) + end.longitude * v;
            marker.setPosition(new LatLng(lat, lng));
        });
        animator.start();
    }

    private void drawRouteToUser(LatLng from) {
        if (routeLine != null) routeLine.remove();
        routeLine = mMap.addPolyline(new PolylineOptions()
                .add(from, userLatLng)
                .width(6)
                .color(Color.BLUE)
                .geodesic(true));
    }

    private void zoomToFitBoth(LatLng drone, LatLng user) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(drone);
        builder.include(user);
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 120));
    }

    private float distanceBetween(LatLng a, LatLng b) {
        float[] result = new float[1];
        android.location.Location.distanceBetween(
                a.latitude, a.longitude,
                b.latitude, b.longitude,
                result);
        return result[0];
    }

    private void showNotification(String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "drone_channel")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1, builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("drone_channel",
                    "Drone Notifications", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
}
