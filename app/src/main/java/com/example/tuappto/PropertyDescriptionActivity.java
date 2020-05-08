package com.example.tuappto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.Objects;
import negocio.Interest;


public class PropertyDescriptionActivity extends AppCompatActivity {

    private static FirebaseFirestoreSettings settings;
    private static StorageReference mStorageRef;
    public static final String PATH_INTEREST = "interest/";
    private DatabaseReference mDatabase;

    public Button buttonAdd;
    public Button buttonRemove;
    public Button buttonChat;
    public ImageView imageViewProperty;

    public TextView textViewPrice;
    public TextView textViewArea;
    public TextView textViewParking;
    public TextView textViewRooms;
    public TextView textViewDescription;
    public TextView textViewKind;

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
    public String myId;
    public double latitude;
    public double longitude;

    public Interest newInterest;
    private String key;
    private DatabaseReference myRef;
    private FirebaseUser fuser;

    private FirebaseFirestore db;
    public FirebaseAuth mAuth;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_description);

        db = FirebaseFirestore.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        settings = new FirebaseFirestoreSettings.Builder().build();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        database = FirebaseDatabase.getInstance();

        buttonAdd = findViewById(R.id.buttonAdd);
        buttonChat = findViewById(R.id.buttonChat);
        buttonRemove = findViewById(R.id.buttonRemove);
        imageViewProperty = findViewById(R.id.imageViewProperty);

        textViewKind = findViewById(R.id.textViewKindInformation);
        textViewPrice = findViewById(R.id.textViewPriceInformation);
        textViewArea = findViewById(R.id.textViewAreaInformation);
        textViewParking = findViewById(R.id.textViewParkingInformation);
        textViewRooms = findViewById(R.id.textViewRoomsInformation);
        textViewDescription = findViewById(R.id.textViewDescriptionInformation);

        myRef = database.getReference();
        mAuth = FirebaseAuth.getInstance();
        fuser = mAuth.getCurrentUser();


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
        propertyId = imagePath.substring(18,(imagePath.length()-4));

        textViewPrice.setText(String.valueOf(price));
        textViewArea.setText(String.valueOf(area));
        textViewParking.setText(String.valueOf(parking));
        textViewRooms.setText(String.valueOf(rooms));
        textViewDescription.setText(description);

        if(sellOrRent.equals("rent")){
            textViewKind.setText(R.string.on_rent_text);
        }
        else if(sellOrRent.equals("sell")){
            textViewKind.setText(R.string.on_sale_text);
        }
        else{
            textViewKind.setText(R.string.on_both);
        }

        downloadPhoto(imagePath, imageViewProperty);

        checkInterest();

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                key = myRef.push().getKey();
                createInterest();
                Toast.makeText(getApplicationContext(),"Se agregó a sus favoritos" , Toast.LENGTH_LONG).show();
                finish();
            }
        });

        buttonRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeInterest();
                Toast.makeText(getApplicationContext(),"Se elimino de sus favoritos" , Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    private void checkInterest() {
        myRef = database.getReference(PATH_INTEREST);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if(fuser.getUid().equals(Objects.requireNonNull(ds.child("clientId").getValue()).toString())&&
                            (ownerId.equals(Objects.requireNonNull(ds.child("owner").getValue()).toString()))&&
                            (propertyId.equals(Objects.requireNonNull(ds.child("property").getValue()).toString()))){
                        buttonAdd.setClickable(false);
                        buttonAdd.setVisibility(View.INVISIBLE);
                        buttonRemove.setClickable(true);
                        buttonRemove.setVisibility(View.VISIBLE);
                        myId = Objects.requireNonNull(ds.child("id").getValue()).toString();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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

    private void createInterest(){
        newInterest = new Interest();
        final String clientId = fuser.getUid();

        newInterest.setClientId(clientId);
        newInterest.setOwner(ownerId);
        newInterest.setId(key);
        newInterest.setProperty(propertyId);

        mDatabase.child("clients").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        if (clientId.equals(Objects.requireNonNull(Objects.requireNonNull(ds.getKey())))) {
                            newInterest.setClientName(Objects.requireNonNull(ds.child("name").getValue()).toString());
                            newInterest.setClientSecondName(Objects.requireNonNull(ds.child("secondName").getValue()).toString());
                            newInterest.setClientEmail(Objects.requireNonNull(ds.child("email").getValue()).toString());
                            newInterest.setClientPhone(Long.parseLong(Objects.requireNonNull(ds.child("phone").getValue()).toString()));
                            newInterest.setClientImagePath(Objects.requireNonNull(ds.child("imagePath").getValue()).toString());

                        }
                    }
                }

                myRef = database.getReference(PATH_INTEREST + key);
                myRef.setValue(newInterest);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void removeInterest(){
        myRef = database.getReference(PATH_INTEREST + myId);
        myRef.removeValue();
    }
}
