package com.example.coffeeshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.coffeeshop.Adapter.MenuCoffeeAdapter;
import com.example.coffeeshop.Interface.ICartLoadListener;
import com.example.coffeeshop.Interface.MyUpdateCartEvent;
import com.example.coffeeshop.Model.CartModel;
import com.example.coffeeshop.Model.DetailedDailyModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nex3z.notificationbadge.NotificationBadge;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class MenuCoffeeActivity extends AppCompatActivity implements ICartLoadListener {

    RecyclerView recyclerView;
    ArrayList<DetailedDailyModel> dataList;
    DatabaseReference database;
    ValueEventListener eventListener;
    FloatingActionButton MyCart;
    ICartLoadListener cartLoadListener;
    NotificationBadge badge;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_coffee);
        AnhXa();
        countCartItem();
        loadDinkFromFirebase();
    }

    private void loadDinkFromFirebase() {

        database = FirebaseDatabase.getInstance().getReference().child("Category");

        dataList = new ArrayList<>();

        recyclerView = findViewById(R.id.detailed_rec);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        MenuCoffeeAdapter adapter = new MenuCoffeeAdapter(this, dataList);
        recyclerView.setAdapter(adapter);

        database.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    DetailedDailyModel detailedDailyModel = dataSnapshot.getValue(DetailedDailyModel.class);
                    detailedDailyModel.setKey(dataSnapshot.getKey());
                    dataList.add(detailedDailyModel);
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),"Load Menu Failed",Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void AnhXa() {
        badge = findViewById(R.id.badge);
        cartLoadListener = this;
        MyCart = findViewById(R.id.MyCart);
        MyCart.setOnClickListener(view -> {
            Intent intent = new Intent(MenuCoffeeActivity.this, MyCartActivity.class);
            startActivity(intent);
        });
    }
    @Override
    public void onCartLoadSuccess(List<CartModel> cartModelsList) {
        int cartSum = 0;
        for (CartModel cartModel : cartModelsList)
            cartSum += cartModel.getQuantity();
        badge.setNumber(cartSum);
    }
    @Override
    public void onCartLoadFailed(String message) {

    }
        @Override
        protected void onStart() {
            super.onStart();
            EventBus.getDefault().register(this);
        }
        @Override
        protected void onStop() {
            super.onStop();
            if (EventBus.getDefault().hasSubscriberForEvent(MyUpdateCartEvent.class))
                EventBus.getDefault().removeStickyEvent(MyUpdateCartEvent.class);
            EventBus.getDefault().unregister(this);
        }
        @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
        public void onUpdateCart(MyUpdateCartEvent event) {
            countCartItem();
        }

    @Override
    protected void onResume() {
        super.onResume();
        countCartItem();
    }
    private void countCartItem() {
        List<CartModel> cartModels = new ArrayList<>();
        FirebaseDatabase
                .getInstance().getReference("Cart")
                .child("UNIQUE_USER_ID")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot carSnapshot : snapshot.getChildren()) {
                            CartModel cartModel = carSnapshot.getValue(CartModel.class);
                            cartModel.setKey(carSnapshot.getKey());
                            cartModels.add(cartModel);
                        }
                        cartLoadListener.onCartLoadSuccess(cartModels);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        cartLoadListener.onCartLoadFailed(error.getMessage());
                    }
                });
    }
}