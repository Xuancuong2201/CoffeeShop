package com.example.coffeeshop.Interface;

import com.example.coffeeshop.Model.CartModel;

import java.util.List;

public interface ICartLoadListener {
    void onCartLoadSuccess(List<CartModel> cartModelsList);
    void onCartLoadFailed(String message);
}
