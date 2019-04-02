package com.example.garbageremover;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.garbageremover.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginSignUpActivity extends AppCompatActivity {
    EditText mEmail , mPassword;
    private FirebaseAuth mAuth;
    private final int SIGN_UP_REQUEST = 0;
    private User userInfo;
    public FirebaseUser currentUser;
    public boolean exist = true ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mEmail = findViewById(R.id.edit_email);
        mPassword = findViewById(R.id.edit_password);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth.getCurrentUser();

    }

    public void btn_SignIn(View view) {
        signInUser();

    }



    public void btn_SignUp(View view) {
        Intent intent = new Intent(this,SignUpFields.class); // запуск активити для ррегистрации
        startActivityForResult(intent,SIGN_UP_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == SIGN_UP_REQUEST){
            String email =  data.getStringExtra("email");
            String password =  data.getStringExtra("password");
            String phoneNumber = data.getStringExtra("phoneNumber");
            // при помощи этого обьекта будет занесена информация в бд
            userInfo = new User();
            userInfo.email = email;
            userInfo.name = data.getStringExtra("name");
            userInfo.city =  data.getStringExtra("city");
            userInfo.surname = data.getStringExtra("surname");
            userInfo.phoneNumber = phoneNumber;
            userInfo.money = 0;
                                                              // получаем регистрациооные данные , и
            UserAlreadyExist(email,phoneNumber,password);    // отправляем на проверку (если есть юзер с данным емайлом или номером то
                                                            // то зарегистрироваться не получится
        }else{
            Toast.makeText(this, "Error getting data from SIGN UP Activity", Toast.LENGTH_SHORT).show();
        }
    }

    private void signInUser() {
        String email = mEmail.getText().toString().trim();
        final String password = mPassword.getText().toString().trim();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {   // если все гуд то переходи в UserActivity.class
                            // Sign in success, update UI with the signed-in user's information
                            currentUser = mAuth.getCurrentUser();
                            if (currentUser.isEmailVerified()){
                                Toast.makeText(LoginSignUpActivity.this, "Authentication success. ", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginSignUpActivity.this,UserActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }else{
                                Toast.makeText(LoginSignUpActivity.this, "Authentication failed. Please verify your email address", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginSignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
    private void signUpUser(String email,String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {              // регистрируем юзера , если все гуд , то отсылаем на почту письмо
                            currentUser = mAuth.getCurrentUser();  // для подтверждения емайл адреса
                            mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(LoginSignUpActivity.this, "Registration success. Check your email for verification", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(LoginSignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            if (currentUser != null ){       // заносим в базу данных информацию о пользователе , которую он указывал
                                String mUid = currentUser .getUid(); //  в SignUpFields.class
                                FirebaseDatabase.getInstance().getReference("Users")
                                        .child(mUid)
                                        .setValue(userInfo);
                            }else {
                                Toast.makeText(LoginSignUpActivity.this, "GG", Toast.LENGTH_SHORT).show();
                            }


                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginSignUpActivity.this,  task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void UserAlreadyExist(final String email, String phoneNumber, final String password){
        // все что с низу просто проверка на существование пользователя в бд

        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("Users");
        userReference.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null ){
                    Toast.makeText(LoginSignUpActivity.this, "User with this email already exist", Toast.LENGTH_SHORT).show();
                    exist = false;

                }else{
                    exist = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        userReference.orderByChild("phoneNumber").equalTo(phoneNumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null ){
                    exist = false;
                    Toast.makeText(LoginSignUpActivity.this, "User with this phone number already exist", Toast.LENGTH_SHORT).show();
                }else{
                    if (exist == true) {
                        signUpUser(email,password);
                    }else{
                        Toast.makeText(LoginSignUpActivity.this, "This user already exist", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    public void btn_ForgotPass(View view){
        // переход к UserForgotPassword активити
        Intent intent = new Intent(this,UserForgotPassword.class);
        startActivity(intent);
    }
}
