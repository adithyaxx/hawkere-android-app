package pw.adithya.hawkere.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.yanzhenjie.album.Action;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumConfig;
import com.yanzhenjie.album.AlbumFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;
import pw.adithya.hawkere.MediaLoader;
import pw.adithya.hawkere.R;
import pw.adithya.hawkere.adapters.StaggeredPhotosAdapter;
import pw.adithya.hawkere.objects.Detail;
import pw.adithya.hawkere.objects.Photo;
import pw.adithya.hawkere.objects.Rating;
import pw.adithya.hawkere.utils.RecyclerItemClickListener;

import org.ocpsoft.prettytime.PrettyTime;

public class DetailActivity extends AppCompatActivity implements OnMapReadyCallback {
    public static Detail detail;
    FirebaseFirestore firestore;
    int size = 0;
    double hygieneRating = 0, seatingRating = 0, varietyRating = 0, foodRating = 0, totalRating = 0;
    MaterialRatingBar hygieneRatingBar, varietyRatingBar, seatingRatingBar, foodRatingBar, reviewRatingBar;
    TextView ratingTextView, reviewsCountTextView, nameTextView, durationTextView, reviewTextView;
    ImageView profileImageView;
    ArrayList<Rating> reviews = new ArrayList<>();
    ArrayList<Photo> photos = new ArrayList<>();
    StaggeredPhotosAdapter staggeredPhotosAdapter;
    RecyclerView photosRecyclerView;
    RelativeLayout reviewsRelativeLayout;
    CardView reviewsCardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        firestore = FirebaseFirestore.getInstance();
        Toolbar toolbar = findViewById(R.id.toolbar);

        TextView descTextView = findViewById(R.id.textview_desc);
        TextView addressTextView = findViewById(R.id.textview_address);
        TextView hoursTextView = findViewById(R.id.textview_hours);
        TextView stallsTextView = findViewById(R.id.textview_stalls);
        TextView seeAllTextView = findViewById(R.id.textview_see_all);
        ratingTextView = findViewById(R.id.textview_rating);
        reviewsCountTextView = findViewById(R.id.textview_reviews_count);
        nameTextView = findViewById(R.id.textview_name);
        durationTextView = findViewById(R.id.textview_duration);
        reviewTextView = findViewById(R.id.textview_review);

        hygieneRatingBar = findViewById(R.id.hygiene_rating_bar);
        varietyRatingBar = findViewById(R.id.variety_rating_bar);
        seatingRatingBar = findViewById(R.id.seating_rating_bar);
        foodRatingBar = findViewById(R.id.food_rating_bar);
        reviewRatingBar = findViewById(R.id.review_rating_bar);

        profileImageView = findViewById(R.id.imageview_profile_pic);

        reviewsRelativeLayout = findViewById(R.id.container_reviews);
        reviewsCardView = findViewById(R.id.cardview_reviews);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        toolbar.setTitleTextColor(0xFFFFFFFF);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(detail.getName());

