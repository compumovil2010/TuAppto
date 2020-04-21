package com.example.tuappto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import negocio.Client;
import negocio.Owner;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChooseActivity extends AppCompatActivity {

    Owner newOwner;
    Client newClient;
    String email;
    String password;
    String name;
    String secondName;
    String imagePath;
    Long phone;

    Button duenio;
    Button cliente;
    Bundle bundle;

    private FirebaseAuth mAuth;

    FirebaseDatabase database;
    DatabaseReference myRef;

    public static final String PATH_CLIENTS="clients/";
    public static final String PATH_OWNERS="owners/";
    private String tipo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);

        mAuth = FirebaseAuth.getInstance();
        database= FirebaseDatabase.getInstance();

        bundle = getIntent().getBundleExtra("bundle");
        email = bundle.getString("email");
        password = bundle.getString("password");
        name = bundle.getString("name");
        secondName = bundle.getString("secondName");
        phone = bundle.getLong("phone");
        imagePath= getIntent().getStringExtra("imagePath");


        duenio = findViewById(R.id.buttonOwner);
        cliente = findViewById(R.id.buttonClient);

        duenio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tipo = "duenio";
                crearUsuario();
            }
        });
        cliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tipo="cliente";
                crearUsuario();
            }
        });
    }

    private void crearUsuario() {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    private static final String TAG = "tag";

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser fuser = mAuth.getCurrentUser();
                            if (tipo.equals("duenio")){
                                createOwner(fuser);
                            }
                            else{
                                createClient(fuser);
                            }

                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(ChooseActivity.this, "Authentication failed."+ email +password,
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }

    private void createClient(FirebaseUser fuser) {
        newClient = new Client();
        newClient.setEmail(email);
        newClient.setPassword(password);
        newClient.setName(name);
        newClient.setSecondname(secondName);
        newClient.setPhone(phone);

        myRef = database.getReference(PATH_CLIENTS + fuser.getUid());
        myRef.setValue(newClient);

        Intent intent = new Intent(getBaseContext(), BuyerMenuActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("email", newClient.getEmail());
        startActivity(intent);
        finish();

    }

    private void createOwner(FirebaseUser fuser) {
        newOwner = new Owner();
        newOwner.setEmail(email);
        newOwner.setPassword(password);
        newOwner.setName(name);
        newOwner.setSecondname(secondName);
        newOwner.setPhone(phone);

        myRef = database.getReference(PATH_OWNERS+fuser.getUid());
        myRef.setValue(newOwner);

        Intent intent = new Intent(getBaseContext(), SellerMenuActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("email", newOwner.getEmail());
        startActivity(intent);
        finish();
    }

}
