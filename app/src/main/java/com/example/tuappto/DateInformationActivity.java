package com.example.tuappto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DateInformationActivity extends AppCompatActivity {

    public Bundle bundle;
    public Intent intent;

    public int year;
    public int month;
    public int day;
    public int hour;
    public int min;
    public String address;
    public String name;
    public String secondName;

    private FirebaseFirestore db;
    private static FirebaseFirestoreSettings settings;
    private static StorageReference mStorageRef;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_information);

        db = FirebaseFirestore.getInstance();
        settings = new FirebaseFirestoreSettings.Builder().build();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        intent = getIntent();
        bundle = intent.getBundleExtra("bundle");

        assert bundle != null;
        year = bundle.getInt("year");
        month = bundle.getInt("month");
        day = bundle.getInt("day");
        hour = bundle.getInt("hour");
        min = bundle.getInt("min");
        address = bundle.getString("address");
        ImageView imageView = findViewById(R.id.imageViewUser);
        name = bundle.getString("name");
        secondName = bundle.getString("secondName");

        TextView textView = findViewById(R.id.textViewDateDescription);
        downloadPhoto(bundle.getString("imagePath"), imageView);

        textView.setText("Tienes una cita a las "+hour+":"+min+
                " el dia "+day+"/"+month+"/"+year+" en la direccion "+ address +" con " + name + " "+ secondName);

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
