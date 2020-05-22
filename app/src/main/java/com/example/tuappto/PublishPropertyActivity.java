package com.example.tuappto;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentActivity;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import negocio.Owner;
import negocio.Property;

public class PublishPropertyActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int PERMISSION_CAMERA_ID = 1;
    private static final int PERMISSION_GALLERY_ID = 2;
    private static final int REQUEST_IMAGE_CAPTURE = 3;
    private static final int IMAGE_PICKER_REQUEST = 4;
    public static final String PATH_PROPERTY = "properties/";
    public static final String PATH_OWNERS = "owners/";

    public ImageButton imageButtonCamera;
    public ImageButton imageButtonGallery;
    private ImageView imageViewUser;
    public Button buttonPublish;
    private CheckBox sell;
    private CheckBox rent;
    public Property newProperty;
    private Uri imageUri;

    private GoogleMap mMap;
    private Location currentLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location destiny;
    private Geocoder mGeocoder;
    private SensorManager sensorManager;
    private Sensor lightSensor;
    private SensorEventListener lightSensorListener;

    private int price;
    public LatLng location;
    private String sellOrRent;
    private int rooms;
    private int area;
    private int parking;
    private String description;
    private String key;


    private EditText editTextprice;
    private EditText editTextrooms;
    private EditText editTextarea;
    private EditText editTextparking;
    private EditText editTextdescription;

    private StorageReference mStorage;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseUser fuser;
    public FirebaseAuth mAuth;
    public Owner aux;

    private static final int REQUEST_CODE = 101;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_property);

        imageButtonCamera = findViewById(R.id.imageButtonCamera);
        imageButtonGallery = findViewById(R.id.imageButtonGallery);
        imageViewUser = findViewById(R.id.imageViewUser);
        buttonPublish = findViewById(R.id.buttonPublish);
        sell = findViewById(R.id.checkBox);
        rent = findViewById(R.id.checkBox2);
        mStorage = FirebaseStorage.getInstance().getReference();
        editTextprice = findViewById(R.id.editTextPrice);
        editTextrooms = findViewById(R.id.editTextRooms);
        editTextarea = findViewById(R.id.editTextArea);
        editTextparking = findViewById(R.id.editTextParking);
        editTextdescription = findViewById(R.id.editTextDescription);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        mAuth = FirebaseAuth.getInstance();

        fuser = mAuth.getCurrentUser();

        sensorManager= (SensorManager) getSystemService(SENSOR_SERVICE);
        assert sensorManager != null;
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        lightSensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (mMap != null) {
                    if (event.values[0] < 1000) {
                        //Log.i("MAPS", "DARK MAP " + event.values[0]);
                        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(PublishPropertyActivity.this,R.raw.style_json_night));
                    } else {
                        //Log.i("MAPS", "LIGHT MAP " + event.values[0]);
                        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(PublishPropertyActivity.this,R.raw.style_json_day));
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {}
        };
        imageButtonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(v.getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    takePhoto();
                }
                else {
                    requestPermission(PublishPropertyActivity.this, Manifest.permission.CAMERA, "Acceso a cámara necesario", PERMISSION_CAMERA_ID);
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
                    requestPermission(PublishPropertyActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE, "Leer almacenamiento es necesario", PERMISSION_GALLERY_ID);
                }
            }
        });

        buttonPublish.setOnClickListener(new View.OnClickListener() {

            //ACA VERIFICAR QUE SI SE TENGA UNA UBICACION, SINO DECIRLE
            @Override
            public void onClick(View view) {

                if(allFilled()) {

                    key = myRef.push().getKey();

                    price = Integer.parseInt(editTextprice.getText().toString());

                    if (sell.isChecked()&&!rent.isChecked()) {
                        sellOrRent = "sell";
                    } else if(!sell.isChecked()&&rent.isChecked()){
                        sellOrRent = "rent";
                    }
                    else{
                        sellOrRent = "both";
                    }

                    rooms = Integer.parseInt(editTextrooms.getText().toString());
                    area = Integer.parseInt(editTextarea.getText().toString());
                    parking = Integer.parseInt(editTextparking.getText().toString());
                    description = editTextdescription.getText().toString();

                    publishProperty();

                    finish();
                }


                else{
                    Toast.makeText(PublishPropertyActivity.this, "Complete todos los campos.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        sensorManager= (SensorManager) getSystemService(SENSOR_SERVICE);
        assert sensorManager != null;
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        lightSensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (mMap != null) {
                    if (event.values[0] < 1000) {
                        //Log.i("MAPS", "DARK MAP " + event.values[0]);
                        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(PublishPropertyActivity.this,R.raw.style_json_night));
                    } else {
                        //Log.i("MAPS", "LIGHT MAP " + event.values[0]);
                        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(PublishPropertyActivity.this,R.raw.style_json_day));
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {}
        };

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLocation();
        destiny = new Location("");
        mGeocoder = new Geocoder(getBaseContext());


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
            File photo = new File(getExternalFilesDir(null),"test.jpg");
            imageUri = FileProvider.getUriForFile(this,getPackageName()+".provider",photo);
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

                } else{
                    Toast.makeText(this, "Permiso necesario para acceder a la camara", Toast.LENGTH_LONG).show();
                }
            }
            case REQUEST_CODE:{
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchLocation();
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

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera

        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Aqui estamos!!!");
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        googleMap.addMarker(markerOptions);

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                try {
                    mMap.clear();
                    destiny.setLatitude(latLng.latitude);
                    destiny.setLongitude(latLng.longitude);
                    mMap.addMarker(new MarkerOptions().position(latLng)
                            .title(mGeocoder.getFromLocation(latLng.latitude, latLng.longitude, 1).get(0).getAddressLine(0)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(sensorManager != null){
            sensorManager.registerListener(lightSensorListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        fetchLocation();
    }
    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(lightSensorListener);
        fetchLocation();
    }

    private void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    //Toast.makeText(getApplicationContext(), currentLocation.getLatitude() + "" + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                    SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                    assert supportMapFragment != null;
                    supportMapFragment.getMapAsync(PublishPropertyActivity.this);
                }
            }
        });
    }

    private boolean allFilled() {
        return !editTextprice.getText().toString().isEmpty()||
                !editTextrooms.getText().toString().isEmpty()||
                !editTextarea.getText().toString().isEmpty()||
                !editTextparking.getText().toString().isEmpty()||
                !editTextdescription.getText().toString().isEmpty()||
                (sell.isChecked()||rent.isChecked());
    }

    private void publishProperty(){

        newProperty = new Property();

        //Aca se pone la ubicacion
        location = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        newProperty.setArea(area);
        newProperty.setDescription(description);
        newProperty.setParking(parking);
        newProperty.setPrice(price);
        newProperty.setRooms(rooms);
        newProperty.setSellOrRent(sellOrRent);

        //Aca se guarda la ubicacion
        newProperty.setLocation(location);
        newProperty.setOwnerId(fuser.getUid());
        newProperty.setImagePath("Images/Properties/" + key + ".jpg");

        myRef = database.getReference(PATH_PROPERTY + key);
        myRef.setValue(newProperty);

        if (imageUri != null) { // aca en storage
            StorageReference folder = mStorage.child("Images").child("Properties").child(key +".jpg");
            folder.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.i("FBApp", "Succesfully upload image");
                }
            });
        }

        myRef = database.getReference(PATH_OWNERS).child(fuser.getUid());

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                aux = dataSnapshot.getValue(Owner.class);
                assert aux != null;
                List<String>properties = aux.getProperties();

                if(properties.size() == 1 && properties.get(0).equals("null")){
                    properties.remove(0);
                    properties.add(key);
                    aux.setProperties(properties);
                    myRef = database.getReference(PATH_OWNERS).child(fuser.getUid());
                    myRef.setValue(aux);
                }
                else {
                    properties.add(key);
                    aux.setProperties(properties);
                    myRef = database.getReference(PATH_OWNERS).child(fuser.getUid());
                    myRef.setValue(aux);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}

