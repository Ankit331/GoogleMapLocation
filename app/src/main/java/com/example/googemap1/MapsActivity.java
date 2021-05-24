package com.example.googemap1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.google.android.gms.location.FusedLocationProviderClient;;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.googemap1.databinding.ActivityMapsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.List;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback  {

    private static final String TAG = "Maps";
    private static final String KEY_LOCATION = "location";
    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient fusedLocationProviderClient;
    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location lastKnownLocation;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng defaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 16;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted;

    private GoogleMap map;
    private ActivityMapsBinding binding;

    //Floating Button
    FloatingActionButton fab;
    CardView cardview;

    // Image Button for Setting map
    ImageButton defaultMap;
    ImageButton satelliteMap;
    ImageButton terrainMap;

    SearchView searchView;

    //Views
    View nview;
    View sview;
    View tview;

    View mapView;
   //MAP Type
    private static final int NORMAL_MAP=1;
    private static final int SATTELITE_MAP=2;
    private static final int TERRAINMAP=3;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
        }
        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapView = mapFragment.getView();



        nview=findViewById(R.id.map_type_default_background);
        sview=findViewById(R.id.map_type_satellite_background);
        tview=findViewById(R.id.map_type_terrain_background);

        searchView=findViewById(R.id.idSearchView);


         fab=findViewById(R.id.map_type_FAB);
         cardview=findViewById(R.id.cardviewmap);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
               //cardview is visiblew when floating button tapped
                cardview.setVisibility(View.VISIBLE);
                //Floating button Invisible  when card view is opened
                fab.setVisibility(View.INVISIBLE);


                defaultMap=findViewById(R.id.map_type_default);
                defaultMap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        map.setMapType(NORMAL_MAP);

                        nview.setVisibility(View.VISIBLE);
                        sview.setVisibility(View.INVISIBLE);
                        tview.setVisibility(View.INVISIBLE);

                        fab.setVisibility(View.VISIBLE);
                        cardview.setVisibility(View.INVISIBLE);

                    }
                });

               satelliteMap=findViewById(R.id.map_type_satellite);
               satelliteMap.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       map.setMapType(SATTELITE_MAP);
                       sview.setVisibility(View.VISIBLE);
                       nview.setVisibility(View.INVISIBLE);
                       tview.setVisibility(View.INVISIBLE);

                       fab.setVisibility(View.VISIBLE);
                       cardview.setVisibility(View.INVISIBLE);

                   }
               });

               terrainMap=findViewById(R.id.map_type_terrain);
               terrainMap.setOnClickListener(new View.OnClickListener()
               {
                   @Override
                   public void onClick(View v)
                   {
                       map.setMapType(TERRAINMAP);

                       tview.setVisibility(View.VISIBLE);
                       nview.setVisibility(View.INVISIBLE);
                       sview.setVisibility(View.INVISIBLE);

                       fab.setVisibility(View.VISIBLE);
                       cardview.setVisibility(View.INVISIBLE);

                   }
               });

            }
        });


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                // on below line we are getting the
                // location name from search view.
                String location = searchView.getQuery().toString();

                // below line is to create a list of address
                // where we will store the list of all address.
                List<Address> addressList = null;

                // checking if the entered location is null or not.
                if (location != null || location.equals("")) {
                    // on below line we are creating and initializing a geo coder.
                    Geocoder geocoder = new Geocoder(MapsActivity.this);
                    try {
                        // on below line we are getting location from the
                        // location name and adding that location to address list.
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(MapsActivity.this, "Enter a correct address", Toast.LENGTH_SHORT).show();
                    }
                    // on below line we are getting the location
                    // from our list a first position.
                    Address address = addressList.get(0);

                    // on below line we are creating a variable for our location
                    // where we will add our locations latitude and longitude.
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                    // on below line we are adding marker to that position.
                    map.addMarker(new MarkerOptions().position(latLng).title(location));

                    // below line is to animate camera to that position.
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                    gestureMap();

                    map.setMapType(map.getMapType());
                    map.setIndoorEnabled(true);
                    map.setBuildingsEnabled(true);
                    map.setTrafficEnabled(true);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });
        // at last we calling our map fragment to update.
        mapFragment.getMapAsync(this);
    }



    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        map = googleMap;

        // By default marker when open app
        LatLng india = new LatLng(22.3511148,78.6677428);
        map.addMarker(new MarkerOptions().position(india).title("Marker in India"));
        map.moveCamera(CameraUpdateFactory.newLatLng(india));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(india,4));

        //finding position of location button
        if (mapView != null &&
                mapView.findViewById(Integer.parseInt("1")) != null) {
            // Get the button view
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            // and next place it, on bottom right (as Google Maps app)
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                    locationButton.getLayoutParams();
            // position on right bottom
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(50, 0, 30, 400);
        }
        //for updating current map
        map.setMapType(map.getMapType());
        gestureMap(); // for setting map gesture

        getLocationPermission(); //for permission
        getDeviceLocation();    // for device location
        updateLocationUI();    // for update ui on google map

        final Context context = this;
        map.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                LocationManager mgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (!mgr.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    Intent intent1;
                    intent1 = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent1);

                }
                return false;
            }
        });

    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(lastKnownLocation.getLatitude(),
                                                lastKnownLocation.getLongitude()), 17));
                                //for updating current map
                                map.setMapType(map.getMapType());
                                map.getUiSettings().setCompassEnabled(true);

                                 gestureMap(); //for setting map gesture
                                Log.d(TAG,"current location updated");
                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.d(TAG, "Exception: %s", task.getException());
                            map.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                            map.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.d(TAG, e.getMessage(), e);
        }
    }


    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;

            Log.d(TAG,"permission granted");
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }


    // [START maps_current_place_update_location_ui]
    private void updateLocationUI()
    {
        if (map == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                 map.setMyLocationEnabled(true);
               map.getUiSettings().setMyLocationButtonEnabled(true);

               gestureMap(); //for setting map gesture

            } else {
                map.setMyLocationEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.d(TAG, e.getMessage());
        }
    }


    public void gestureMap()
    {
        UiSettings uiSettings = map.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setMapToolbarEnabled(true);
        uiSettings.setIndoorLevelPickerEnabled(true);

        uiSettings.setRotateGesturesEnabled(true);
        uiSettings.setScrollGesturesEnabled(true);
        uiSettings.setTiltGesturesEnabled(true);
        uiSettings.setZoomGesturesEnabled(true);
        uiSettings.setAllGesturesEnabled(true);
        uiSettings.setCompassEnabled(true);

        map.setIndoorEnabled(true);
        map.setBuildingsEnabled(true);
        map.setTrafficEnabled(true);

    }

}