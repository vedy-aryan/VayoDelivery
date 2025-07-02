package com.vayonics.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vayonics.TrackingActivity;
import com.vayonics.model.TrackableOrder;
import com.vayonics.R;

import java.util.List;

public class OrderTrackAdapter extends RecyclerView.Adapter<OrderTrackAdapter.OrderViewHolder> {

    private Context context;
    private List<TrackableOrder> orders;

    public OrderTrackAdapter(Context context, List<TrackableOrder> orders) {
        this.context = context;
        this.orders = orders;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_track_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        TrackableOrder order = orders.get(position);
        holder.orderIdText.setText("Order ID: " + order.getOrderId());
        holder.foodItemText.setText("Food: " + order.getFoodItem());

        // MANUAL DRONE SPEED AND DISTANCE ESTIMATION
        double droneSpeed = 8.3; // m/s (30 km/h)

        float[] result = new float[1];
        android.location.Location.distanceBetween(
                order.getLat(), order.getLng(),
                22.7452, 86.2573,  // Replace with dummy drone location or fetch dynamically
                result
        );

        float distance = result[0]; // in meters
        int eta = (int) (distance / droneSpeed); // seconds

        holder.extraInfo.setText("Distance: " + Math.round(distance) + " m\nETA: " + eta + " sec");


        holder.trackButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, TrackingActivity.class);
            intent.putExtra("droneId", order.getDroneId());
            intent.putExtra("userLat", order.getLat());
            intent.putExtra("userLng", order.getLng());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderIdText, foodItemText,extraInfo;
        Button trackButton;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderIdText = itemView.findViewById(R.id.orderIdText);
            foodItemText = itemView.findViewById(R.id.foodItemText);
            trackButton = itemView.findViewById(R.id.trackButton);
            extraInfo = itemView.findViewById(R.id.extraInfo);
        }
    }
}
