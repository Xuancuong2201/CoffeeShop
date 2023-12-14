package com.example.coffeeshop.Adapter;





import static android.content.Context.INPUT_METHOD_SERVICE;

import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.example.coffeeshop.BroadCast.ConnectionReceiver;
import com.example.coffeeshop.Interface.MyUpdateCartEvent;
import com.example.coffeeshop.Model.CartModel;
import com.example.coffeeshop.Model.DetailedDailyModel;
import com.example.coffeeshop.R;
import com.google.firebase.database.FirebaseDatabase;

import org.greenrobot.eventbus.EventBus;

import java.util.List;



public class MyCartAdapter extends RecyclerView.Adapter<MyCartAdapter.MyCartViewHolder> {
    Context context;
    private List<CartModel> cartModelList;

    public MyCartAdapter(Context context, List<CartModel> cartModelList) {
        this.context = context;
        this.cartModelList = cartModelList;
    }

    @NonNull
    @Override
    public MyCartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_cart_item,parent,false);
        return new MyCartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyCartViewHolder holder, int position) {
        Glide.with(context)
                .load(cartModelList.get(position).getImage())
                .into(holder.Img_name);
        holder.TxtPrice.setText(new StringBuilder().append(cartModelList.get(position).getPrice()));
        holder.TxtName.setText(new StringBuilder().append(cartModelList.get(position).getName()));
        holder.TxtQuantity.setText(new StringBuilder().append(cartModelList.get(position).getQuantity()));
    //Event
        holder.BtnMinus.setOnClickListener(view -> {
            minusCartItem(holder,cartModelList.get(position));
        });
        holder.BtnPlus.setOnClickListener(view -> {
            plusCartItem(holder,cartModelList.get(position));
        });
        holder.BtnDelete.setOnClickListener(view -> {
            AlertDialog dialog = new AlertDialog.Builder(context)
                    .setTitle("Delete item")
                    .setMessage("Do you really want to delete item")
                    .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                    .setPositiveButton("OK", (dialogInterface, i) -> {
                        notifyItemRemoved(position);
                        deleteFromFirebase(cartModelList.get(position));
                        dialogInterface.dismiss();
                    }).create();
            dialog.show();
        });
        holder.TxtQuantity.setOnEditorActionListener((textView, i, keyEvent) -> {
            boolean ret = ConnectionReceiver.isConnected();
            if (!ret) {
                Toast.makeText(context, "Kiểm tra kết nối mạng", Toast.LENGTH_SHORT).show();
                return true;
            }
            if (holder.TxtQuantity.getText().toString().contains(" ")) {
                Toast.makeText(context, "Số lượng không hợp lệ", Toast.LENGTH_SHORT).show();
                return true;
            }
            if (Integer.parseInt(holder.TxtQuantity.getText().toString())<1 ||Integer.parseInt(holder.TxtQuantity.getText().toString())>100) {
                Toast.makeText(context, "Số lượng không hợp lệ ", Toast.LENGTH_SHORT).show();
                return true;
            }
            if (i == EditorInfo.IME_ACTION_DONE) {
                updateQuantity(holder, cartModelList.get(position));
                InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(holder.TxtQuantity.getWindowToken(), 0);
                return true;
            }
            return false;
        });
    }

    private void updateQuantity(MyCartViewHolder holder, CartModel cartModel) {
        cartModel.setQuantity(Integer.parseInt(holder.TxtQuantity.getText().toString()));

        cartModel.setQuantity(cartModel.getQuantity());
        cartModel.setTotalPrice(cartModel.getQuantity()*Float.parseFloat(cartModel.getPrice()));
        FirebaseDatabase.getInstance()
                .getReference("Cart")
                .child("UNIQUE_USER_ID")
                .child(cartModel.getKey())
                .setValue(cartModel)
                .addOnSuccessListener(aVoid -> EventBus.getDefault().postSticky(new MyUpdateCartEvent()));

        //updateFirebase(cartModel);

    }
    private void deleteFromFirebase(CartModel cartModel) {
        FirebaseDatabase.getInstance()
                .getReference("Cart")
                .child("UNIQUE_USER_ID")
                .child(cartModel.getKey())
                .removeValue()
                .addOnSuccessListener(aVoid -> EventBus.getDefault().postSticky(new MyUpdateCartEvent()));
    }
    private void plusCartItem(MyCartViewHolder holder, CartModel cartModel) {
        cartModel.setQuantity(cartModel.getQuantity()+1);
        cartModel.setTotalPrice(cartModel.getQuantity()*Float.parseFloat(cartModel.getPrice()));
        holder.TxtQuantity.setText(new StringBuilder().append(cartModel.getQuantity()));
        updateFirebase(cartModel);

    }

    private void minusCartItem(MyCartViewHolder holder, CartModel cartModel) {
        if(cartModel.getQuantity()>1)
        {
            cartModel.setQuantity(cartModel.getQuantity()-1);
            cartModel.setTotalPrice(cartModel.getQuantity()*Float.parseFloat(cartModel.getPrice()));

            holder.TxtQuantity.setText(new StringBuilder().append(cartModel.getQuantity()));
            updateFirebase(cartModel);

        }
    }

    private void updateFirebase(CartModel cartModel) {
        FirebaseDatabase.getInstance()
                .getReference("Cart")
                .child("UNIQUE_USER_ID")
                .child(cartModel.getKey())
                .setValue(cartModel)
                .addOnSuccessListener(aVoid -> EventBus.getDefault().postSticky(new MyUpdateCartEvent()));
    }

    @Override
    public int getItemCount() {
        return cartModelList.size();
    }

    public static class MyCartViewHolder extends RecyclerView.ViewHolder {
        ImageView BtnMinus,BtnPlus,BtnDelete,Img_name;
        TextView TxtName,TxtPrice;
        EditText TxtQuantity;

        public MyCartViewHolder(@NonNull View itemView) {
             super(itemView);
             BtnMinus=itemView.findViewById(R.id.btnMinus);
             BtnPlus=itemView.findViewById(R.id.btnPlus);
             BtnDelete=itemView.findViewById(R.id.btnDelete);
             Img_name=itemView.findViewById(R.id.Img_name);
             TxtName=itemView.findViewById(R.id.TxtName);
             TxtPrice=itemView.findViewById(R.id.TxtPrice);
             TxtQuantity=itemView.findViewById(R.id.TxtQuantity);
        }
    }
}
