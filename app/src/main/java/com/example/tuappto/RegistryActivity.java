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
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import static com.example.tuappto.R.id.imageViewUser;

public class RegistryActivity extends AppCompatActivity {

    private static final int PERMISSION_CAMERA_ID = 1;
    private static final int PERMISSION_GALLERY_ID = 2;
    private static final int REQUEST_IMAGE_CAPTURE = 3;
    private static final int IMAGE_PICKER_REQUEST = 4;
    private static final int WRITE_EXTERNAL_STORAGE = 5;

    ImageButton imageButtonCamera;
    ImageButton imageButtonGallery;
    ImageView imageViewUser;
    Button buttonContinue;
    String filePath;

    TextView name;
    TextView user;
    TextView password;
    TextView phone;
    TextView secondName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registry);

        buttonContinue = findViewById(R.id.buttonContinue);
        imageButtonCamera = findViewById(R.id.imageButtonCamera);
        imageButtonGallery = findViewById(R.id.imageButtonGallery);
        imageViewUser = findViewById(R.id.imageViewUser);

        name = findViewById(R.id.editTextName);
        user = findViewById(R.id.editTextUser);
        phone = findViewById(R.id.editTextPhone);
        password = findViewById(R.id.editTextPassword);
        secondName = findViewById(R.id.editTextSecondName);

        buttonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                imageViewUser.buildDrawingCache();
                Bitmap bitmap = imageViewUser.getDrawingCache();

                if(allFilled()){
                    if(emailValidation(user.getText().toString())){
                        if(password.getText().toString().length()>5){
                            Intent i = new Intent(view.getContext(),ChooseActivity.class);
                            i.putExtra("name",name.getText().toString());
                            i.putExtra("user",user.getText().toString());
                            i.putExtra("phone",phone.getText().toString());
                            i.putExtra("password",password.getText().toString());
                            i.putExtra("secondName",secondName.getText().toString());

                            if(filePath!=null) {
                                i.putExtra("imagePath", filePath);
                            }else{
                                i.putExtra("imagePath", "");
                            }
                            //saveImage();
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                        }else{
                            Toast.makeText(RegistryActivity.this, "la contraseña debe tener mas de 5 caracteres.",
                                    Toast.LENGTH_SHORT).show();

                        }

                    }else{
                        Toast.makeText(RegistryActivity.this, "ingrese un email valido.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(RegistryActivity.this, "Complete todos los campos.",
                            Toast.LENGTH_SHORT).show();
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
                    requestPermission(RegistryActivity.this, Manifest.permission.CAMERA, "Acceso a cámara necesario", PERMISSION_CAMERA_ID);
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
                    requestPermission(RegistryActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE, "Leer almacenamiento es necesario", PERMISSION_GALLERY_ID);
                }
            }
        });
    }



    private boolean emailValidation(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }
    private boolean allFilled() {
        return !name.getText().toString().isEmpty()||
                !user.getText().toString().isEmpty()||
                !phone.getText().toString().isEmpty()||
                !password.getText().toString().isEmpty()||
                !secondName.getText().toString().isEmpty();
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
                            imageViewUser.setImageBitmap(imageBitmap);
                            permisionSave();


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
                            imageViewUser.setImageBitmap(selectedImage);
                            filePath =imageUri.getPath();
                        }

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            case WRITE_EXTERNAL_STORAGE:
                if (resultCode == RESULT_OK){
                    try {
                        SaveImage();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

        }

    }

    private void permisionSave() throws IOException {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            SaveImage();
        }
        else {
            requestPermission(RegistryActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE, "Acceso a cámara necesario", WRITE_EXTERNAL_STORAGE);
        }
    }

    private void SaveImage() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
         filePath = image.getAbsolutePath();
        ImageView ivPerfil = findViewById(R.id.imageViewUser);
        ivPerfil.setTag(Uri.parse(filePath));
        filePath = ivPerfil.getTag().toString();

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
