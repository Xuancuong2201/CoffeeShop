package com.example.coffeeshop.Model;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DetailedDailyModel {
    String image,name,description,rating,price,key;
    public DetailedDailyModel() {
    }

    public DetailedDailyModel(String Description, String Image, String name, String price, String rating, String timing) {
        this.description = Description;
        this.image = Image;
        this.name = name;
        this.price = price;
        this.rating = rating;
        this.key = timing;
    }

    public String getImage() {
        return image;
    }

    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public String getRating() {
        return rating;
    }
    public String getPrice() {
        return price;
    }
    public String getKey() {
        return key;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setKey(String timing) {
        this.key = timing;
    }
}
