package com.example.tuappto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import negocio.Property;

public class EditActivity extends AppCompatActivity {

    private static final int PERMISSION_CAMERA_ID = 1;
    private static final int PERMISSION_GALLERY_ID = 2;
    private static final int REQUEST_IMAGE_CAPTURE = 3;
    private static final int IMAGE_PICKER_REQUEST = 4;
    public static final String PATH_PROPERTY = "properties/";
    private static FirebaseFirestoreSettings settings;
    private static StorageReference mStorageRef;

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
    public Button buttonSave;
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
    public String propertyId;
    public String ownerId;
    public double latitude;
    public double longitude;

    private FirebaseFirestore db;
    private StorageReference mStorage;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        db = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();
        settings = new FirebaseFirestoreSettings.Builder().build();
        mStorageRef = FirebaseStorage.getInstance().getReference();

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
        buttonSave = findViewById(R.id.buttonSave);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

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
        ownerId = bundle.getString("ownerId");
        location = new LatLng(latitude, longitude);

        editTextPrice.setText(String.valueOf(price));
        editTextArea.setText(String.valueOf(area));
        editTextRooms.setText(String.valueOf(rooms));
        editTextParking.setText(String.valueOf(parking));
        editTextDescription.setText(description);

        downloadPhoto(imagePath, imageViewProperty);

        if(sellOrRent.equals("rent")){
            checkBoxRent.setChecked(true);
        }
        else if(sellOrRent.equals("sell")){
            checkBoxRent.setChecked(true);
        }
        else{
            checkBoxSell.setChecked(true);
            checkBoxRent.setChecked(true);
        }

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(allFilled()){
                    if(isChanged()){
                        propertyId = imagePath.substring(18,(imagePath.length()-4));
                        upLoadImage();
                        change();
                        finish();
                    }
                }
            }
        });

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
            takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            File foto = new File(getExternalFilesDir(null),"test.jpg");
            imageUri = FileProvider.getUriForFile(this,getPackageName()+".provider",foto);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE:
                if (resultCode == RESULT_OK) {
                    try {
                        Bitmap bitmap = BitmapFactory.decodeFile(getExternalFilesDir(null)+"/test.jpg");

                        imageViewProperty.setImageBitmap(bitmap);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return;

            case IMAGE_PICKER_REQUEST:
                if (resultCode == RESULT_OK) {
                    try {
                        imageUri = data.getData();
                        final InputStream imageStream;
                        if (imageUri != null) {
                            imageStream = getContentResolver().openInputStream(imageUri);
                            Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
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

    private void downloadPhoto(String ruta, final ImageView iv) {
        db.setFirestoreSettings(settings);
        StorageReference photoRef = mStorageRef.child(ruta);
        final long ONE_MEGABYTE = 1024 * 1024 * 10; //(1024 bytes = 1 KB) x (1024 = 1 MB) x 1 = 1 MB
        photoRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>()
        {
            @Override
            public void onSuccess(byte[] bytes) {
                iv.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }

    private boolean isChanged() {
        return !editTextPrice.getText().toString().equals(String.valueOf(price)) ||
                !editTextArea.getText().toString().equals(String.valueOf(area)) ||
                !editTextRooms.getText().toString().equals(String.valueOf(rooms)) ||
                !editTextParking.getText().toString().equals(String.valueOf(parking)) ||
                !editTextDescription.getText().toString().equals(String.valueOf(description)) ||
                (imageUri == null);
    }

    private void upLoadImage(){
        if (imageUri != null) { // aca en storage
            StorageReference folder = mStorage.child(imagePath);
            folder.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.i("FBApp", "Succesfully upload image");
                }
            });
        }
    }

    private void change(){
        Property newProperty = new Property();
        newProperty.setArea(Integer.parseInt(editTextArea.getText().toString()));
        newProperty.setDescription(editTextDescription.getText().toString());
        newProperty.setParking(Integer.parseInt(editTextParking.getText().toString()));
        newProperty.setPrice(Integer.parseInt(editTextPrice.getText().toString()));
        newProperty.setRooms(Integer.parseInt(editTextRooms.getText().toString()));

        if(checkBoxSell.isChecked() && checkBoxRent.isChecked()){
            newProperty.setSellOrRent("both");
        }
        else if(!checkBoxSell.isChecked() && checkBoxRent.isChecked()){
            newProperty.setSellOrRent("rent");
        }
        else{
            newProperty.setSellOrRent("sell");
        }

        newProperty.setLocation(location);
        newProperty.setOwnerId(ownerId);
        newProperty.setImagePath(imagePath);

        myRef = database.getReference(PATH_PROPERTY + propertyId);
        myRef.setValue(newProperty);

    }

    private boolean allFilled() {
        return !editTextPrice.getText().toString().isEmpty()||
                !editTextRooms.getText().toString().isEmpty()||
                !editTextArea.getText().toString().isEmpty()||
                !editTextParking.getText().toString().isEmpty()||
                !editTextDescription.getText().toString().isEmpty()||
                (checkBoxRent.isChecked()||checkBoxSell.isChecked());
    }
}
