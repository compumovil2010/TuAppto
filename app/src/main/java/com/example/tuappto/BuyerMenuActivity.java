package com.example.tuappto;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.tuappto.listeners.onMarkerLongClickListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import negocio.Property;
import negocio.Utilidades;

public class BuyerMenuActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Location currentLocation;
    public Geocoder mGeocoder;

    private SensorManager sensorManager;
    private Sensor lightSensor;
    private SensorEventListener lightSensorListener;
    private LocationManager locationManager;
    public Button buttonViewProperties;
    public Button buttonMyFavourites;
    public Button buttonChats;
    public Button buttonDates;
    private FirebaseAuth mAuth;
    private Marker marcador;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    public static final String PATH_PROPERTY = "properties/";
    private static final int REQUEST_CODE = 101;
    ArrayList<Property> ofertas = new ArrayList<>();
    ArrayList<Marker> markers = new ArrayList<>();
    private Marker selectedmarker;
    JsonObjectRequest jsonObjectRequest;
    RequestQueue request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_menu);
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert supportMapFragment != null;
        supportMapFragment.getMapAsync(BuyerMenuActivity.this);
        BuyerMenuActivity context = this;
        mGeocoder = new Geocoder(getBaseContext());
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        mAuth = FirebaseAuth.getInstance();

        buttonViewProperties = findViewById(R.id.buttonViewProperties);
        buttonMyFavourites = findViewById(R.id.buttonMyFavourites);
        buttonChats = findViewById(R.id.buttonChats);
        buttonDates = findViewById(R.id.buttonDates);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        request= Volley.newRequestQueue(getApplicationContext());
        mGeocoder = new Geocoder(getBaseContext());

        buttonViewProperties.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(view.getContext(), SearchPropertyActivity.class));
            }
        });

        buttonMyFavourites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(view.getContext(), InterestListActivity.class));
            }
        });

        buttonChats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(view.getContext(), Chat2Activity.class));
            }
        });

        buttonDates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(view.getContext(), BuyerDatesActivity.class));
            }
        });

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        assert sensorManager != null;
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        lightSensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (mMap != null) {
                    if (event.values[0] < 1000) {
                        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(BuyerMenuActivity.this, R.raw.style_json_night));
                    } else {
                        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(BuyerMenuActivity.this, R.raw.style_json_day));
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logOut) {
            mAuth.signOut();
            startActivity(new Intent(BuyerMenuActivity.this, PrincipalActivity.class));
            finish();
            return true;
        }
        return false;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.example_menu, menu);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        fetchLocation();
        getPropertiesFromFirebase();
        mMap.setOnMarkerDragListener(new onMarkerLongClickListener(markers) {
            @Override
            public void onLongClickListener(Marker marker) {

            }
        });
    }

    private void actualizarUbicacion(Location location) {
        if (location != null) {
            currentLocation = location;
            agregarMarcador(currentLocation.getLatitude(), currentLocation.getLongitude());
        }
    }

    private void agregarMarcador(double lat, double lng) {
        LatLng latLng = new LatLng(lat, lng);
        CameraUpdate miUbicacion = CameraUpdateFactory.newLatLngZoom(latLng, 15);
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(latLng + " ");
        if (marcador != null) marcador.remove();
        marcador = mMap.addMarker(new MarkerOptions().position(latLng)
                .title("aquitoy"));
        mMap.animateCamera(miUbicacion);
    }

    private void actualizarPropiedades() {
        LatLng latLng;
        MarkerOptions markerOptions;
        Marker mark;
        for (Property propiedad : ofertas) {
            latLng = new LatLng(propiedad.getLocation().latitude, propiedad.getLocation().longitude);
            markerOptions = new MarkerOptions()
                    .position(latLng)
                    .title(propiedad.getSellOrRent() + ": " + propiedad.getPrice() + "$")
                    .draggable(true);
            mark = mMap.addMarker(markerOptions);
            markers.add(mark);
        }
        mMap.setOnMarkerDragListener(new onMarkerLongClickListener(markers) {
            @Override
            public void onLongClickListener(Marker marker) {
                selectedmarker = marker;
                showAlertDialogButtonClicked();
            }
        });
    }

    public void showAlertDialogButtonClicked() {

        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("What do you want to do?");

        // add a list
        String[] opciones = {"Go There!", "Show me the property!"};
        builder.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        Utilidades.coordenadas.setLatitudInicial(currentLocation.getLatitude());
                        Utilidades.coordenadas.setLongitudInicial(currentLocation.getLatitude());
                        Utilidades.coordenadas.setLatitudFinal(selectedmarker.getPosition().latitude);
                        Utilidades.coordenadas.setLongitudFinal(selectedmarker.getPosition().longitude);
/*
                        webServiceObtenerRuta(String.valueOf(currentLocation.getLatitude()),String.valueOf(currentLocation.getLatitude()),
                                String.valueOf(selectedmarker.getPosition().latitude),String.valueOf(selectedmarker.getPosition().longitude));

                        createRoutes();
*/
                    case 1:
                        Intent intent = new Intent(BuyerMenuActivity.this, Street_view.class );
                        intent.putExtra("latitud", selectedmarker.getPosition().latitude);
                        intent.putExtra("longitud", selectedmarker.getPosition().longitude);
                        startActivity(intent);
                }
            }
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {actualizarUbicacion(location);}
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
        @Override
        public void onProviderEnabled(String provider) {}
        @Override
        public void onProviderDisabled(String provider) {}
    };
    private  void getPropertiesFromFirebase(){
        myRef.child("properties").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot ds: dataSnapshot.getChildren()){
                        int parking = Integer.parseInt(Objects.requireNonNull(ds.child("parking").getValue()).toString());
                        int area = Integer.parseInt(Objects.requireNonNull(ds.child("area").getValue()).toString());
                        int price = Integer.parseInt(Objects.requireNonNull(ds.child("price").getValue()).toString());
                        int rooms = Integer.parseInt(Objects.requireNonNull(ds.child("rooms").getValue()).toString());
                        String kind = Objects.requireNonNull(ds.child("sellOrRent").getValue()).toString();
                        String imagePath = Objects.requireNonNull(ds.child("imagePath").getValue()).toString();
                        String description = Objects.requireNonNull(ds.child("description").getValue()).toString();
                        String ownerId = Objects.requireNonNull(ds.child("ownerId").getValue()).toString();
                        double latitude = Double.parseDouble(Objects.requireNonNull(ds.child("location").child("latitude").getValue()).toString());
                        double longitude = Double.parseDouble(Objects.requireNonNull(ds.child("location").child("longitude").getValue()).toString());
                        LatLng latLng = new LatLng(latitude, longitude);
                        String address = "";

                        Property aux = new Property();
                        aux.setSellOrRent(kind);
                        aux.setParking(parking);
                        aux.setArea(area);
                        aux.setPrice(price);
                        aux.setRooms(rooms);
                        aux.setImagePath(imagePath);
                        aux.setLocation(latLng);
                        aux.setDescription(description);
                        aux.setOwnerId(ownerId);
                        aux.setAddress(address);
                        ofertas.add(aux);
                    }
                    actualizarPropiedades();

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(lightSensorListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }
    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(lightSensorListener);
    }
    private void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        currentLocation = getLastBestLocation();
        actualizarUbicacion(currentLocation);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1100, 0, locationListener);
    }
    private Location getLastBestLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Location locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        long GPSLocationTime = 0;
        if (null != locationGPS) { GPSLocationTime = locationGPS.getTime(); }

        long NetLocationTime = 0;

        if (null != locationNet) {
            NetLocationTime = locationNet.getTime();
        }

        if ( 0 < GPSLocationTime - NetLocationTime ) {
            return locationGPS;
        }
        else {
            return locationNet;
        }
    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchLocation();
            } else {
                Toast.makeText(this, "Permiso necesario para acceder a la camara", Toast.LENGTH_LONG).show();
            }
        }
    }
}
