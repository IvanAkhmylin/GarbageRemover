package com.example.garbageremover;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.garbageremover.Adapter.ImageViewPagerAdapter;
import com.example.garbageremover.Fragments.FragmentWithCleanRequest;
import com.example.garbageremover.Model.RequestModel;
import com.example.garbageremover.Model.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.location.Location.convert;
import static android.os.Environment.getExternalStoragePublicDirectory;

public class CreateRequestForClean extends AppCompatActivity {
    private final int TAKE_CAMERA_CAPTURE = 3;
    private final int GET_IMAGE_GALLERY = 4;
    private final int REQUEST_PERMISSION = 5;
    private EditText description , userPayment;
    private FusedLocationProviderClient client;
    private TextView userMoney;
    private ImageView requestImage ;
    private ImageViewPagerAdapter viewPagerAdapter;
    private StorageReference mStorageRef;
    private FirebaseAuth mAuth;
    private User data;
    private Uri requestUri;
    private FirebaseUser mUser;
    private FirebaseDatabase mDatabase;
    private FirebaseStorage mStorage;
    private List<Uri> imageUri = new ArrayList<Uri>();
    private String photoPath = null;
    public  String stringLatitude,stringLongitude;
    private RequestModel requestModel;
    private double Latitude , Longitude;
    private boolean showImage = false;
    private Button btnCreateRequest, btnGetImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_request_for_clean);
        client = LocationServices.getFusedLocationProviderClient(this);
        btnGetImage = findViewById(R.id.getImage);
        btnGetImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImageFrom();
            }
        });
        description = findViewById(R.id.description_create_request);
        requestImage  = findViewById(R.id.viewPager);
        requestImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (showImage) {
                    Drawable drawable = requestImage.getDrawable();
                    Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                    Intent intent = new Intent(CreateRequestForClean.this, ShowImageActivity.class);
                    intent.putExtra("user", "Back");
                    ShowImageActivity.getImage(bitmap);
                    startActivity(intent);
                }
            }
        });
