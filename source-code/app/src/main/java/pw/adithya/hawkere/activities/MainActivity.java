package pw.adithya.hawkere.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

import es.dmoral.toasty.Toasty;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.providers.LocationGooglePlayServicesWithFallbackProvider;
import pw.adithya.hawkere.objects.Detail;
import pw.adithya.hawkere.R;
import pw.adithya.hawkere.objects.Rating;
import pw.adithya.hawkere.utils.RecyclerItemClickListener;
import pw.adithya.hawkere.adapters.DisplayAdapter;
import pw.adithya.hawkere.utils.SAXXMLParser;
import pw.adithya.hawkere.utils.UnitConversionUtil;

public final class MainActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, OnLocationUpdatedListener {

    private static final int LOCATION_PERMISSION_ID = 1001;
    private BottomSheetBehavior mBottomSheetBehavior;
    private GoogleMap mMap;
    private View originalLocationButton;
    private SupportMapFragment mapFragment;
    private float lat = 0, lng = 0;
    private ArrayList<Detail> details = new ArrayList<>();
    private DisplayAdapter displayAdapter;
    private TextView title;
    private FirebaseFirestore firestore;
    private double totalRating = 0;
    private int size = 0;
    private Uri data;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        setContentView(R.layout.activity_main);

        firestore = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        String action = intent.getAction();
        data = intent.getData();

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

        new ParseDataSet().execute();

        title = findViewById(R.id.textview_title);
        displayAdapter = new DisplayAdapter(details, MainActivity.this);
        RecyclerView mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setAdapter(displayAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        /*mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, mRecyclerView, new RecyclerItemClickListener
                .OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                DetailActivity.detail = details.get(position);
                startActivity(new Intent(MainActivity.this, DetailActivity.class));
            }

            @Override
            public void onItemLongClick(View view, int position) {
            }
        }));*/

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        FloatingSearchView floatingSearchView = findViewById(R.id.floating_search_view);

        floatingSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, final String newQuery) {
                displayAdapter.getFilter().filter(newQuery);

                if (displayAdapter.getItemCount() == 0) {
                    mBottomSheetBehavior.setPeekHeight(UnitConversionUtil.convertDpToPx(100));

                    title.setText("No Results Available");
                }
                else {
                    mBottomSheetBehavior.setPeekHeight(UnitConversionUtil.convertDpToPx(300));

                    title.setText("Hawker Centres Nearby");
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style));
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setTrafficEnabled(false);
        mMap.setIndoorEnabled(false);
        mMap.setBuildingsEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);

        originalLocationButton = ((View) (mapFragment.getView().findViewById(Integer.parseInt("1")).getParent())).findViewById(Integer.parseInt("2"));
        originalLocationButton.setVisibility(View.GONE);

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
                            .target(new LatLng(lat - 0.02, lng))      // Sets the center of the map to Mountain View
                            .zoom(13)                   // Sets the zoom
                            .bearing(0)                // Sets the orientation of the camera to east
                            .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                            .build();

                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
            }
        });
    }

    private class ParseDataSet extends AsyncTask<String, Void, String> {
        String result;

        @Override
        public void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                details.addAll(SAXXMLParser.parse(getAssets().open("hawker_centres.kml")));
                result = "in";
            } catch (IOException e) {
                e.printStackTrace();
                result = "out";
            }

            return result;
        }

        @Override
        public void onPostExecute(String result) {
            if (result.equalsIgnoreCase("in")) {
                sortDetails();

                if (mMap != null)
                    refreshMap();
            }
        }
    }

    private void sortDetails() {
        if (details != null) {
            Location currLocation = new Location("");
            currLocation.setLatitude(lat);
            currLocation.setLongitude(lng);

            for (Detail detail : details) {
                Location location = new Location("");
                location.setLatitude(detail.getLat());
                location.setLongitude(detail.getLon());
                detail.setDistance(currLocation.distanceTo(location));
            }

            Collections.sort(details);
        }
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
    public void onResume() {
        super.onResume();

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_ID);

        SmartLocation.with(MainActivity.this).location()
                .start(this);

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_ID);
        } else {
            SmartLocation.with(MainActivity.this)
                    .location(new LocationGooglePlayServicesWithFallbackProvider(Objects.requireNonNull(MainActivity.this)))
                    .start(this);

            if (SmartLocation.with(MainActivity.this).location().getLastLocation() != null)
                setLocation(Objects.requireNonNull(SmartLocation.with(MainActivity.this).location().getLastLocation()));
        }

        fetchRatings();
    }

    private void setLocation(Location location) {
        lat = (float) location.getLatitude();
        lng = (float) location.getLongitude();

        sortDetails();
    }

    @Override
    public void onLocationUpdated(Location location) {
        setLocation(location);

        if (displayAdapter != null)
            displayAdapter.notifyDataSetChanged();
    }

    private void refreshMap() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }

        CameraPosition cameraPosition;

        if (lat == 0 && lng == 0)
        {
            cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(1.3521-0.06, 103.8198))
                    .zoom(10)
                    .bearing(0)
                    .tilt(0)
                    .build();
        }
        else
        {
            cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(lat-0.02, lng))
                    .zoom(13)
                    .bearing(0)
                    .tilt(0)
                    .build();
        }

        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        int size = 140;

        if (details != null) {
            for (int i = 0; i < details.size(); i++) {
                Detail detail = details.get(i);

                if (size > 70)
                    size -= 3 * i;

                Marker m = detail.getMarker();

                if (m != null)
                    m.remove();

                BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_location_marker);
                Bitmap b = bitmapdraw.getBitmap();
                Bitmap icon = Bitmap.createScaledBitmap(b, size, size, false);

                detail.setMarker(mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(icon)).position(new LatLng(detail.getLat(), detail.getLon()))));
            }
        }

        //displayAdapter.notifyDataSetChanged();
        fetchRatings();
    }

    private void fetchRatings() {
        firestore.collection("Ratings")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Rating> fetchedRatings = new ArrayList<>();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Rating r = document.toObject(Rating.class);

                                fetchedRatings.add(r);
                            }

                            Collections.sort(fetchedRatings, new Comparator<Rating>() {
                                @Override
                                public int compare(Rating r1, Rating r2) {
                                    if (r1.getPlaceID().compareTo(r2.getPlaceID()) == 0)
                                        return 0;

                                    return r1.getPlaceID().compareTo(r2.getPlaceID());
                                }
                            });

                            int count = 0;
                            double totalRating = 0;

                            for (Detail d : details)
                            {
                                for (Rating rating : fetchedRatings)
                                {
                                    if (rating.getPlaceID().equals(d.getPlaceID()))
                                    {
                                        totalRating += rating.getTotalRating();
                                        count++;
                                    }
                                    else if (count > 0)
                                    {
                                        d.setRating(totalRating / count);
                                        count = 0;
                                        totalRating = 0;
                                    }
                                }

                                if (count > 0) {
                                    d.setRating(totalRating / count);
                                    count = 0;
                                    totalRating = 0;
                                }
                            }

                            displayAdapter.notifyDataSetChanged();

                            if (data != null && data.getPathSegments() != null)
                            {
                                for (Detail d : details)
                                {
                                    if (d.getPlaceID().equals(data.getPathSegments().get(0))) {
                                        DetailActivity.detail = d;
                                        startActivity(new Intent(MainActivity.this, DetailActivity.class));
                                    }
                                }
                            }
                        }
                        else {
                            Log.e("Error getting documents", "" + task.getException());
                        }
                    }
                });
    }
}