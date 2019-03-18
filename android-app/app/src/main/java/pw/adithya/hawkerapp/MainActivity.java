package pw.adithya.hawkerapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;

public final class MainActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private static final int LOCATION_PERMISSION_ID = 1001;
    private BottomSheetBehavior mBottomSheetBehavior;
    private GoogleMap mMap;
    private View originalLocationButton;
    private SupportMapFragment mapFragment;
    private float lat = 0, lng = 0;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        setContentView(R.layout.activity_main);

        View bottomSheet = findViewById(R.id.bottom_sheet);

        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN)
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        ArrayList<Details> arrayList = new ArrayList<>();
        arrayList.add(new Details());
        arrayList.add(new Details());
        arrayList.add(new Details());
        arrayList.add(new Details());
        arrayList.add(new Details());
        arrayList.add(new Details());
        arrayList.add(new Details());
        arrayList.add(new Details());
        arrayList.add(new Details());
        arrayList.add(new Details());
        arrayList.add(new Details());
        arrayList.add(new Details());

        SearchAdapter searchAdapter = new SearchAdapter(this, arrayList);
        RecyclerView mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setAdapter(searchAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ImageView locationButton = findViewById(R.id.imageview_location);
        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                }

                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_ID);
                else if (lat == 0 && lng == 0)
                    Toasty.warning(MainActivity.this, "Your location is being determined, please try again in a few seconds \uD83D\uDE05").show();
                else {
                    originalLocationButton.callOnClick();
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(1.3521, 103.8198))      // Sets the center of the map to Mountain View
                            .zoom(12)                   // Sets the zoom
                            .bearing(0)                // Sets the orientation of the camera to east
                            .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                            .build();

                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.setTrafficEnabled(false);
        googleMap.setIndoorEnabled(false);
        googleMap.setBuildingsEnabled(true);

        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        }

        googleMap.getUiSettings().setZoomControlsEnabled(false);
        googleMap.getUiSettings().setMapToolbarEnabled(false);

        originalLocationButton = ((View) (mapFragment.getView().findViewById(Integer.parseInt("1")).getParent())).findViewById(Integer.parseInt("2"));
        originalLocationButton.setVisibility(View.GONE);

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(1.3521, 103.8198))      // Sets the center of the map to Mountain View
                .zoom(12)                   // Sets the zoom
                .bearing(0)                // Sets the orientation of the camera to east
                .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                .build();

        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_ID && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            originalLocationButton.callOnClick();
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_ID);

        SmartLocation.with(MainActivity.this).location()
                .start(new OnLocationUpdatedListener() {
                    @Override
                    public void onLocationUpdated(Location location) {
                        lat = (float) location.getLatitude();
                        lng = (float) location.getLongitude();
                    }
                });
    }
}