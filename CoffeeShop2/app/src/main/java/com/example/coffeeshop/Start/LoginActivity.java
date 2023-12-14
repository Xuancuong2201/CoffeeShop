package com.example.coffeeshop.Start;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.AlertDialog;
import android.app.ProgressDialog;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.coffeeshop.BroadCast.ConnectionReceiver;

import com.example.coffeeshop.MainActivity;

import com.example.coffeeshop.R;
import com.example.coffeeshop.databinding.ActivityLoginBinding;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LoginActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    ActivityLoginBinding binding;
    EditText Email, Password;
    Button End;
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
          Intent intent = new Intent(LoginActivity.this, MainActivity.class);
          startActivity(intent);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        Email = findViewById(R.id.Email);
        Password = findViewById(R.id.Password);
        End = findViewById(R.id.EndProcess);
        End.setOnClickListener(view -> {
            finish();
        });
        binding.TxtSignUp.setOnClickListener(view -> {
            ChangeActivityRegister();
        });
        binding.SignIn.setOnClickListener(view -> {
            String Email, Password;
            String pattern = getString(R.string.checkpassword);
            String pattern1 = getString(R.string.CheckEmail);
            Email = String.valueOf(binding.Email.getText());
            Password = String.valueOf(binding.Password.getText());
            Pattern regex = Pattern.compile(pattern);
            Matcher matcher = regex.matcher(Password);

            boolean KQ = Email.matches(pattern1);
            ProgressDialog mDialog = new ProgressDialog(LoginActivity.this);
            mDialog.setMessage("Please waiting....");
            mDialog.show();
            boolean ret = ConnectionReceiver.isConnected();
            if (!ret) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.custom_dialog_connect, null);
                alert.setView(mView);
                final AlertDialog alertDialog = alert.create();
                alertDialog.setCancelable(false);
                mView.findViewById(R.id.Btn_close).setOnClickListener(v -> {
                    alertDialog.dismiss();
                });
                alertDialog.show();
                mDialog.cancel();
                return;
            }
            if (Email.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Email không được để trống", Toast.LENGTH_SHORT).show();
                mDialog.cancel();
                return;
            }
            if (Password.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Password không được để trống", Toast.LENGTH_SHORT).show();
                mDialog.cancel();
                return;
            }
            if (Email.contains(" ") ||!KQ) {
                Toast.makeText(getApplicationContext(), "Email không đúng định dạng", Toast.LENGTH_SHORT).show();
                mDialog.cancel();
                return;
            }
            if (Password.length() > 16 || Password.length() < 8 ||matcher.find() || Password.contains(" ")) {
                Toast.makeText(getApplicationContext(), "Password không đúng định dạng", Toast.LENGTH_SHORT).show();
                mDialog.cancel();
                return;
            }
            mAuth.signInWithEmailAndPassword(Email, Password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(LoginActivity.this, "Email không tồn tại", Toast.LENGTH_SHORT).show();
                                mDialog.cancel();
                            }
                        }
                    });
        });
    }

    public void ChangeActivityRegister() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }
}