package com.example.coffeeshop.Manage;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coffeeshop.Adapter.ManageAdapter;
import com.example.coffeeshop.Interface.ICartLoadListener;
import com.example.coffeeshop.Interface.MyUpdateCartEvent;
import com.example.coffeeshop.Model.CartModel;
import com.example.coffeeshop.Model.WareHouse;
import com.example.coffeeshop.R;
import com.example.coffeeshop.Start.LoginActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ManageFragment extends Fragment {
    RecyclerView Warehouse_Rec;
    View root;
    ImageView Btn_Add;

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (EventBus.getDefault().hasSubscriberForEvent(MyUpdateCartEvent.class))
            EventBus.getDefault().removeStickyEvent(MyUpdateCartEvent.class);
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onUpdateCart(MyUpdateCartEvent event) {
        loadwarehouseFromFirebase();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_manage, container, false);
        AnhXa();
        ClickAddWareHouse();
        loadwarehouseFromFirebase();
        return root;
    }

    @SuppressLint("MissingInflatedId")
    private void ClickAddWareHouse() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        final AlertDialog alertDialog = alert.create();
        EditText Name, Date, Depot, Type;
        TextView Id;
        View mView = getLayoutInflater().inflate(R.layout.dialog_item_warehouse, null);
        Id = mView.findViewById(R.id.ID_warehouse);
        Name = mView.findViewById(R.id.Name_warehouse);
        Date = mView.findViewById(R.id.Date_warehouse);
        Depot = mView.findViewById(R.id.Depot_warehouse);
        Type = mView.findViewById(R.id.Type_warehouse);
        Btn_Add.setOnClickListener(view -> {

            List<WareHouse> wareHouseList = new ArrayList<>();
            DatabaseReference userWareHouse = FirebaseDatabase
                    .getInstance()
                    .getReference("WareHouse")
                    .child("UNIQUE_USER_ID");

            userWareHouse.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ItemSnapshot : snapshot.getChildren()) {
                        WareHouse wareHouse = ItemSnapshot.getValue(WareHouse.class);
                        wareHouseList.add(wareHouse);
                    }

                    Id.setText(String.valueOf(wareHouseList.size() + 1));
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            mView.findViewById(R.id.Btn_close).setOnClickListener(view1 -> {

                alertDialog.dismiss();

            });

            mView.findViewById(R.id.Btn_Save).setOnClickListener(view1 -> {

                String pattern = getString(R.string.checkpassword);
                String pattern1 = ".*[a-zA-Z].*";
                String name = Name.getText().toString();
                String type = Type.getText().toString();
                String date = Date.getText().toString();
                int depot = Integer.parseInt(Depot.getText().toString());
                boolean KQ = false;
                int key=0;

                    if (name.isEmpty())
                    {
                        Toast.makeText(getContext(), "Tên mặt hàng không được bỏ trống", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (name.matches(pattern))
                    {
                        Toast.makeText(getContext(), "Tên mặt hàng không hợp lệ",    Toast.LENGTH_SHORT).show();
                        return;
                    }

                for(int i=0;i<wareHouseList.size();i++)
                {
                    if(name.equals(wareHouseList.get(i).getName())){

                        KQ=true;
                        key =i;
                    }
                }
                if (KQ)
                {
                    Toast.makeText(getContext(), "Mặt hàng đã tồn tại", Toast.LENGTH_SHORT).show();

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    int finalKey = key;

                    builder.setMessage("Bạn có muốn thêm số lượng vào mặt hàng đã tồn tại?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    int newDepot = wareHouseList.get(finalKey).getDepot()+Integer.parseInt(Depot.getText().toString());
                                    wareHouseList.get(finalKey).setDepot(newDepot);
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    dialog.cancel();
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }

                if (type.isEmpty()) {
                    Toast.makeText(getContext(), "Loại mặt hàng không được bỏ trống", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (type.matches(pattern)) {
                    Toast.makeText(getContext(), "Loại mặt hàng không hợp lệ", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(depot<1 || depot>10000){
                    Toast.makeText(getContext(), "Số lượng mặt hàng không hợp lệ", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(String.valueOf(type).matches(pattern1)){
                    Toast.makeText(getContext(), "Số lượng mặt hàng không hợp lệ", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(date.isEmpty()){
                    Toast.makeText(getContext(), "Hạn sử dụng không được để trống", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!isValidExpirationDate(date)){
                    Toast.makeText(getContext(), "Đã quá hạn sử dụng", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (isExpirationDateNear(date)) {
                    Toast.makeText(getContext(), "Hạn sử dụng quá ít", Toast.LENGTH_SHORT).show();
                }
                if (isValidExpirationDateYear(date)) {
                    Toast.makeText(getContext(), "Hạn sử dụng không hợp lệ", Toast.LENGTH_SHORT).show();
                }
                else {
                    WareHouse wareHouse = new WareHouse();
                    wareHouse.setID(Integer.parseInt(Id.getText().toString()));
                    wareHouse.setDate(Date.getText().toString());
                    wareHouse.setDepot(Integer.parseInt(Depot.getText().toString()));
                    wareHouse.setName(Name.getText().toString());
                    wareHouse.setType(Type.getText().toString());
                    wareHouse.setKey(Id.getText().toString());
                    userWareHouse.push().setValue(wareHouse);
                    Toast.makeText(getContext(), "Thêm mặt hàng thành công", Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                    loadwarehouseFromFirebase();
                }
            });
            alertDialog.setView(mView);
            alertDialog.show();
        });
    }
    private boolean isValidExpirationDate(String date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate currentDate = LocalDate.now();
        try {
            LocalDate expirationDate = LocalDate.parse(date, formatter);
            return !expirationDate.isBefore(currentDate);
        } catch (DateTimeParseException e) {
            return false;
        }
    }
    private boolean isExpirationDateNear(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate currentDate = LocalDate.now();
        LocalDate expirationDate = LocalDate.parse(date, formatter);

        Period diff = Period.between(currentDate, expirationDate);
        return diff.getDays() <= 7;
    }
    private boolean isValidExpirationDateYear(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate currentDate = LocalDate.now();
        LocalDate expirationDate = LocalDate.parse(date, formatter);
        Period diff = Period.between(currentDate, expirationDate);
        return diff.getYears() >= 2;
    }
    private void loadwarehouseFromFirebase() {
        List<WareHouse> wareHouseList = new ArrayList<>();
        Warehouse_Rec = root.findViewById(R.id.Manage_rec);
        Warehouse_Rec.setHasFixedSize(true);
        Warehouse_Rec.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        Warehouse_Rec.addItemDecoration(dividerItemDecoration);
        ManageAdapter adapter = new ManageAdapter(getContext(), wareHouseList);
        FirebaseDatabase.getInstance()
                .getReference("WareHouse")
                .child("UNIQUE_USER_ID")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot ItemSnapshot : snapshot.getChildren()) {
                                WareHouse wareHouse = ItemSnapshot.getValue(WareHouse.class);
                                wareHouseList.add(wareHouse);
                            }
                        }
                        Warehouse_Rec.setAdapter(adapter);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Load Menu Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void AnhXa() {
        Btn_Add = root.findViewById(R.id.Btn_add);
    }

}