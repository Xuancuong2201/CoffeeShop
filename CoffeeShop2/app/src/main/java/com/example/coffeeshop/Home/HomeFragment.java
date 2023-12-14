package com.example.coffeeshop.Home;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.coffeeshop.BroadCast.ConnectionReceiver;
import com.example.coffeeshop.MenuCoffeeActivity;
import com.example.coffeeshop.MyCartActivity;
import com.example.coffeeshop.R;
import com.example.coffeeshop.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentHomeBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        // Inflate the layout for this fragment
        View view = binding.getRoot();

        //Event Click
        binding.BtnCoffee.setOnClickListener(view1 -> {
            boolean ret = ConnectionReceiver.isConnected();

            if (ret) {

                Intent intent = new Intent(HomeFragment.this.getActivity(), MenuCoffeeActivity.class);
                startActivity(intent);
            }
            else {
                final AlertDialog.Builder alert = new AlertDialog.Builder(HomeFragment.this.getActivity());
                View mView = getLayoutInflater().inflate(R.layout.custom_dialog_connect, null);
                alert.setView(mView);
                final AlertDialog alertDialog = alert.create();
                alertDialog.setCancelable(false);
                mView.findViewById(R.id.Btn_close).setOnClickListener(v -> {
                    alertDialog.dismiss();
                });
                alertDialog.show();
            }
        });


        return view;
    }
}