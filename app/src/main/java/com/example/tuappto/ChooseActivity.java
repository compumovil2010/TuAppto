package com.example.tuappto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import negocio.Client;
import negocio.Owner;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ChooseActivity extends AppCompatActivity {

    public Bundle bundle;
    public Button duenio;
    public Button cliente;
    public Client newClient;
    public Intent intent;
    private Long phone;
    public Owner newOwner;
    private String email;
    private String password;
    private String name;
    private String secondName;
    private String country;
    private Uri imageUri;
    public InputStream imageStream;
    private FirebaseAuth mAuth;
    private StorageReference mStorage;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    public static final String PATH_CLIENTS="clients/";
    public static final String PATH_OWNERS="owners/";
    private String tipo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        bundle = getIntent().getBundleExtra("bundle");
        assert bundle != null;
        email = bundle.getString("email");
        password = bundle.getString("password");
        name = bundle.getString("name");
        secondName = bundle.getString("secondName");
        phone = bundle.getLong("phone");
        country = bundle.getString("country");

        intent = getIntent();
        imageUri = intent.getParcelableExtra("uri");
        try {
            imageStream = getContentResolver().openInputStream(imageUri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        mStorage = FirebaseStorage.getInstance().getReference();
        duenio = findViewById(R.id.buttonOwner);
        cliente = findViewById(R.id.buttonClient);

        duenio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tipo = "duenio";
                createUser();
            }
        });
        cliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tipo="cliente";
                createUser();
            }
        });
    }

    private void createUser() {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    private static final String TAG = "tag";

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser fuser = mAuth.getCurrentUser();
                            if (tipo.equals("duenio")){
                                assert fuser != null;
                                createOwner(fuser);
                            }
                            else{
                                assert fuser != null;
                                createClient(fuser);
                            }

                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(ChooseActivity.this, "Authentication failed."+ email +password,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void createClient(FirebaseUser fuser) {
        newClient = new Client();
        newClient.setCountry(country);
        newClient.setEmail(email);
        newClient.setPassword(password);
        newClient.setName(name);
        newClient.setSecondName(secondName);
        newClient.setPhone(phone);
        newClient.setImagePath("Images/Clients/" + fuser.getUid() + ".jpg");

        myRef = database.getReference(PATH_CLIENTS + fuser.getUid());
        myRef.setValue(newClient);

        if(imageUri != null) {
            StorageReference folder = mStorage.child("Images").child("Clients").child(fuser.getUid()+".jpg");
            folder.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.i("FBApp", "Succesfully upload image");
                }
            });
        }

        Intent intent = new Intent(getBaseContext(), BuyerMenuActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("email", newClient.getEmail());
        startActivity(intent);
        finish();

    }

    private void createOwner(FirebaseUser fuser) {
        newOwner = new Owner();
        List<String> aux = new ArrayList<>();
        aux.add("null");
        newOwner.setCountry(country);
        newOwner.setEmail(email);
        newOwner.setPassword(password);
        newOwner.setName(name);
        newOwner.setSecondName(secondName);
        newOwner.setPhone(phone);
        newOwner.setProperties(aux);
        newOwner.setImagePath("Images/Owners/" + fuser.getUid() + ".jpg");

        myRef = database.getReference(PATH_OWNERS + fuser.getUid());
        myRef.setValue(newOwner);

        if(imageUri != null) {
            StorageReference folder = mStorage.child("Images").child("Owners").child(fuser.getUid()+".jpg");
            folder.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.i("FBApp", "Succesfully upload image");
                }
            });
        }

        Intent intent = new Intent(getBaseContext(), SellerMenuActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("email", newOwner.getEmail());
        startActivity(intent);
        finish();
    }

}