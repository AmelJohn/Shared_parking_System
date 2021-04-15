package com.aumento.sharedparking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.aumento.sharedparking.utils.GlobalPreference;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FindParkingActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, GoogleMap.OnCameraMoveStartedListener,
        GoogleMap.OnCameraMoveListener,
        GoogleMap.OnCameraMoveCanceledListener,
        GoogleMap.OnCameraIdleListener {

    private LocationManager locationManager;
    private GoogleMap mMap;
    private Location currentLocation;
    private boolean first;
    private LatLng origin;
    private double deslat;
    private double deslon;

    private static final String TAG = "FindParkingActivity";
    private String ip;
    private ImageView markerIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_parking);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map2);
        mapFragment.getMapAsync(this);

        GlobalPreference globalPreference = new GlobalPreference(this);
        ip = globalPreference.RetriveIP();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        markerIV = (ImageView) findViewById(R.id.markerIV);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Toast.makeText(this, "Map Ready", Toast.LENGTH_SHORT).show();

        if (currentLocation != null && first) {
            origin = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            CameraPosition cameraPosition = new CameraPosition.Builder().target(
                    origin).zoom(15).build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            first = false;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {

                currentLocation = location;
                if(first) {
                    origin = new LatLng(location.getLatitude(), location.getLongitude());
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(
                            origin).zoom(15).build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    first = false;

                }

            }
        });

        mMap.setOnCameraMoveStartedListener(this);
        mMap.setOnCameraIdleListener(this);
        mMap.setOnCameraMoveListener(this);

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        currentLocation = location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }

    @Override
    public void onCameraIdle() {

        MarkerOptions markerOptions = new MarkerOptions().position(mMap.getCameraPosition().target);
        deslat = markerOptions.getPosition().latitude;
        deslon = markerOptions.getPosition().longitude;
        findParkingSpots(deslat,deslon);

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String position = marker.getTitle();
                Toast.makeText(FindParkingActivity.this, ""+position, Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    private void findParkingSpots(double deslat, double deslon) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://"+ip+"/shared_parking/API/getParkingSpots.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                if(response.contains("Failed")){
                }
                else {

                    try{

                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("data");

                        for(int i=0; i< jsonArray.length(); i++)
                        {
                            JSONObject object = jsonArray.getJSONObject(i);
                            String id = object.getString("id");
                            String name = object.getString("name");
                            String latitude = object.getString("latitude");
                            String longitude = object.getString("longitude");

                            LatLng latLng = new LatLng(Double.valueOf(latitude),Double.valueOf(longitude));
                            MarkerOptions markerOptions = new MarkerOptions();
                            markerOptions.title(name);
                            markerOptions.position(latLng);
                            markerOptions.title(name);
                            mMap.addMarker(markerOptions);
                        }

                    }catch (JSONException e){
                        e.printStackTrace();
                    }

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: "+error);
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> params = new HashMap<>();
                params.put("lat", String.valueOf(deslat));
                params.put("lon", String.valueOf(deslon));
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    @Override
    public void onCameraMoveCanceled() {

    }

    @Override
    public void onCameraMove() {
    }

    @Override
    public void onCameraMoveStarted(int i) {
        mMap.clear();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}