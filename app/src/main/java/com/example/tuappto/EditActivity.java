package com.example.tuappto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class EditActivity extends AppCompatActivity {

    private static final int PERMISSION_CAMERA_ID = 1;
    private static final int PERMISSION_GALLERY_ID = 2;
    private static final int REQUEST_IMAGE_CAPTURE = 3;
    private static final int IMAGE_PICKER_REQUEST = 4;

    public ImageButton imageButtonCamera;
    public ImageButton imageButtonGallery;
    public ImageView imageViewProperty;
    public EditText editTextPrice;
    public EditText editTextRooms;
    public EditText editTextArea;
    public EditText editTextParking;
    public EditText editTextDescription;
    public CheckBox checkBoxSell;
    public CheckBox checkBoxRent;

    public Bundle bundle;
    public Intent intent;

    public int price;
    public int rooms;
    public int area;
    public int parking;
    public String description;
    public String sellOrRent;
    public String imagePath;
    public LatLng location;
    public double latitude;
    public  double longitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        imageButtonCamera = findViewById(R.id.imageButtonCamera);
        imageButtonGallery = findViewById(R.id.imageButtonGallery);
        imageViewProperty = findViewById(R.id.imageViewProperty);
        editTextPrice = findViewById(R.id.editTextPrice);
        editTextRooms = findViewById(R.id.editTextRooms);
        editTextArea = findViewById(R.id.editTextArea);
        editTextParking = findViewById(R.id.editTextParking);
        editTextDescription = findViewById(R.id.editTextDescription);
        checkBoxSell = findViewById(R.id.checkBoxSell);
        checkBoxRent = findViewById(R.id.checkBoxRent);

        intent = getIntent();
        bundle = intent.getBundleExtra("bundle");

        assert bundle != null;
        price = bundle.getInt("price");
        rooms = bundle.getInt("rooms");
        area = bundle.getInt("area");
        parking = bundle.getInt("parking");
        description = bundle.getString("description");
        sellOrRent = bundle.getString("sellOrRent");
        imagePath = bundle.getString("imagePath");
        latitude = bundle.getDouble("latitude");
        longitude = bundle.getDouble("longitude");
        location = new LatLng(latitude,longitude);

        editTextPrice.setText(price);
        editTextArea.setText(area);
        editTextRooms.setText(rooms);
        editTextParking.setText(parking);
        editTextDescription.setText(description);

        if(sellOrRent.equals("rent")){
            checkBoxSell.setChecked(true);
        }
        else if(sellOrRent.equals("sell")){
            checkBoxRent.setChecked(true);
        }
        else{
            checkBoxSell.setChecked(true);
            checkBoxRent.setChecked(true);
        }





        imageButtonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(v.getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    takePhoto();
                }
                else {
                    requestPermission(EditActivity.this, Manifest.permission.CAMERA, "Acceso a cámara necesario", PERMISSION_CAMERA_ID);
                }
            }
        });

        imageButtonGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(v.getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Intent pickImage = new Intent(Intent.ACTION_PICK);
                    pickImage.setType("image/*");
                    startActivityForResult(pickImage, IMAGE_PICKER_REQUEST);

                } else {
                    requestPermission(EditActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE, "Leer almacenamiento es necesario", PERMISSION_GALLERY_ID);
                }
            }
        });
    }

    private void requestPermission(Activity context, String permiso, String justificacion, int idCode) {

        if (ContextCompat.checkSelfPermission(context, permiso) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(context, permiso)) {
                Toast.makeText(context, justificacion, Toast.LENGTH_SHORT).show();
            }
            ActivityCompat.requestPermissions(context, new String[]{permiso}, idCode);
        }
    }

    private void takePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE:
                if (resultCode == RESULT_OK) {
                    try {
                        Bundle extras = data.getExtras();
                        Bitmap imageBitmap;
                        if (extras != null) {
                            imageBitmap = (Bitmap) extras.get("data");
                            imageViewProperty.setImageBitmap(imageBitmap);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return;

            case IMAGE_PICKER_REQUEST:
                if (resultCode == RESULT_OK) {
                    try {
                        final Uri imageUri = data.getData();
                        final InputStream imageStream;

                        if (imageUri != null) {
                            imageStream = getContentResolver().openInputStream(imageUri);
                            final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                            imageViewProperty.setImageBitmap(selectedImage);
                        }

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }

        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_CAMERA_ID: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePhoto();

                } else{
                    Toast.makeText(this, "Permiso necesario para acceder a la camara", Toast.LENGTH_LONG).show();
                }

            }
            break;

            case PERMISSION_GALLERY_ID: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent pickImage = new Intent(Intent.ACTION_PICK);
                    pickImage.setType("image/*");
                    startActivityForResult(pickImage, IMAGE_PICKER_REQUEST);

                } else{
                    Toast.makeText(this, "Permiso necesario para acceder a la galeria", Toast.LENGTH_LONG).show();
                }
            }
            break;
        }
    }
}