//        viewPager.setAdapter(viewPagerAdapter);
//        viewPager.setCurrentItem(0);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        userPayment = findViewById(R.id.payment_money);
        userMoney = findViewById(R.id.user_money);
        getUserData();
        btnCreateRequest = findViewById(R.id.createRequest);
        btnCreateRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnCreateRequest();
            }
        });

    }
    private void getImageFrom(){
        final String[] items = {"Camera" , "Gallery" };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:
                        showImage = true;
                        getCameraImage();
                        break;
                    case 1:
                        showImage = true;
                        getImageGallery();
                        break;
                }
            }
        });
        builder.show();
    }

    private void getCameraImage() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION);
        } else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(getPackageManager()) != null) {
                File photoFile;
                photoFile = createPhotoFile();
                if (photoFile != null) {
                    photoPath = photoFile.getAbsolutePath();
                    requestUri = FileProvider.getUriForFile(CreateRequestForClean.this, "com.example.garbageremover.fileprovider", photoFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, requestUri);
                    startActivityForResult(intent, TAKE_CAMERA_CAPTURE);
                }
            }
        }
    }

    public void getUserData() {
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = database.child("Users").child(mUser.getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                data = dataSnapshot.getValue(User.class);
                userMoney.setText(String.valueOf(data.getMoney()) + " Сом");
                Toast.makeText(CreateRequestForClean.this, String.valueOf(data.getMoney()), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    private File createPhotoFile() {
        String name = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File storageDirectory = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(name, ".jpeg", storageDirectory);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case TAKE_CAMERA_CAPTURE:
                    requestImage.setImageURI(requestUri);
                    try {
                        ExifInterface exifInterface = new ExifInterface(photoPath);
                        showEXIFInfo(exifInterface);
                    } catch (IOException e) {
                        Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case GET_IMAGE_GALLERY:
                    requestUri = data.getData();
                    String imagePath = requestUri.getPath();
                    StringBuilder stringBuilder = new StringBuilder(imagePath);
                    stringBuilder.delete(0, 5);
                    imagePath = stringBuilder.toString();
                    requestImage.setImageURI(requestUri);
//                    imageUri.add(uriImage);
//                    viewPagerAdapter = new ImageViewPagerAdapter(this, imageUri);
//                    viewPagerAdapter.notifyDataSetChanged();
//                    viewPager.setAdapter(viewPagerAdapter);
//                    viewPager.setCurrentItem(imageUri.size());
                    try {
                        ExifInterface exifInterface = new ExifInterface(imagePath);
                        showEXIFInfo(exifInterface);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }


        }

    }

    private void showEXIFInfo(ExifInterface exifInterface) {

        String latitude = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
        String latitudeRef = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
        String longitude = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
        String longitudeRef =  exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
        
        if (latitude != null || latitudeRef != null || longitude != null || longitudeRef != null){
            if (latitudeRef.equals("N")){
                Latitude = convertGPSData(latitude);
            }else {
                Latitude = 0 - convertGPSData(latitude);
            }
            if (longitudeRef.equals("E")){
                Longitude = convertGPSData(longitude);
            }else{
                Longitude = 0 - convertGPSData(longitude);
            }
            stringLatitude = String.valueOf(Latitude);
            stringLongitude = String.valueOf(Longitude);
            Toast.makeText(this, "\n" + stringLatitude+"\n"+stringLongitude, Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Cannot get GEO data from image, please check camera settings", Toast.LENGTH_SHORT).show();
        }
    }

    private double convertGPSData(String GPSData) {
        float result ;
        String[] DMS = GPSData.split(",",3);

        String[] stringD = DMS[0].split("/",2);
            Double D0 = Double.valueOf(stringD[0]);
            Double D1 = Double.valueOf(stringD[1]);
            Double doubleD = D0/D1;
        String[] stringM = DMS[1].split("/",2);
            Double M0 = Double.valueOf(stringM[0]);
            Double M1 = Double.valueOf(stringM[1]);
            Double doubleM = M0 / M1;
        String[] stringS = DMS[2].split("/",2);
            Double S0 = Double.valueOf(stringS[0]);
            Double S1 = Double.valueOf(stringS[1]);
            Double doubleS = S0 / S1;
        result =  new Float(doubleD +(doubleM / 60) + (doubleS / 3600));
        return result;
    }
    private String getAddressFromLatLon(double latitude, double longitude){
        Geocoder geocoder ;
        List<Address> addresses = new ArrayList<Address>() ;
        geocoder = new Geocoder(this,Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(latitude,longitude,1);
        } catch (IOException e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
        String address = addresses.get(0).getAddressLine(0);
        return address;
    }
    public void btnCreateRequest() {
            if (description.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, "Write some description", Toast.LENGTH_SHORT).show();
            }else if(userPayment.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, "Write some userMoney", Toast.LENGTH_SHORT).show();
            }else if (requestImage.getDrawable() == null) {
                Toast.makeText(this, "Load some image", Toast.LENGTH_SHORT).show();
            }else if (stringLatitude == null || stringLongitude == null){
                Toast.makeText(this, "This image unsupported", Toast.LENGTH_SHORT).show();
            }else if ((Integer.parseInt(userMoney.getText().toString()) - (Integer.parseInt(userPayment.getText().toString()))) < 0) {
                            Toast.makeText(this, "Not enough money", Toast.LENGTH_SHORT).show();
                        }else{
                            requestModel = new RequestModel();
                            requestModel.setAddress(getAddressFromLatLon(Latitude,Longitude));
                            requestModel.setCustomerName(data.getName() + " " + data.getSurname());
                            requestModel.setDescription(description.getText().toString());
                            requestModel.setLatitude(stringLatitude);
                            requestModel.setLongitude(stringLongitude);
                            requestModel.setPayment(userPayment.getText().toString());
                            String mUid = mUser.getUid(); //  в SignUpFields.class
                            stringLatitude = stringLatitude.replace(".",",");
                            stringLongitude = stringLongitude.replace(".",",");
                            uploadImageToFirebase(requestUri);


//        String stringLatitude = String.valueOf(Latitude);
//        String stringLongitude = String.valueOf(Longitude);
//        String uri = String.format(Locale.ENGLISH,"geo:0,0?q="+stringLatitude+" "+stringLongitude);
//        Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(uri));
//        startActivity(intent);

            }

    }
    private void getDownloadUri(){
        StorageReference reference = FirebaseStorage.getInstance().getReference("RequestImages");
        reference.child(stringLatitude + " " + stringLongitude+"/"+stringLatitude + " " + stringLongitude).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                requestModel.setCustomerImageUri(uri.toString());
                FirebaseDatabase.getInstance().getReference("CleanRequests")
                        .child( stringLatitude+ " " + stringLongitude)
                        .setValue(requestModel);
                setResult(RESULT_OK);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CreateRequestForClean.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void uploadImageToFirebase(Uri requestUri) {
        if (mUser != null){
            StorageReference riversRef = mStorageRef.child("RequestImages/"+ stringLatitude + " " + stringLongitude+"/"+stringLatitude + " " + stringLongitude);
            riversRef.putFile(requestUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get a URL to the uploaded content
                            Toast.makeText(CreateRequestForClean.this, "Good", Toast.LENGTH_SHORT).show();
                            getDownloadUri();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            Toast.makeText(CreateRequestForClean.this, exception.toString() , Toast.LENGTH_SHORT).show();
                            // ...

                        }
                    });

        }else {
            Toast.makeText(CreateRequestForClean.this, "GG", Toast.LENGTH_SHORT).show();
        }
    }


    public void getImageGallery() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("image/jpeg");
        startActivityForResult(Intent.createChooser(intent,"Select Picture") , GET_IMAGE_GALLERY);
    }
}

