package com.example.coffeeshop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;

import com.example.coffeeshop.Home.HomeFragment;
import com.example.coffeeshop.Manage.ManageFragment;
import com.example.coffeeshop.Start.LoginActivity;
import com.example.coffeeshop.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private int selectedTab = 1;
    ActivityMainBinding binding;
    HomeFragment homeFragment;
    FragmentManager fragmentManager;
    ManageFragment manageFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        Event_Bottom();
        AnhXa();
    }

    private void AnhXa() {
        homeFragment = new HomeFragment();
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.fragment_container_view,homeFragment).commit();

        manageFragment = new ManageFragment();

    }

    private void Event_Bottom() {
        binding.HomeLayout.setOnClickListener(view -> {
            if (selectedTab != 1) {
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container_view,homeFragment)
                        .commit();

                binding.ManaTxt.setVisibility(View.GONE);
                binding.LogTxt.setVisibility(View.GONE);

                binding.ManaImage.setImageResource(R.drawable.baseline_store_basic);
                binding.LoImage.setImageResource(R.drawable.baseline_logout_basic);

                binding.ManageLauout.setBackgroundColor(getColor(R.color.Background));
                binding.LogoutLayout.setBackgroundColor(getColor(R.color.Background));

                binding.HomeTxt.setVisibility(View.VISIBLE);
                binding.HomeImage.setImageResource(R.drawable.home_icon_selected);
                binding.HomeLayout.setBackgroundResource(R.drawable.custom_navigation_bottom);

                ScaleAnimation scaleAnimation = new ScaleAnimation(0.8f, 1.0f, 1f, 1f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
                scaleAnimation.setDuration(200);
                scaleAnimation.setFillAfter(true);
                binding.HomeLayout.startAnimation(scaleAnimation);
                selectedTab = 1;
            }
        });

        binding.ManageLauout.setOnClickListener(view ->{
            if(selectedTab !=2)
            {
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container_view,manageFragment)
                        .commit();

                binding.HomeTxt.setVisibility(View.GONE);
                binding.LogTxt.setVisibility(View.GONE);

                binding.HomeImage.setImageResource(R.drawable.home_icon_basic);
                binding.LoImage.setImageResource(R.drawable.baseline_logout_basic);

                binding.HomeLayout.setBackgroundColor(getColor(R.color.Background));
                binding.LogoutLayout.setBackgroundColor(getColor(R.color.Background));

                binding.ManaTxt.setVisibility(View.VISIBLE);
                binding.ManaImage.setImageResource(R.drawable.baseline_store_24);
                binding.ManageLauout.setBackgroundResource(R.drawable.custom_navigation_bottom);

                ScaleAnimation scaleAnimation = new ScaleAnimation(0.8f, 1.0f, 1f, 1f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
                scaleAnimation.setDuration(200);
                scaleAnimation.setFillAfter(true);
                binding.ManageLauout.startAnimation(scaleAnimation);
                selectedTab = 2;
            }
        });
        binding.LogoutLayout.setOnClickListener(view -> {
            if(selectedTab !=3)
            {
                openFeedbackDialog(Gravity.CENTER);
                binding.HomeTxt.setVisibility(View.GONE);
                binding.ManaTxt.setVisibility(View.GONE);

                binding.HomeImage.setImageResource(R.drawable.home_icon_basic);
                binding.ManaImage.setImageResource(R.drawable.baseline_store_basic);

                binding.HomeLayout.setBackgroundColor(getColor(R.color.Background));
                binding.ManageLauout.setBackgroundColor(getColor(R.color.Background));

                binding.LogTxt.setVisibility(View.VISIBLE);
                binding.LoImage.setImageResource(R.drawable.baseline_logout_24);
                binding.LogoutLayout.setBackgroundResource(R.drawable.custom_navigation_bottom);
                ScaleAnimation scaleAnimation = new ScaleAnimation(0.8f, 1.0f, 1f, 1f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
                scaleAnimation.setDuration(200);
                scaleAnimation.setFillAfter(true);
                binding.LogoutLayout.startAnimation(scaleAnimation);
                selectedTab = 3;
            }
        });
    }

    private void openFeedbackDialog(int center) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog);
        Window window = dialog.getWindow();
        if(window == null){
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windownAttributes = window.getAttributes();
        windownAttributes.gravity = windownAttributes.gravity;
        window.setAttributes(windownAttributes);
        Button No = dialog.findViewById(R.id.No);
        Button Yes = dialog.findViewById(R.id.Yes);
        No.setOnClickListener(view -> {
            dialog.dismiss();
        });
        Yes.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);

        });
        dialog.show();
    }
}