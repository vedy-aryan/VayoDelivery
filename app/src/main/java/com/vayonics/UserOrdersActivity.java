package com.vayonics;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import com.vayonics.adapter.OrderTrackAdapter;
import com.vayonics.model.TrackableOrder;

import java.util.ArrayList;
import java.util.List;

public class UserOrdersActivity extends AppCompatActivity {

    RecyclerView userOrdersRecycler;
    FirebaseAuth auth;
    DatabaseReference ordersRef;
    List<TrackableOrder> trackableOrders = new ArrayList<>();
    OrderTrackAdapter adapter;

    private final android.os.Handler handler = new android.os.Handler();
    private final Runnable refresher = new Runnable() {
        @Override
        public void run() {
            loadUserOrders();
            handler.postDelayed(this, 10000); // 10 seconds
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        handler.post(refresher);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_orders);

        userOrdersRecycler = findViewById(R.id.userOrdersRecycler);
        userOrdersRecycler.setLayoutManager(new LinearLayoutManager(this));

        auth = FirebaseAuth.getInstance();
        ordersRef = FirebaseDatabase.getInstance("https://vayodelivery-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("orders");

        adapter = new OrderTrackAdapter(this, trackableOrders);
        userOrdersRecycler.setAdapter(adapter);

        loadUserOrders();
    }

    private void loadUserOrders() {
        String uid = auth.getCurrentUser().getUid();

        ordersRef.orderByChild("userId").equalTo(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        trackableOrders.clear();
                        for (DataSnapshot child : snapshot.getChildren()) {
                            String orderId = child.getKey();
                            String foodItem = child.child("foodItem").getValue(String.class);
                            String status = child.child("status").getValue(String.class);

                            if (status != null && status.startsWith("drone_")) {
                                Double lat = child.child("lat").getValue(Double.class);
                                Double lng = child.child("lng").getValue(Double.class);

                                TrackableOrder order = new TrackableOrder(orderId, foodItem, status);
                                order.setLat(lat != null ? lat : 0);
                                order.setLng(lng != null ? lng : 0);
                                trackableOrders.add(order);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle error
                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacks(refresher);
    }

}
