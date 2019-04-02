package com.example.garbageremover;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.garbageremover.Adapter.EditProfileDiaolog;
import com.example.garbageremover.Model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class UserActivity extends AppCompatActivity implements  EditProfileDiaolog.DialogListener{
    private final int CHANGE_IMAGE_REQUEST = 3;
    FirebaseAuth mAuth ;
    FirebaseUser mUser;
    ImageView user_image;
    TextView email , phone , user_name_surname, city ;
    User user;
    private StorageReference mStorageRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        user_name_surname = findViewById(R.id.user_name_surname);
        email = findViewById(R.id.profile_email);
        phone = findViewById(R.id.profile_phone);
        city = findViewById(R.id.profile_city);
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mUser = mAuth.getCurrentUser();
        user_image = findViewById(R.id.image_profile);
        user_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                creteDialogForChoose();
            }
        });
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

        addProfileImageToTheView();
    }

    private void addProfileImageToTheView() {
        // получаем из Firebase изображение и выводим в ImageView
                // если изображение есть то выводит фото ззагруженное пользователем если нет то берет дефолтное изображение
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("UsersProfileImages");
        storageReference.child(mUser.getUid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(user_image);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                user_image.setImageResource(R.drawable.user_default_image);
            }
        });
    }


    private void creteDialogForChoose() {
        final String[] items = {"Show Image" , "Change Image" , "Edit Profile"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:
                        Drawable drawable = user_image.getDrawable();
                        Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG,100,baos);
                        byte[] bytes = baos.toByteArray();
                        // открываем изображение в новом активити
                        Intent intent  = new Intent(UserActivity.this, ShowImageActivity.class);
                        intent.putExtra("image",bytes);
                        intent.putExtra("user",user_name_surname.getText().toString());
                        startActivity(intent);
                        break;
                    case 1:
                        // код получения изображения из галлеереи
                        Intent intent1 = new Intent(Intent.ACTION_PICK);
                        intent1.setType("image/*");
                        startActivityForResult(intent1,CHANGE_IMAGE_REQUEST);
                        break;
                    case 2:
                        editProfileDialog();
                        break;
                }
            }
        });
        builder.show();
    }

    private void editProfileDialog() {
        // отправляе текующие даннные пользователя для изменения (эти данные записываются в ЕditText ы
        String[] name_surname = user_name_surname.getText().toString().split(" ");
        EditProfileDiaolog editDialog = new EditProfileDiaolog();
        editDialog.Constructor(name_surname[0],name_surname[1],city.getText().toString());
        editDialog.show(getSupportFragmentManager(),"Edit Profile");
    }


    public void signOut(View view) {
        mAuth.getInstance().signOut();
        Intent intent = new Intent(this, LoginSignUpActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHANGE_IMAGE_REQUEST && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            try {
                // получаем фото из галереи
                uploadUserProfileImage(mAuth.getCurrentUser(),imageUri);
                InputStream is = getContentResolver().openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                user_image.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            }

        }else{

        }
    }
    public void uploadUserProfileImage(FirebaseUser user, Uri uri){
        // отправляем в Firebase изображение которое выбрал пользователь
        if (user != null){
            String mUid = user.getUid();
            Uri file = uri;
            StorageReference riversRef = mStorageRef.child("UsersProfileImages/"+ mUid);
            riversRef.putFile(file)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get a URL to the uploaded content
                            Toast.makeText(UserActivity.this, "Good", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            Toast.makeText(UserActivity.this, exception.toString() , Toast.LENGTH_SHORT).show();
                            // ...
                        }
                    });

        }else {
            Toast.makeText(UserActivity.this, "GG", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void saveData(String name, String surname, String city) {
        // записываем данные которые изменил пользователь , в FB
        if (mUser != null){
            FirebaseDatabase.getInstance().getReference("Users").child(mUser.getUid()).child("name").setValue(name);
            FirebaseDatabase.getInstance().getReference("Users").child(mUser.getUid()).child("surname").setValue(surname);
            FirebaseDatabase.getInstance().getReference("Users").child(mUser.getUid()).child("city").setValue(city);
        }else {

        }
    }
}
