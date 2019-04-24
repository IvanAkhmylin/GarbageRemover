package com.example.garbageremover;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.garbageremover.Model.PerformModel;
import com.example.garbageremover.Model.RequestModel;
import com.example.garbageremover.Model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.example.garbageremover.CreateRequestForClean.GET_IMAGE_GALLERY;
import static com.example.garbageremover.CreateRequestForClean.REQUEST_PERMISSION;
import static com.example.garbageremover.CreateRequestForClean.TAKE_CAMERA_CAPTURE;
import static com.example.garbageremover.Fragments.FragmentWithCleanRequest.REQUEST_ADDRESS;
import static com.example.garbageremover.Fragments.FragmentWithCleanRequest.REQUEST_CUSTOMER_ID;
import static com.example.garbageremover.Fragments.FragmentWithCleanRequest.REQUEST_CUSTOMER_NAME;
import static com.example.garbageremover.Fragments.FragmentWithCleanRequest.REQUEST_DESCRIPTION;
import static com.example.garbageremover.Fragments.FragmentWithCleanRequest.REQUEST_IMAGE;
import static com.example.garbageremover.Fragments.FragmentWithCleanRequest.REQUEST_LATITUDE;
import static com.example.garbageremover.Fragments.FragmentWithCleanRequest.REQUEST_LONGITUDE;
import static com.example.garbageremover.Fragments.FragmentWithCleanRequest.REQUEST_PAYMENT;

public class DetailOfRequest extends AppCompatActivity {
    private ImageView requestImage, imagePerformRequest;
    private EditText performDescription;

