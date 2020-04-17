package com.example.tuappto;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.tuappto.data.Owner;
import com.example.tuappto.data.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChooseActivity extends AppCompatActivity {

    Button buttonUser;
    Button buttonOwner;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference myRef;
    public static final String PATH_USERS = "users/";
    public static final String PATH_OWNERS = "owners/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);
        buttonUser = findViewById(R.id.button2);
        buttonOwner = findViewById(R.id.buttonOwner);
        final String valorCorreo = getIntent().getStringExtra("correo");
        final String valorNombre = getIntent().getStringExtra("nombre");
        final String valorApellido = getIntent().getStringExtra("apellido");
        final String valorContraseña = getIntent().getStringExtra("contraseña");
        final String valorTelefono = getIntent().getStringExtra("telefono");
        System.out.println(valorCorreo);

        buttonUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(),PrincipalActivity.class);
                myRef = database.getReference(PATH_USERS+mAuth.getUid());
                startActivity(intent);
                User user = new User(valorNombre,valorApellido,valorCorreo,valorContraseña,valorTelefono);
                myRef.setValue(user);
            }
        });
        buttonOwner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(),PrincipalActivity.class);
                myRef = database.getReference(PATH_OWNERS+mAuth.getUid());
                startActivity(intent);
                Owner owner = new Owner(valorNombre,valorApellido,valorCorreo,valorContraseña,valorTelefono);
                myRef.setValue(owner);
            }
        });


    }



}
