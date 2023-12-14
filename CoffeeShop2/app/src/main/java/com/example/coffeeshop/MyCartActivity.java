package com.example.coffeeshop;

import static android.content.ContentValues.TAG;

import static java.lang.Float.parseFloat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.example.coffeeshop.Adapter.MyCartAdapter;
import com.example.coffeeshop.Interface.ICartLoadListener;
import com.example.coffeeshop.Interface.MyUpdateCartEvent;
import com.example.coffeeshop.Model.CartModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.time.LocalTime;


public class MyCartActivity extends AppCompatActivity implements ICartLoadListener {
    ConstraintLayout constraintLayout;
    RecyclerView MyCart;
    ICartLoadListener cartLoadListener;
    Button Btn_Order;
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
        loadCartFromFirebase();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_cart);
        init();
        loadCartFromFirebase();
        MakeOrder();
    }private void MakeOrder() {
        Btn_Order = findViewById(R.id.Btn_order);
        Btn_Order.setOnClickListener(view -> {
            finish();
        });
    }private void loadCartFromFirebase() {
        List<CartModel> cartModels = new ArrayList<>();
        FirebaseDatabase.getInstance()
                .getReference("Cart")
                .child("UNIQUE_USER_ID")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot cartSnapshot : snapshot.getChildren()) {
                                CartModel cartModel = cartSnapshot.getValue(CartModel.class);
                                cartModel.setKey(cartSnapshot.getKey());
                                cartModels.add(cartModel);
                            }
                            cartLoadListener.onCartLoadSuccess(cartModels);
                        } else
                            cartLoadListener.onCartLoadFailed("Cart empty");
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        cartLoadListener.onCartLoadFailed(error.getMessage());
                    }
                });
    }
    private void init() {
        constraintLayout = findViewById(R.id.mainLayout);
        MyCart = findViewById(R.id.cart_rec);
        cartLoadListener = this;
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        MyCart.setLayoutManager(layoutManager);
        MyCart.addItemDecoration(new DividerItemDecoration(this, layoutManager.getOrientation()));
    }
    @Override
    public void onCartLoadSuccess(List<CartModel> cartModelsList) {
        double sum = 0;
        int quantity =0;
        TextView Total = findViewById(R.id.TotalTxt);
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        for (CartModel cartModel : cartModelsList)
        {
            sum += cartModel.getTotalPrice();
            quantity +=cartModel.getQuantity();
        }
        if(sum>150)
            sum = sum-(sum *0.2);

        if(quantity>10)
        {
            CartModel cartModel1 = new CartModel("8","Bánh Chocolate","https://cdn.tgdd.vn/Files/2021/08/08/1373908/tiramisu-la-gi-y-nghia-cua-banh-tiramisu-202108082258460504.jpg","0",1,0);
            cartModelsList.add(cartModel1);
        }
        if(dayOfWeek ==2)
        {
            sum=sum-(sum*0.1);
        }

        Total.setText(new StringBuilder("$").append(sum));
        MyCartAdapter adapter = new MyCartAdapter(this, cartModelsList);
        MyCart.setAdapter(adapter);
    }
    @Override
    public void onCartLoadFailed(String message) {
        Snackbar.make(constraintLayout, message, Snackbar.LENGTH_SHORT).show();
    }
}