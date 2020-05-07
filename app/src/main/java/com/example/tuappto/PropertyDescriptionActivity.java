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

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class PropertyDescriptionActivity extends AppCompatActivity {

    private static FirebaseFirestoreSettings settings;
    private static StorageReference mStorageRef;

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
    public double latitude;
    public double longitude;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_description);

        db = FirebaseFirestore.getInstance();
        settings = new FirebaseFirestoreSettings.Builder().build();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        buttonAdd = findViewById(R.id.buttonAdd);
        buttonChat = findViewById(R.id.buttonChat);
        buttonRemove = findViewById(R.id.buttonRemove);
        imageViewProperty = findViewById(R.id.imageViewProperty);

        textViewKind = findViewById(R.id.textViewKind);
        textViewPrice = findViewById(R.id.textViewPriceInformation);
        textViewArea = findViewById(R.id.textViewAreaInformation);
        textViewParking = findViewById(R.id.textViewParkingInformation);
        textViewRooms = findViewById(R.id.textViewRoomsInformation);
        textViewDescription = findViewById(R.id.textViewDescriptionInformation);


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

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(view.getContext(),Chat2Activity.class));
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
}
