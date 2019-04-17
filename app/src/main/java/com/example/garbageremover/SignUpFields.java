package com.example.garbageremover;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class SignUpFields extends AppCompatActivity {
    EditText email, password, name , surname, phoneNumber, city;
    private boolean valid = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_fields);
        email = findViewById(R.id.email);
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (email.getText().toString().trim().isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()){
                    email.setError("Enter valid email address");
                    valid = false;
                }else{
                    email.setError(null);
                    valid = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        password = findViewById(R.id.password);
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (password.getText().toString().trim().isEmpty() || password.getText().toString().trim().length() < 5 ){
                    password.setError("Password must be 5 characters or more");
                    valid = false;
                }else{
                    password.setError(null);
                    valid = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        name = findViewById(R.id.name);
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (name.getText().toString().trim().isEmpty() || name.getText().toString().length() < 2){
                    name.setError("Invalid name");
                    valid = false;
                }else{
                    name.setError(null);
                    valid = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        surname = findViewById(R.id.surname);
        surname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (surname.getText().toString().trim().isEmpty() || surname.getText().toString().length() < 3){
                    surname.setError("Invalid surname");
                    valid = false;
                }else{
                    surname.setError(null);
                    valid = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        city = findViewById(R.id.city);
        city.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (city.getText().toString().trim().isEmpty() || city.getText().toString().length() < 3){
                    city.setError("Invalid city");
                    valid = false;
                }else{
                    city.setError(null);
                    valid = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        phoneNumber = findViewById(R.id.phoneNumber);
        phoneNumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        phoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (phoneNumber.getText().toString().trim().isEmpty() || phoneNumber.getText().toString().length() < 6){
                    phoneNumber.setError("Invalid phone number");
                    valid = false;
                }else{
                    phoneNumber.setError(null);
                    valid = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    public void btn_Registration(View view) {
        if (!valid){
            Toast.makeText(this, "Enter Data Correct", Toast.LENGTH_SHORT).show();
        }else {
            Intent intent = new Intent();
            intent.putExtra("email", email.getText().toString().trim());
            intent.putExtra("password", password.getText().toString().trim());
            intent.putExtra("name", name.getText().toString().trim());
            intent.putExtra("surname", surname.getText().toString().trim());
            intent.putExtra("phoneNumber", phoneNumber.getText().toString().trim());
            intent.putExtra("city",city.getText().toString().trim());
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}
