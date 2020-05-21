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
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

public class RegistryActivity extends AppCompatActivity {

    private static final int PERMISSION_CAMERA_ID = 1;
    private static final int PERMISSION_GALLERY_ID = 2;
    private static final int REQUEST_IMAGE_CAPTURE = 3;
    private static final int IMAGE_PICKER_REQUEST = 4;
    private static final int PERMISSION_INTERNET = 5;
    private EditText email;
    private EditText name;
    private EditText password;
    private EditText phone;
    private EditText secondName;
    private ImageView imageViewUser;
    private Uri imageUri;
    public ImageButton imageButtonCamera;
    public ImageButton imageButtonGallery;
    public Button buttonContinue;
    Spinner sItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registry);

        buttonContinue = findViewById(R.id.buttonContinue);
        imageButtonCamera = findViewById(R.id.imageButtonCamera);
        imageButtonGallery = findViewById(R.id.imageButtonGallery);
        imageViewUser = findViewById(R.id.imageViewUser);
        name = findViewById(R.id.editTextName);
        email = findViewById(R.id.editTextUser);
        phone = findViewById(R.id.editTextPhone);
        password = findViewById(R.id.editTextPassword);
        secondName = findViewById(R.id.editTextSecondName);
        sItems = (Spinner) findViewById(R.id.spinner);



        buttonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(allFilled()){
                    if(emailValidation(email.getText().toString())){
                        if(password.getText().toString().length() > 5){
                            Bundle bundle = new Bundle();
                            Intent i = new Intent(view.getContext(),ChooseActivity.class);

                            bundle.putString("name",name.getText().toString());
                            bundle.putString("email",email.getText().toString());
                            bundle.putLong("phone",Long.parseLong(phone.getText().toString()));
                            bundle.putString("password",password.getText().toString());
                            bundle.putString("secondName",secondName.getText().toString());
                            bundle.putString("country",sItems.getSelectedItem().toString());

                            i.putExtra("bundle",bundle);
                            if(imageUri != null) {
                                i.putExtra("uri", imageUri);
                            }

                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                        }
                        else{
                            Toast.makeText(RegistryActivity.this, "la contraseña debe tener mas de 5 caracteres.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        Toast.makeText(RegistryActivity.this, "ingrese un email valido.", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(RegistryActivity.this, "Complete todos los campos.", Toast.LENGTH_SHORT).show();
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
        sItems.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (ContextCompat.checkSelfPermission(view.getContext(), Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
                    Log.i("permiso","permiso internet otorgado");
                    fillSpiner();
                }
                else {
                    requestPermission(RegistryActivity.this, Manifest.permission.INTERNET, "Acceso a internet necesario", PERMISSION_INTERNET);
                }
                return false;
            }
        });
    }

    private void fillSpiner() {
        consultarPaisesRest();
        }

    private String consultarPaisesRest() {
        RequestQueue queue = Volley.newRequestQueue(this);

        String url = "https://restcountries.eu/rest/v2";
        String query = "?fields=name;";
        StringRequest req = new StringRequest(Request.Method.GET, url+query,
                new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {
                        List<String> paises = new ArrayList<>();
                        paises.add("Seleccione su pais de procedencia");
                        String data = (String) response;
                        JsonParser parser  = new JsonParser();
                        JsonElement notes  = parser.parse(data);
                        JsonArray notesArr = notes.getAsJsonArray();
                        for (int i = 0; i < notesArr.size(); i++) {
                            // get your jsonobject
                            JsonObject obj = notesArr.get(i).getAsJsonObject();

                            // do the same for the rest of the elements like date , author ,authorId
                            String name;

                            // fetch data from object
                            name   = obj.get("name").getAsString();
                            // Store these values in list or objects you want
                            paises.add(name);
                            Log.i("datos",name);

                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(RegistryActivity.this, android.R.layout.simple_spinner_item, paises);

                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        sItems.setAdapter(adapter);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("TAG", "Error handling rest invocation"+error.getCause());
                    }
                }
        );
        queue.add(req);
        return "";

    }

    private boolean emailValidation(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

    private boolean allFilled() {
        return !name.getText().toString().isEmpty()||
                !email.getText().toString().isEmpty()||
                !phone.getText().toString().isEmpty()||
                !password.getText().toString().isEmpty()||
                !secondName.getText().toString().isEmpty()||
                sItems.getSelectedItem().toString()!="Seleccione su pais de procedencia"||
                sItems.getSelectedItem()!=null;
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

                        imageViewUser.setImageBitmap(bitmap);

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
                            imageViewUser.setImageBitmap(selectedImage);
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
                } else {
                    Toast.makeText(this, "Permiso necesario para acceder a la camara", Toast.LENGTH_LONG).show();
                }
            }
            break;

            case PERMISSION_GALLERY_ID: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent pickImage = new Intent(Intent.ACTION_PICK);
                    pickImage.setType("image/*");
                    startActivityForResult(pickImage, IMAGE_PICKER_REQUEST);

                } else {
                    Toast.makeText(this, "Permiso necesario para acceder a la galeria", Toast.LENGTH_LONG).show();
                }
            }
            break;

            case PERMISSION_INTERNET: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fillSpiner();
                } else {
                    Toast.makeText(this, "Permiso necesario para registrar su perfil", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
