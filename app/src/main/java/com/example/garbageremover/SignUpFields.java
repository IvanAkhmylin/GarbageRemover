package com.example.garbageremover;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class SignUpFields extends AppCompatActivity {
    EditText email, password, name , surname, phoneNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_fields);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        name = findViewById(R.id.name);
        surname = findViewById(R.id.surname);
        phoneNumber = findViewById(R.id.phoneNumber);
    }

    public void btn_Registration(View view) {
        if (email.getText().toString().isEmpty() || password.getText().toString().isEmpty()
        || name.getText().toString().isEmpty() || surname.getText().toString().isEmpty()
        || phoneNumber.getText().toString().isEmpty()){
            Toast.makeText(this, "Enter Data Correct", Toast.LENGTH_SHORT).show();
        }else {
            Intent intent = new Intent();
            intent.putExtra("email", email.getText().toString().trim());
            intent.putExtra("password", password.getText().toString().trim());
            intent.putExtra("name", name.getText().toString().trim());
            intent.putExtra("surname", surname.getText().toString().trim());
            intent.putExtra("phoneNumber", phoneNumber.getText().toString().trim());
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}
