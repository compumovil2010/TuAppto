package com.example.tuappto;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import negocio.Appointment;
import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.Objects;

public class DateActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String PATH_APPOINMENTS = "appoinments/" ;
    EditText etPlannedDate;
    int dayA;
    int monthA;
    int yearA;
    private GoogleMap mMap;
    private Location currentLocation;
    public Geocoder mGeocoder;
    private DatabaseReference mDatabase;

    public FirebaseAuth mAuth;
    private FirebaseDatabase database;
    public DatabaseReference myRef;
    FirebaseUser currentUser;

    private SensorManager sensorManager;
    private Sensor lightSensor;
    private SensorEventListener lightSensorListener;
    private LatLng latLng;
    Marker marcador;

    public Button buttonContinue;
    private TimePicker timePicker1;

    public Intent intent;
    public Bundle bundle;

    public String clientImagePath;
    public String clientId;
    public String clientName;
    public String clientSecondName;
    public String property;
    public long clientPhone;

    public String ownerImagePath;
    public String ownerName;
    public String ownerSecondName;
    public long ownerPhone;


    private static final int REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date);

        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        timePicker1 = findViewById(R.id.timePicker1);

        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert supportMapFragment != null;
        supportMapFragment.getMapAsync(DateActivity.this);

        mGeocoder = new Geocoder(getBaseContext());

        etPlannedDate = findViewById(R.id.etPlannedDate);
        etPlannedDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.etPlannedDate) {
                    showDatePickerDialog();
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
                        Log.i("MAPS", "DARK MAP " + event.values[0]);
                        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(DateActivity.this,R.raw.style_json_night));
                    } else {
                        Log.i("MAPS", "LIGHT MAP " + event.values[0]);
                        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(DateActivity.this,R.raw.style_json_day));
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {}
        };

        buttonContinue = findViewById(R.id.buttonContinueDate);
        buttonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                crearCita();
                finish();
            }
        });

        intent = getIntent();
        bundle = intent.getBundleExtra("bundle");

        assert bundle != null;
        clientSecondName = bundle.getString("clientSecondName");
        clientName = bundle.getString("clientName");
        clientImagePath = bundle.getString("clientImagePath");
        clientId = bundle.getString("clientId");
        clientPhone = bundle.getLong("clientPhone");

    }

    private void crearCita() {
        int hour = timePicker1.getCurrentHour();
        int min = timePicker1.getCurrentMinute();
        LatLng location = new LatLng(74.78,74.45);
        final Appointment appointment = new Appointment();

        appointment.setDay(dayA);
        appointment.setMonth(monthA);
        appointment.setYear(yearA);
        appointment.setMin(min);
        appointment.setHour(hour);
        appointment.setLocation(location);
        appointment.setOwner(currentUser.getUid());
        appointment.setClient(clientId);
        appointment.setClientImagePath(clientImagePath);
        appointment.setClientName(clientName);
        appointment.setClientSecondName(clientSecondName);
        appointment.setClientPhone(clientPhone);

        final String aux = currentUser.getUid();

        mDatabase.child("owners").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        if (aux.equals(Objects.requireNonNull(ds.getKey()))) {
                            ownerName = Objects.requireNonNull(ds.child("name").getValue()).toString();
                            ownerSecondName = Objects.requireNonNull(ds.child("secondName").getValue()).toString();
                            ownerPhone = Long.parseLong(Objects.requireNonNull(ds.child("phone").getValue()).toString());
                            ownerImagePath = Objects.requireNonNull(ds.child("imagePath").getValue()).toString();

                        }
                    }
                }

                appointment.setOwnerImagePath(ownerImagePath);
                appointment.setOwnerName(ownerName);
                appointment.setOwnerSecondName(ownerSecondName);
                appointment.setOwnerPhone(ownerPhone);

                myRef = database.getReference(PATH_APPOINMENTS + database.getReference().push().getKey() );
                myRef.setValue(appointment);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void showDatePickerDialog() {

        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                // +1 because January is zero
                dayA = day;
                monthA = month + 1;
                yearA = year;
                final String selectedDate = day + " / " + (month+1) + " / " + year;
                etPlannedDate.setText(selectedDate);
            }
        });
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        fetchLocation();

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng1) {
                latLng = latLng1;
                try {
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(latLng)
                            .title(mGeocoder.getFromLocation(latLng.latitude, latLng.longitude, 1).get(0).getAddressLine(0)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }
    private void actualizarUbicacion(Location location){
        if(location != null){
            currentLocation = location;
            agregarMarcador(location.getLatitude(), location.getLongitude());
        }
    }
    private void agregarMarcador(double lat, double lng){
        LatLng latLng = new LatLng(lat, lng);
        CameraUpdate miUbicacion = CameraUpdateFactory.newLatLngZoom(latLng, 15);
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(latLng + " ");
        if(marcador!=null)marcador.remove();
        marcador=mMap.addMarker(new MarkerOptions().position(latLng)
                .title("aquitoy"));
        mMap.animateCamera(miUbicacion);
    }
    private void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        actualizarUbicacion(currentLocation);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 150000, 0, locationListener );
    }

    LocationListener locationListener = new LocationListener() {
        @Override public void onLocationChanged(Location location) {actualizarUbicacion(location);}
        @Override public void onStatusChanged(String provider, int status, Bundle extras) {}
        @Override public void onProviderEnabled(String provider) {}
        @Override public void onProviderDisabled(String provider) {}
    };
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchLocation();
            } else {
                Toast.makeText(this, "Permiso necesario para acceder a la camara", Toast.LENGTH_LONG).show();
            }
        }
    }
    private void requestPermission(Activity context) {

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(context, Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(context, "Acceso a localizacion necesario", Toast.LENGTH_SHORT).show();
            }
            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, DateActivity.REQUEST_CODE);
        }
    }
}
