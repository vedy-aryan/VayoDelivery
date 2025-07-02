package com.vayonics.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vayonics.R;
import com.vayonics.model.FoodItem;

import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {

    private List<FoodItem> foodList;
    private Context context;

    public interface OnItemClickListener {
        void onItemClick(FoodItem item);
    }

    private final OnItemClickListener listener;

    public FoodAdapter(Context context, List<FoodItem> foodList, OnItemClickListener listener) {
        this.context = context;
        this.foodList = foodList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.food_item, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        FoodItem item = foodList.get(position);
        holder.name.setText(item.getName());
        holder.desc.setText(item.getDescription());
        holder.price.setText("₹ " + item.getPrice());

        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    static class FoodViewHolder extends RecyclerView.ViewHolder {
        TextView name, desc, price;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.foodName);
            desc = itemView.findViewById(R.id.foodDescription);
            price = itemView.findViewById(R.id.foodPrice);
        }
    }
}
