package pw.adithya.hawkerapp;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.AsyncTask;
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
import android.util.Log;
import android.util.Property;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.location.places.PlaceReport;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.data.Geometry;
import com.google.maps.android.data.kml.KmlContainer;
import com.google.maps.android.data.kml.KmlLayer;
import com.google.maps.android.data.kml.KmlLineString;
import com.google.maps.android.data.kml.KmlPlacemark;
import com.google.maps.android.data.kml.KmlPoint;
import com.google.maps.android.data.kml.KmlPolygon;
import com.google.gson.Gson;

import org.json.JSONException;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import pw.adithya.hawkerapp.Objects.Details;

public final class MainActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private static final int LOCATION_PERMISSION_ID = 1001;
    private BottomSheetBehavior mBottomSheetBehavior;
    private GoogleMap mMap;
    private View originalLocationButton;
    private SupportMapFragment mapFragment;
    private float lat = 0, lng = 0;
    private List<ParsingStructure> parsingStr;

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
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style));
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

        new ParsingBigFileAsync().execute();
    }

    private class ParsingBigFileAsync extends AsyncTask<String, Void , String> {
        String  result;
        Dialog dialog;
        ProgressBar pBar ;
        @Override
        public void onPreExecute(){
            dialog = new Dialog(MainActivity.this);
            dialog.setTitle("Loading......");
            dialog.show();
        }
        @Override
        protected String doInBackground(String... params) {
            try {
                parsingStr = SAXXMLParser.parse(getAssets().open("hawker_centres.kml"));
                result = "in";
            } catch (IOException e) {
                e.printStackTrace();
                result = "out";
            }

            return result;
        }

        @Override
        public void onPostExecute(String result){
            if(result.equalsIgnoreCase("in")){
                BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_location_marker);
                Bitmap b = bitmapdraw.getBitmap();
                Bitmap icon = Bitmap.createScaledBitmap(b, 30, 30, false);

                for (ParsingStructure parsingStructure : parsingStr) {
                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(icon)).position(new LatLng(parsingStructure.getLat(), parsingStructure.getLon())));
                }
                dialog.dismiss();
            }
        }

    }

    /*private ArrayList<Details> getDetails()
    {
        InputStream kmlInputStream = getResources().openRawResource(R.raw.hawker_centres);
        try {
            KmlLayer kmlLayer = new KmlLayer(mMap, kmlInputStream, getApplicationContext());
            kmlLayer.addLayerToMap();

            BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_location_marker);
            Bitmap b = bitmapdraw.getBitmap();
            Bitmap icon = Bitmap.createScaledBitmap(b, 30, 30, false);

            ArrayList<LatLng> pathPoints = new ArrayList<>();

            if (kmlLayer.getContainers() != null) {
                for (KmlContainer container : kmlLayer.getContainers()) {
                    if (container.hasPlacemarks()) {
                        for (KmlPlacemark placemark : container.getPlacemarks())
                        {
                            Geometry geometry = placemark.getGeometry();
                            placemark.getMarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(icon));
                            if (geometry.getGeometryType().equals("Point")) {
                                KmlPoint point = (KmlPoint) geometry;
                                LatLng latLng = new LatLng(point.getGeometryObject().latitude, point.getGeometryObject().longitude);
                                pathPoints.add(latLng);
                            } else if (geometry.getGeometryType().equals("LineString")) {
                                KmlLineString kmlLineString = (KmlLineString) geometry;
                                ArrayList<LatLng> coords = kmlLineString.getGeometryObject();
                                pathPoints.addAll(coords);
                            }
                        }
                    }
                }

                for (LatLng latLng : pathPoints) {
                    MarkerOptions markerOptions = new MarkerOptions();

                    mMap.addMarker(markerOptions);
                }
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

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