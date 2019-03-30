package com.example.garbageremover;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.garbageremover.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserActivity extends AppCompatActivity {
    FirebaseAuth mAuth ;
    FirebaseUser mUser;
    ImageView user_image;
    TextView email , phone , user_name_surname, city ;
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        user_name_surname = findViewById(R.id.user_name_surname);
        email = findViewById(R.id.profile_email);
        phone = findViewById(R.id.profile_phone);
        city = findViewById(R.id.profile_city);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = database.child("Users").child(mAuth.getCurrentUser().getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                user = dataSnapshot.getValue(User.class);
                String name = user.getName().substring(0,1).toUpperCase() +  user.getName().substring(1);
                String surname = user.getSurname().substring(0,1).toUpperCase() +  user.getSurname().substring(1);
                user_name_surname.setText(name + " " + surname);
                email.setText(user.getEmail());
                phone.setText(user.getPhoneNumber());
                city.setText(user.getCity());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });


    }




    public void signOut(View view) {
        mAuth.getInstance().signOut();
        Intent intent = new Intent(this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }
}
