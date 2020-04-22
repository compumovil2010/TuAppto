package com.example.tuappto;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final double lowerLeftLatitude= 1.396967;
    private static final double lowerLeftLongitude= -78.903968;
    private static final double upperRightLatitude= 11.983639;
    private static final double upperRigthLongitude= -71.869905;
    private static final double RADIUS_OF_EARTH_KM = 6371;
    private static final int PERMISSION_LOCATION_ID = 1;
    private static final int REQUEST_CHECK_SETTINGS = 2;

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;

    SensorManager sensorManager;
    Sensor lightSensor;
    SensorEventListener lightSensorListener;
    EditText mAddress;
    Marker myMarker;
    Marker myMarker2;
    LatLng dis1;
    LatLng dis2;
    LatLng dis3;
    double latitud;
    double longitud;
    private Polyline mLine;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this); // Esto llama al metodo onMapReady

        opcionesSensorLuz();

        mAddress = findViewById(R.id.texto);

        buscarPorTexto();

    }

    public void opcionesSensorLuz(){
        lightSensorListener = new SensorEventListener()  {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (mMap != null) {
                    if (event.values[0] < 20000) {
                        Log.i("MAPS", "DARK MAP " + event.values[0]);
                        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(MapsActivity.this, R.raw.styledark));
                    } else {
                        Log.i("MAPS", "LIGHT MAP " + event.values[0]);
                        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(MapsActivity.this, R.raw.stylelight));
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {}
        };
    }

    public void buscarPorTexto(){

        mAddress.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH){
                    String addressString = mAddress.getText().toString();
                    if (!addressString.isEmpty()) {
                        try {
                            Geocoder mGeocoder = new Geocoder(getBaseContext());
                            List<Address> addresses = mGeocoder.getFromLocationName( addressString, 2,
                                    lowerLeftLatitude, lowerLeftLongitude,
                                    upperRightLatitude, upperRigthLongitude);
                            if (addresses != null && !addresses.isEmpty()) {
                                Address addressResult = addresses.get(0);
                                LatLng position = new LatLng(addressResult.getLatitude(), addressResult.getLongitude());
                                if (mMap != null) {
                                    MarkerOptions myMarkerOptions = new MarkerOptions();
                                    myMarkerOptions.position(position);
                                    myMarkerOptions.title("Dirección Encontrada");
                                    myMarkerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                                    mMap.addMarker(myMarkerOptions);
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15));
                                    distance(addressResult.getLatitude(),addressResult.getLongitude(),latitud,longitud);
                                    consumeRESTVolley(addressResult);
                                }
                            } else {Toast.makeText(MapsActivity.this, "Dirección no encontrada", Toast.LENGTH_SHORT).show();}
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {Toast.makeText(MapsActivity.this, "La dirección esta vacía", Toast.LENGTH_SHORT).show();}
                }
                return false;
            }
        });
    }

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //--------------estos no se si estan bien aqui-------------------------------------------
        //si sirve lo del botones del zoom, el resto no se
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        //mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.stylelight));
        //----------------------------------------------------------------------------------------
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            localizacion();
        } else {
            //si el permiso no esta aceptado, lo pide!!!
            requestPermission(this, Manifest.permission.ACCESS_FINE_LOCATION, "Acceso a la localizacion necesario", PERMISSION_LOCATION_ID);
        }
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                try {
                    Geocoder geocoder;
                    List<Address> addresses;
                    geocoder = new Geocoder(getBaseContext());
                    double latitude = latLng.latitude;
                    double longitude = latLng.longitude;
                    addresses = geocoder.getFromLocation(latitude, longitude, 1);
                    String carac = addresses.get(0).getCountryName();
                    String direccion = addresses.get(0).getAddressLine(0);
                    if (myMarker == null) {
                        myMarker = mMap.addMarker(new MarkerOptions()
                                .position(latLng)
                                .title(carac)
                                .snippet(direccion));
                        dis2 = latLng;
                        distance(latitude,longitude,latitud,longitud);
                        consumeRESTVolley(addresses.get(0));

                    } else {
                        // Marker already exists, just update it's position
                        myMarker.setPosition(latLng);
                        distance(latitude,longitude,latitud,longitud);
                        consumeRESTVolley(addresses.get(0));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    dis2 = latLng;
                }
            }

        });

    }

    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(lightSensorListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(lightSensorListener);
    }

    private void requestPermission(Activity context, String permiso, String justificacion, int idCode) {
        //Aca pide el permiso
        if (ContextCompat.checkSelfPermission(context, permiso) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(context, permiso)) {
                Toast.makeText(context, justificacion, Toast.LENGTH_SHORT).show();
            }
            ActivityCompat.requestPermissions(context, new String[]{permiso}, idCode);
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            //si acepta el permiso mira cual fue
            case PERMISSION_LOCATION_ID: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    localizacion();
                }
            }
        }
    }

    private void localizacion(){
        this.mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    longitud = location.getLongitude();
                    latitud = location.getLatitude();
                    dis1 = new LatLng(latitud, longitud);

                    myMarker2 = mMap.addMarker(new MarkerOptions()
                            .position(dis1)
                            .title("Marcador pocision actual")
                            .snippet("aca se encuentra actualmente") //Texto de información
                            .alpha(0.5f)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(dis1));
                    mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
                }
            }
        });
    }

    public void distance(double lat1, double long1, double lat2, double long2) {
        double latDistance = Math.toRadians(lat1 - lat2);
        double lngDistance = Math.toRadians(long1 - long2);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double result = RADIUS_OF_EARTH_KM * c;
        Toast.makeText(MapsActivity.this, "La dirección esta a " + Math.round(result*100.0)/100.0 + " Km de su ubicacion", Toast.LENGTH_LONG).show();
    }

    public void consumeRESTVolley(Address addressResult){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://maps.googleapis.com/maps/api/directions/json?";
        String origin = "origin="+latitud+","+longitud;
        String destination = "destination="+addressResult.getLatitude()+","+addressResult.getLongitude();
        String mode = "mode=walking";
        String key = "key="+getResources().getString(R.string.google_maps_key);
        StringRequest req = new StringRequest(Request.Method.GET, url+origin+"&"+destination+"&"+mode+"&"+key,
                new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {
                        String data = (String)response;
                        parseJSON(data);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("TAG", "Error handling rest invocation"+error.getCause());
                    } }
        );
        queue.add(req);
    }

    private void parseJSON(String data) {
        ArrayList<LatLng> result = new ArrayList<>();
        String distance="";
        Double d;

        try {
            JSONObject jsonObject = new JSONObject(data);
            JSONArray steps = jsonObject.getJSONArray("routes");
            steps = steps.getJSONObject(0).getJSONArray("legs");
            d = steps.getJSONObject(0).getJSONObject("distance").getDouble("value");
            steps = steps.getJSONObject(0).getJSONArray("steps");

            result.add(new LatLng(((JSONObject)((JSONObject)steps.get(0)).get("start_location")).getDouble("lat"), ((JSONObject)((JSONObject)steps.get(0)).get("start_location")).getDouble("lng")));
            for(int i=0;i<steps.length();++i) {
                JSONObject punto = steps.getJSONObject(i);
                result.add(new LatLng(((JSONObject)punto.get("end_location")).getDouble("lat"), ((JSONObject)punto.get("end_location")).getDouble("lng")));
                Log.i("LATLNG", result.get(i).toString());
            }

            distance = "La distancia es: " + d/1000.0 + " Km a su objetivo";
            //mMap.moveCamera(CameraUpdateFactory.zoomTo(11));

            Toast.makeText(getApplicationContext(),  distance ,Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        drawRoute(result);
    }

    private void drawRoute(ArrayList<LatLng> result) {
        if(mLine!=null)
            mLine.remove();
        PolylineOptions line = new PolylineOptions();
        line.addAll(result);
        line.width(10);
        line.color(Color.RED);
        line.jointType(JointType.ROUND);
        mLine = mMap.addPolyline(line);
        CameraUpdateFactory.zoomBy(0.5f);
    }
}