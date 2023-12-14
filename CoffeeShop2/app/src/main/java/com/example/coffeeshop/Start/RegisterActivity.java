package com.example.coffeeshop.Start;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.coffeeshop.BroadCast.ConnectionReceiver;
import com.example.coffeeshop.MainActivity;
import com.example.coffeeshop.Model.User;
import com.example.coffeeshop.R;
import com.example.coffeeshop.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);

        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityRegisterBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        binding.TxtSignIn.setOnClickListener(view -> {
            ChangeActivityLogin();
        });

        binding.BtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Email,Password,ConfirmPassword;
                String pattern = "[!@#$%^&*(),.?\":{}|<>]";
                String pattern1 = "^[\\w-]+(\\.[\\w-]+)*@[\\w-]+(\\.[\\w-]+)*(\\.[a-zA-Z]{2,})$";
                Email = String.valueOf(binding.Email.getText());
                Password=String.valueOf(binding.Password.getText());
                ConfirmPassword = String.valueOf(binding.ConfirmPassword.getText());
                Pattern regex = Pattern.compile(pattern);
                Matcher matcher = regex.matcher(Password);
                boolean KQ = Email.matches(pattern1);

                ProgressDialog mDialog = new ProgressDialog(RegisterActivity.this);
                mDialog.setMessage("Please waiting....");
                mDialog.show();

                boolean ret = ConnectionReceiver.isConnected();
                if (!ret) {
                    final AlertDialog.Builder alert = new AlertDialog.Builder(RegisterActivity.this);
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
                if (Email.contains(" ") || !KQ) {
                    Toast.makeText(getApplicationContext(), "Email không đúng định dạng", Toast.LENGTH_SHORT).show();
                    mDialog.cancel();
                    return;
                }
                if (Password.length()>16 || Password.length()<8||matcher.find()||Password.contains(" ")) {
                    Toast.makeText(getApplicationContext(), "Password không đúng định dạng", Toast.LENGTH_SHORT).show();
                    mDialog.cancel();
                    return;
                }
                if (!Password.equals(ConfirmPassword)) {
                    Toast.makeText(getApplicationContext(), "Password và Confirm Password phải giống nhau", Toast.LENGTH_SHORT).show();
                    mDialog.cancel();
                    return;
                }
                mAuth.createUserWithEmailAndPassword(Email, Password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                }
                                else {
                                    Toast.makeText(getApplicationContext(), "Email đã tồn tại", Toast.LENGTH_SHORT).show();
                                    mDialog.cancel();
                                    return;
                                }
                            }
                        });
            }
        });
}

    public void ChangeActivityLogin(){
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}