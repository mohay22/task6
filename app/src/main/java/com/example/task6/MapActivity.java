package com.example.task6;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import android.Manifest;
import android.content.pm.PackageManager;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;
    private boolean mapLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1
            );
        } else {
            setupMap();
        }
    }

    private void setupMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (!mapLoaded) {
                Toast.makeText(this, "Map failed to load. Closing app.", Toast.LENGTH_LONG).show();
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    finish(); // Terminates the activity
                }, 2000); // Wait 2 seconds to show the toast
            }
        }, 5000); // Wait 5 seconds for map to load
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mapLoaded = true;
        googleMap = map;
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            googleMap.addMarker(new MarkerOptions().position(userLocation).title("You are here"));
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15f));
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to get location: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            setupMap();
        } else {
            Toast.makeText(this, "Location permission denied. Closing app.", Toast.LENGTH_LONG).show();
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                finish(); // Terminate if permission is denied
            }, 2000);
        }
    }
}