    private String photoPath;
    private String Image,Image1;
    private Uri performRequestUriImage;
    private StorageReference mStorageRef;
    private String RequestCreator;
    private PerformModel performModel ;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    Dialog dialogPerformRequest,dialog;
    private TextView customerTextView,descriptionTextView,priceTextView,addressTextView, performerName, performShowDescription, performDate;
    private Button showRequestLocation , performRequest, deleteRequest , getImage, perform ,showPerformRequest, cancel;
    private String latitude, longitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_of_request);
        Intent intent = getIntent();
        mAuth = FirebaseAuth.getInstance();
        performModel = new PerformModel();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mUser = mAuth.getCurrentUser();
        performRequest = findViewById(R.id.performRequest);
        showPerformRequest = findViewById(R.id.show_performRequest);
        showPerformRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDoneRequest();
            }
        });
        deleteRequest = findViewById(R.id.deleteRequest);
        if (intent.getStringExtra(REQUEST_CUSTOMER_ID).equals(mUser.getUid())){
            deleteRequest.setVisibility(View.VISIBLE);
            performRequest.setVisibility(View.VISIBLE);
            performRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    performRequestForClean();
                }
            });
            deleteRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteRequestForClean();
                }
            });
        }else {

        }

        customerTextView = findViewById(R.id.customerID);
            customerTextView.setText(intent.getStringExtra(REQUEST_CUSTOMER_NAME));
        descriptionTextView = findViewById(R.id.descriptionID);
            descriptionTextView.setText(intent.getStringExtra(REQUEST_DESCRIPTION));
        priceTextView = findViewById(R.id.priceID);
            priceTextView.setText(intent.getStringExtra(REQUEST_PAYMENT));
        addressTextView = findViewById(R.id.addressID);
            addressTextView.setText(intent.getStringExtra(REQUEST_ADDRESS));

        latitude = intent.getStringExtra(REQUEST_LATITUDE);
        longitude = intent.getStringExtra(REQUEST_LONGITUDE);

        showRequestLocation = findViewById(R.id.getRequestLocation);
        requestImage = findViewById(R.id.requestImage);
        requestImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable drawable = requestImage.getDrawable();
                Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
                Intent intent  = new Intent(DetailOfRequest.this, ShowImageActivity.class);
                intent.putExtra("user","Back");
                ShowImageActivity.getImage(bitmap);
                startActivity(intent);
            }
        });

        checkRequestStatus();
        Image = intent.getStringExtra(REQUEST_IMAGE);
        Glide.with(this).load(intent.getStringExtra(REQUEST_IMAGE)).into(requestImage);
        showRequestLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String stringLatitude = String.valueOf(latitude);
                String stringLongitude = String.valueOf(longitude);
                String uri = String.format(Locale.ENGLISH,"geo:0,0?q="+stringLatitude+" "+stringLongitude);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(intent);
            }
        });


    }

    private void showDoneRequest() {
        dialogPerformRequest = new Dialog(this,R.style.NarrowDialog);
        dialogPerformRequest.setContentView(R.layout.custom_dialog_show_perform_request);
        imagePerformRequest = dialogPerformRequest.findViewById(R.id.performRequestImage);
        performShowDescription = dialogPerformRequest.findViewById(R.id.description_perform_request);
        performDate = dialogPerformRequest.findViewById(R.id.performDateID);
        performerName = dialogPerformRequest.findViewById(R.id.performerID);
        cancel = dialogPerformRequest.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogPerformRequest.dismiss();
            }
        });

        perform = dialogPerformRequest.findViewById(R.id.RequestPerform);
        perform.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (RequestCreator.equals(mUser.getUid())){
                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(DetailOfRequest.this);
                    alertDialog.setTitle("Are you sure ?");
                    alertDialog.setMessage("These actions will confirm that your request has been completed?");
                    alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteRequest();
                            onBackPressed();
                        }
                    });
                    alertDialog.setNegativeButton("NO" ,new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    AlertDialog dialog = alertDialog.create();
                    dialog.show();
                }

            }
        });

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = database.child("PerformRequests").child(latitude + " " + longitude);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                PerformModel pm= dataSnapshot.getValue(PerformModel.class);
                Image1 = pm.getPerformImageUri();
                Glide.with(DetailOfRequest.this).load(pm.getPerformImageUri()).into(imagePerformRequest);
                performDate.setText(pm.getPerformDate());
                String name = pm.getPerformerName().substring(0,1).toUpperCase() +  pm.getPerformerName().substring(1);
                String surname = pm.getPerformerSurname().substring(0,1).toUpperCase() +  pm.getPerformerSurname().substring(1);
                performerName.setText(name + " " + surname);
                performShowDescription.setText(pm.getPerformerDescription());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        dialogPerformRequest.show();
        dialogPerformRequest.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    private void deleteRequest() {
        latitude = latitude.replace(".",",");
        longitude = longitude.replace(".",",");

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref1 = database.child("CleanRequests").child(latitude + " " + longitude);

        ref1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                    appleSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        DatabaseReference ref2 = database.child("PerformRequests").child(latitude + " " + longitude);
        ref2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                    appleSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        StorageReference img1 = FirebaseStorage.getInstance().getReferenceFromUrl(Image1);
        img1.delete();
        StorageReference img2 = FirebaseStorage.getInstance().getReferenceFromUrl(Image);
        img2.delete();

    }


    private void checkRequestStatus() {
        latitude = latitude.replace(".",",");
        longitude = longitude.replace(".",",");

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = database.child("CleanRequests").child(latitude + " " + longitude);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                RequestModel  model = dataSnapshot.getValue(RequestModel.class);
                RequestCreator = model.getCustomerID();
                String status = model.getRequestStatus();
                if (("before".equals(status)) && mAuth.getCurrentUser().getUid().equals((RequestCreator))){
                    Toast.makeText(DetailOfRequest.this,"before", Toast.LENGTH_SHORT).show();
                    deleteRequest.setVisibility(View.GONE);
                    performRequest.setVisibility(View.VISIBLE);
                }else if(model.getRequestStatus().equals("after")){
                    Toast.makeText(DetailOfRequest.this, "After", Toast.LENGTH_SHORT).show();
                    deleteRequest.setVisibility(View.GONE);
                    performRequest.setVisibility(View.GONE);
                    showPerformRequest.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void performRequestForClean() {
        getPerformerNameSurname();
        dialog = new Dialog(this,R.style.NarrowDialog);
        dialog.setContentView(R.layout.custom_dialog_perform_request);
        imagePerformRequest = dialog.findViewById(R.id.performRequestImage);
        performDescription = dialog.findViewById(R.id.description_perform_request);
        getImage = dialog.findViewById(R.id.getPerformImage);
        getImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] items = {"Camera" , "Gallery" };
                AlertDialog.Builder builder = new AlertDialog.Builder(DetailOfRequest.this);
                builder.setTitle("Choose");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                getCameraImage();
                                break;
                            case 1:
                                getImageGallery();
                                break;
                        }
                    }
                });
                builder.show();

            }
        });
        perform = dialog.findViewById(R.id.RequestPerform);
        perform.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();

                performModel.setPerformDate(dateFormat.format(date));
                performModel.setPerformerDescription(performDescription.getText().toString());
                FirebaseDatabase.getInstance().getReference("PerformRequests")
                        .child( latitude+ " " + longitude)
                        .setValue(performModel);
                FirebaseDatabase.getInstance().getReference("CleanRequests")
                        .child( latitude+ " " + longitude)
                        .child("requestStatus")
                        .setValue("after");
                dialog.dismiss();
                onBackPressed();
                Toast.makeText(DetailOfRequest.this, mUser.getDisplayName(), Toast.LENGTH_SHORT).show();
            }
        });
        cancel = dialog.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }
    private void getPerformerNameSurname(){
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = database.child("Users").child(mAuth.getCurrentUser().getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                performModel.setPerformerName(user.getName());
                performModel.setPerformerSurname(user.getSurname());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void uploadImage(){
        latitude = latitude.replace(".",",");
        longitude = longitude.replace(".",",");
        if (mUser != null){
            StorageReference riversRef = mStorageRef.child("PerformRequests/"+ latitude + " " + longitude+"/"+latitude + " " + longitude);
            riversRef.putFile(performRequestUriImage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get a URL to the uploaded content
                            Toast.makeText(DetailOfRequest.this, "Good", Toast.LENGTH_SHORT).show();
                            getDownloadUri();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    Toast.makeText(DetailOfRequest.this, exception.toString() , Toast.LENGTH_SHORT).show();
                    // ...
                }
            });

        }else {
            Toast.makeText(DetailOfRequest.this, "GG", Toast.LENGTH_SHORT).show();
        }
    }
    private void getDownloadUri(){
        latitude = latitude.replace(".",",");
        longitude = longitude.replace(".",",");
        StorageReference reference = FirebaseStorage.getInstance().getReference("PerformRequests");
        reference.child(latitude + " " + longitude+"/"+latitude + " " + longitude).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                performModel.setPerformImageUri(uri.toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DetailOfRequest.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getImageGallery() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("image/jpeg");
        startActivityForResult(Intent.createChooser(intent,"Select Picture") , GET_IMAGE_GALLERY);
    }

    private void getCameraImage() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION);
        } else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(getPackageManager()) != null) {
                File photoFile;
                photoFile = CreateRequestForClean.createPhotoFile();
                if (photoFile != null) {
                    photoPath = photoFile.getAbsolutePath();
                    performRequestUriImage= FileProvider.getUriForFile(DetailOfRequest.this, "com.example.garbageremover.fileprovider", photoFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, performRequestUriImage);
                    startActivityForResult(intent, TAKE_CAMERA_CAPTURE);
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case TAKE_CAMERA_CAPTURE:
                    imagePerformRequest.setImageURI(performRequestUriImage);
                    uploadImage();
                    break;
                case GET_IMAGE_GALLERY:
                    performRequestUriImage = data.getData();
                    uploadImage();
                    String imagePath = performRequestUriImage.getPath();
                    StringBuilder stringBuilder = new StringBuilder(imagePath);
                    stringBuilder.delete(0, 5);
                    imagePath = stringBuilder.toString();
                    imagePerformRequest.setImageURI(performRequestUriImage);
                    break;
            }
        }
    }

    private void deleteRequestForClean() {
        latitude = latitude.replace(".",",");
        longitude = longitude.replace(".",",");
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Are you sure ?");
        alertDialog.setMessage("Do you want delete your request for clean ?");
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                Query query = reference.child("CleanRequests").child(latitude+" "+longitude);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            snapshot.getRef().removeValue();
                        }
                        Toast.makeText(DetailOfRequest.this, "Successful", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(DetailOfRequest.this, "Unsuccessful", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });
        alertDialog.setNegativeButton("NO" ,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = alertDialog.create();
        dialog.show();
    }

}