        staggeredPhotosAdapter = new StaggeredPhotosAdapter(photos, DetailActivity.this);
        photosRecyclerView = findViewById(R.id.recyclerview_photos);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL);
        photosRecyclerView.setLayoutManager(layoutManager);

        photosRecyclerView.setAdapter(staggeredPhotosAdapter);

        descTextView.setText(detail.getDescription());
        addressTextView.setText(detail.getLongAddr());
        stallsTextView.setText(detail.getNoOfStalls() + " Food Stalls");

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if (item.getItemId() == R.id.menu_rate) {
                    RatingActivity.placeID = detail.getPlaceID();
                    startActivity(new Intent(DetailActivity.this, RatingActivity.class));
                } else if (item.getItemId() == R.id.menu_photo) {
                    openGallery();
                } else if (item.getItemId() == R.id.menu_share) {
                    // do something
                } else {
                    // do something
                }

                return false;
            }
        });

        photosRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, photosRecyclerView, new RecyclerItemClickListener
                .OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                PhotosActivity.photos = photos;
                startActivity(new Intent(DetailActivity.this, PhotosActivity.class));
            }

            @Override
            public void onItemLongClick(View view, int position) {
            }
        }));

        seeAllTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReviewsActivity.reviews = reviews;
                startActivity(new Intent(DetailActivity.this, ReviewsActivity.class));
            }
        });
    }

    private void populateFields() {
        firestore.collection("Ratings")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Rating r = document.toObject(Rating.class);

                                if (r.getPlaceID().equals(detail.getPlaceID())) {
                                    hygieneRating += r.getHygieneRating();
                                    foodRating += r.getFoodRating();
                                    seatingRating += r.getSeatingRating();
                                    varietyRating += r.getVarietyRating();
                                    totalRating += r.getTotalRating();
                                    size++;

                                    reviews.add(r);
                                }
                            }

                            hygieneRatingBar.setRating((float)(hygieneRating / size));
                            varietyRatingBar.setRating((float)(varietyRating / size));
                            foodRatingBar.setRating((float)(foodRating / size));
                            seatingRatingBar.setRating((float)(seatingRating / size));

                            if (totalRating > 0)
                                ratingTextView.setText(String.format("%.2f", totalRating / size));
                            else
                                ratingTextView.setText("-");

                            reviewsCountTextView.setText(size + " REVIEWS");

                            if (reviews.size() > 0)
                            {
                                reviewsCardView.setVisibility(View.VISIBLE);
                                reviewsRelativeLayout.setVisibility(View.VISIBLE);

                                Collections.sort(reviews);

                                nameTextView.setText(reviews.get(0).getAuthorName());
                                reviewRatingBar.setRating((float) reviews.get(0).getTotalRating());

                                Glide.with(DetailActivity.this)
                                        .load(reviews.get(0).getAuthorPic())
                                        .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                                        .into(profileImageView);

                                long postDate = reviews.get(0).getTimestamp();

                                PrettyTime p = new PrettyTime();
                                durationTextView.setText(p.format(new Date(postDate)));

                                reviewTextView.setText(reviews.get(0).getReview());
                            }
                            else
                            {
                                reviewsCardView.setVisibility(View.GONE);
                                reviewsRelativeLayout.setVisibility(View.GONE);
                            }
                        }
                        else {
                            Log.e("Error getting documents", "" + task.getException());
                        }
                    }
                });

        firestore.collection("Photos")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Photo photo = document.toObject(Photo.class);

                                if (photo.getPlaceID().equals(detail.getPlaceID()))
                                    photos.add(photo);
                            }

                            if (photos.size() > 0) {
                                staggeredPhotosAdapter.notifyDataSetChanged();
                                photosRecyclerView.setVisibility(View.VISIBLE);
                            }
                            else
                                photosRecyclerView.setVisibility(View.GONE);
                        }
                        else {
                            Log.e("Error", "" + task.getException());
                        }
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds photos to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    private void openGallery()
    {
        Album.initialize(AlbumConfig.newBuilder(this)
                .setAlbumLoader(new MediaLoader())
                .build());

        Album.image(this)
                .multipleChoice()
                .camera(true)
                .selectCount(1)
                .onResult(new Action<ArrayList<AlbumFile>>() {
                    @Override
                    public void onAction(@NonNull ArrayList<AlbumFile> result) {
                        SelectPhotoActivity.placeID = detail.getPlaceID();
                        SelectPhotoActivity.photoUrl = result.get(0).getPath();
                        startActivity(new Intent(DetailActivity.this, SelectPhotoActivity.class));
                    }
                })
                .onCancel(new Action<String>() {
                    @Override
                    public void onAction(@NonNull String result) {
                    }
                })
                .start();
    }

    @Override
    public void onResume()
    {
        super.onResume();

        photos.clear();
        populateFields();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(detail.getLat(), detail.getLon()))      // Sets the center of the map to Mountain View
                .zoom(15)                   // Sets the zoom
                .bearing(0)                // Sets the orientation of the camera to east
                .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                .build();

        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style));
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.setTrafficEnabled(false);
        googleMap.setIndoorEnabled(false);
        googleMap.setBuildingsEnabled(true);

        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_location_marker);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap icon = Bitmap.createScaledBitmap(b, 150, 150, false);

        googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(icon)).position(new LatLng(detail.getLat(), detail.getLon())));
    }
}