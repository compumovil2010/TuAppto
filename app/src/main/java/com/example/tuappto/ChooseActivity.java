package com.example.tuappto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import negocio.Client;
import negocio.Owner;

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

    Owner nuevoDuenio;
    Client nuevoCliente;
    String user;
    String password;
    String name;
    String secondName;
    int phone;

    Button duenio;
    Button cliente;
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

        user = getIntent().getStringExtra("user");
        password = getIntent().getStringExtra("password");
        name = getIntent().getStringExtra("name");
        secondName = getIntent().getStringExtra("secondName");
        phone = Integer.parseInt(getIntent().getStringExtra("phone"));


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
        mAuth.createUserWithEmailAndPassword(user, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    private static final String TAG = "tag";

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser fuser = mAuth.getCurrentUser();
                            if (tipo=="duenio"){
                                crearDuenio(fuser);
                            }else{
                                crearCliente(fuser);
                            }

                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(ChooseActivity.this, "Authentication failed."+ user+password,
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }

    private void crearCliente(FirebaseUser fuser) {
        nuevoCliente = new Client();
        nuevoCliente.setEmail(user);
        nuevoCliente.setPassword(password);
        nuevoCliente.setName(name);
        nuevoCliente.setSecondname(secondName);
        nuevoCliente.setPhone(phone);

        myRef=database.getReference(PATH_CLIENTS+fuser.getUid());
        myRef.setValue(nuevoCliente);

    }

    private void crearDuenio(FirebaseUser fuser) {
        nuevoDuenio = new Owner();
        nuevoDuenio.setEmail(user);
        nuevoDuenio.setPassword(password);
        nuevoDuenio.setName(name);
        nuevoDuenio.setSecondname(secondName);
        nuevoDuenio.setPhone(phone);

        myRef=database.getReference(PATH_OWNERS+fuser.getUid());
        myRef.setValue(nuevoDuenio);
    }
}
