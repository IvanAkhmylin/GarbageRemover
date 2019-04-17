package com.example.garbageremover.Fragments;

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
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.garbageremover.Adapter.EditProfileDialog;
import com.example.garbageremover.LoginSignUpActivity;
import com.example.garbageremover.Model.User;
import com.example.garbageremover.R;
import com.example.garbageremover.ShowImageActivity;
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

import static android.app.Activity.RESULT_OK;

public class UserProfileFragment extends Fragment {
    private final int CHANGE_IMAGE_REQUEST = 3;
    FirebaseAuth mAuth ;
    FirebaseUser mUser;
    ImageView user_image;
    TextView email , phone , user_name_surname, city ;
    User user;
    Button btnSignOut;
    private StorageReference mStorageRef;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.user_profile_fragment,container,false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View v = getView();
        btnSignOut = v.findViewById(R.id.btn_signOut);
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), LoginSignUpActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        user_name_surname = v.findViewById(R.id.user_name_surname);
        email = v.findViewById(R.id.profile_email);
        phone = v.findViewById(R.id.profile_phone);
        city = v.findViewById(R.id.profile_city);
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mUser = mAuth.getCurrentUser();
        user_image = v.findViewById(R.id.image_profile);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:
                        Drawable drawable = user_image.getDrawable();
                        Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
                        Intent intent  = new Intent(getActivity(), ShowImageActivity.class);
                        intent.putExtra("user",user_name_surname.getText().toString());
                        ShowImageActivity.getImage(bitmap);
                        getActivity().startActivity(intent);
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
        EditProfileDialog editDialog = new EditProfileDialog();
        editDialog.EditProfileDialog(mUser);
        editDialog.Constructor(name_surname[0],name_surname[1],city.getText().toString());
        editDialog.show(getFragmentManager(),"Edit Profile");
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHANGE_IMAGE_REQUEST && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            try {
                // получаем фото из галереи
                uploadUserProfileImage(mAuth.getCurrentUser(),imageUri);
                InputStream is = getActivity().getApplicationContext().getContentResolver().openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                user_image.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(getActivity(), "Good", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            Toast.makeText(getActivity(), exception.toString() , Toast.LENGTH_SHORT).show();
                            // ...
                        }
                    });

        }else {
            Toast.makeText(getActivity(), "GG", Toast.LENGTH_SHORT).show();
        }


    }


}
