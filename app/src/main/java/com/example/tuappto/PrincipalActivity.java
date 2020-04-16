package com.example.tuappto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import negocio.Client;
import negocio.Owner;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;


import android.content.Intent;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Pattern;


public class PrincipalActivity extends AppCompatActivity {

    public static final String PATH_CLIENTS="clients/";
    public static final String PATH_OWNERS="owners/";;
    private static final String TAG = "tag";
    Button buttonRegister;
    Button buttonEnter;
    Button buttonTemporal;
    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        database= FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        final EditText emailET = findViewById(R.id.editEmail);
        final EditText passwordET = findViewById(R.id.editPassword);

        buttonEnter = findViewById(R.id.buttonEnter);
        buttonRegister = findViewById(R.id.buttonRegister);
        buttonTemporal = findViewById(R.id.buttonTemporal);

        final FirebaseUser currentUser = mAuth.getCurrentUser();

        buttonEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInUser(emailET.getText().toString(),passwordET.getText().toString());
                //startActivity(new Intent(view.getContext(),SellerMenuActivity.class));

            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(),RegistryActivity.class);
                startActivity(intent);

            }
        });

        buttonTemporal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(view.getContext(),BuyerMenuActivity.class));
            }
        });


    }
    private void signInUser(String email, String password) {
        if (validateForm(email)) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        private static final String TAG = "tag";

                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
// Sign in success, update UI
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                if(!updateUIC(user)) {
                                    updateUIO(user);
                                }

                            } else {
// If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(PrincipalActivity.this, "correo o contraseñas incorrectas",
                                        Toast.LENGTH_SHORT).show();
                                updateUIC(null);
                            }
                        }
                    });
        }else{
            Toast.makeText(PrincipalActivity.this, "Correo incorrecto",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private boolean updateUIO(final FirebaseUser currentUser) {
        final boolean[] flag = {false};
        if (currentUser != null) {

            myRef = database.getReference(PATH_OWNERS);
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                        Owner myUser = singleSnapshot.getValue(Owner.class);
                        if (currentUser.getEmail() == myUser.getEmail()) {

                            Intent intent = new Intent(PrincipalActivity.this, SellerMenuActivity.class);
                            intent.putExtra("email", currentUser.getEmail());
                            startActivity(intent);
                            flag[0] = true;
                        }
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "error en la consulta", databaseError.toException());
                }
            });
        }
        return flag[0];
    }

    private boolean validateForm(String email) {

        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();

    }

    private boolean updateUIC(final FirebaseUser currentUser) {
        final boolean[] flag = {false};
        if (currentUser != null) {

            myRef = database.getReference(PATH_CLIENTS);
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                        Client myUser = singleSnapshot.getValue(Client.class);
                        if (myUser.getEmail() == currentUser.getEmail()) {

                            Intent intent = new Intent(PrincipalActivity.this, BuyerMenuActivity.class);
                            intent.putExtra("email", currentUser.getEmail());
                            startActivity(intent);
                            flag[0] = true;
                        }
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "error en la consulta", databaseError.toException());
                }
            });
        }
            return flag[0];

    }
}
