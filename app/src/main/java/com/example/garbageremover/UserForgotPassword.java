package com.example.garbageremover;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class UserForgotPassword extends AppCompatActivity {
    EditText email;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_forgot_password);
        email = findViewById(R.id.email_forgot_pass);
        mAuth = FirebaseAuth.getInstance();
    }

    public void btn_ForgotPass(View view) {
        if (email.getText().toString().isEmpty()){
            Toast.makeText(this, "Write email correct", Toast.LENGTH_SHORT).show();
        }else {
            mAuth.sendPasswordResetEmail(email.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(UserForgotPassword.this, "Check your email address for reset password", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(UserForgotPassword.this,MainActivity.class));
                    } else {
                        Toast.makeText(UserForgotPassword.this, "ERROR", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
