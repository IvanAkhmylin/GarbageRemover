package com.example.garbageremover;

import android.app.Application;
import android.content.Intent;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CheckCurrentUser extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        // проверка , если юзер уже логинился , то открывает его профиль если же нет то , открывает активити для логина .
        if (mUser != null && mUser.isEmailVerified()){
            startActivity(new Intent(CheckCurrentUser.this, UserActivity.class));
        }else{
            startActivity(new Intent(CheckCurrentUser.this, LoginSignUpActivity.class));
        }
    }
}
