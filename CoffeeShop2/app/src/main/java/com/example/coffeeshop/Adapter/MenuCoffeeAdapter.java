package com.example.coffeeshop.Adapter;



import android.app.Dialog;
import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.coffeeshop.BroadCast.ConnectionReceiver;

import com.example.coffeeshop.Interface.ItemClickListener;

import com.example.coffeeshop.Model.CartModel;
import com.example.coffeeshop.Model.DetailedDailyModel;
import com.example.coffeeshop.Interface.MyUpdateCartEvent;
import com.example.coffeeshop.Model.WareHouse;
import com.example.coffeeshop.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import org.greenrobot.eventbus.EventBus;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuCoffeeAdapter extends RecyclerView.Adapter<MenuCoffeeAdapter.ViewHolder> {
    Context context;
    ArrayList<DetailedDailyModel> list;

    public MenuCoffeeAdapter(Context context, ArrayList<DetailedDailyModel> dataList) {
        this.context = context;
        this.list = dataList;
    }
     @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_coffee, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DetailedDailyModel detailedDailyModel = list.get(position);
        Glide.with(context).load(detailedDailyModel.getImage()).into(holder.imageView);
        holder.Name.setText(detailedDailyModel.getName());
        holder.Description.setText(detailedDailyModel.getDescription());
        holder.Price.setText(detailedDailyModel.getPrice());
        holder.Rating.setText(detailedDailyModel.getRating());
        holder.Addcart.setOnClickListener(view -> {

            boolean ret = ConnectionReceiver.isConnected();

            if (!ret) {

                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.custom_dialog_connect);
                Button No = dialog.findViewById(R.id.Btn_close);
                No.setOnClickListener(view1 -> {
                    dialog.dismiss();
                });
                dialog.show();
            }

            LocalTime localTime = LocalTime.now();

            if (localTime.getHour() <= 7 || localTime.getHour() >22)
            {
                Toast.makeText(context.getApplicationContext(), R.string.Shop_Close, Toast.LENGTH_SHORT).show();
                return;
            }
            List<WareHouse> wareHouseList = new ArrayList<>();

            DatabaseReference WareHouse = FirebaseDatabase
                    .getInstance()
                    .getReference("WareHouse")
                    .child("UNIQUE_USER_ID");

            WareHouse.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {

                        for (DataSnapshot wareSnapshot : snapshot.getChildren()) {

                            WareHouse wareHouse1 = wareSnapshot.getValue(WareHouse.class);
                            wareHouseList.add(wareHouse1);
                        }
                            if(holder.Description.getText().equals("arabica seeds") && (wareHouseList.get(0).getDepot()<20))
                            {
                                Toast.makeText(context.getApplicationContext(),"Kho không đủ nguyên liêu",Toast.LENGTH_SHORT).show();
                            }
                            else if(holder.Description.getText().equals("Robusta seeds") &&(wareHouseList.get(1).getDepot()<20))
                            {
                                Toast.makeText(context.getApplicationContext(), "Kho không đủ nguyên liệu", Toast.LENGTH_SHORT).show();
                            }
                            else
                                AddToCart(detailedDailyModel);
                        }
                    else {
                        Toast.makeText(context.getApplicationContext(), "Kho trống",Toast.LENGTH_SHORT).show();
                    }
                }

                    @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(context.getApplicationContext(), "Failed to read data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void AddToCart(DetailedDailyModel detailedDailyModel) {
        DatabaseReference userCart = FirebaseDatabase
                .getInstance()
                .getReference("Cart")
                .child("UNIQUE_USER_ID");
        userCart.child(detailedDailyModel.getKey())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            CartModel cartModel = snapshot.getValue(CartModel.class);
                            Map<String, Object> updateData = new HashMap<>();
                            updateData.put("quantity", cartModel.getQuantity() + 1);
                            updateData.put("totalPrice", cartModel.getQuantity() * Float.parseFloat(cartModel.getPrice()));
                            userCart.child(detailedDailyModel.getKey())
                                    .updateChildren(updateData)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(context.getApplicationContext(), "Add To Cart Success", Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            CartModel cartModel = new CartModel();
                            cartModel.setName(detailedDailyModel.getName());
                            cartModel.setImage(detailedDailyModel.getImage());
                            cartModel.setQuantity(1);
                            cartModel.setKey(detailedDailyModel.getKey());
                            cartModel.setPrice(detailedDailyModel.getPrice());
                            cartModel.setTotalPrice(Float.parseFloat(detailedDailyModel.getPrice()));

                            userCart.child(detailedDailyModel.getKey())
                                    .setValue(cartModel)
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(context.getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                        EventBus.getDefault().postSticky(new MyUpdateCartEvent());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;
        TextView Name, Description, Rating, Price, Time;
        Button Addcart;
        ItemClickListener listener;

        public void setListener(ItemClickListener listener) {
            this.listener = listener;
        }

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.detailed_img);
            Name = itemView.findViewById(R.id.detailed_name);
            Description = itemView.findViewById(R.id.detailed_des);
            Rating = itemView.findViewById(R.id.detailed_rating);
            Price = itemView.findViewById(R.id.detailed_price);
            Time = itemView.findViewById(R.id.detailed_timing);
            Addcart = itemView.findViewById(R.id.detailed_add);
            Addcart.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onItemClick(view, getAdapterPosition());
        }
    }
}


