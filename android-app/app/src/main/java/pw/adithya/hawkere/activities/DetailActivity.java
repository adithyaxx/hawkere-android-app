package pw.adithya.hawkere.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
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
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;
import pw.adithya.hawkere.MediaLoader;
import pw.adithya.hawkere.R;
import pw.adithya.hawkere.adapters.StaggeredPhotosAdapter;
import pw.adithya.hawkere.objects.Detail;
import pw.adithya.hawkere.objects.Rating;
import pw.adithya.hawkere.utils.RecyclerItemClickListener;

import org.ocpsoft.prettytime.PrettyTime;

public class DetailActivity extends AppCompatActivity {
    public static Detail detail;
    FirebaseFirestore firestore;
    int size = 0;
    double hygieneRating = 0, seatingRating = 0, varietyRating = 0, foodRating = 0, totalRating = 0;
    MaterialRatingBar hygieneRatingBar, varietyRatingBar, seatingRatingBar, foodRatingBar, reviewRatingBar;
    TextView ratingTextView, reviewsCountTextView, nameTextView, durationTextView, reviewTextView;
    ImageView profileImageView;
    ArrayList<Rating> reviews = new ArrayList<>();

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

        final ArrayList<String> items = new ArrayList<>();

        toolbar.setTitleTextColor(0xFFFFFFFF);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(detail.getName());

        RecyclerView photosRecyclerView = findViewById(R.id.recyclerview_photos);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL);
        photosRecyclerView.setLayoutManager(layoutManager);

        items.add("https://proxy.duckduckgo.com/iu/?u=http%3A%2F%2Fimages.huffingtonpost.com%2F2013-12-27-food12.jpg&f=1");
        items.add("https://www.soomska.com/wp-content/uploads/2017/12/1-1.jpg");
        items.add("https://proxy.duckduckgo.com/iu/?u=http%3A%2F%2Fwww.michaelray.com%2Fwordpress%2Fwp-content%2Fpittsburgh-photographer%2FPA-food-photographer.jpg&f=1");
        items.add("https://proxy.duckduckgo.com/iu/?u=https%3A%2F%2Fcdn.pixabay.com%2Fphoto%2F2017%2F07%2F14%2F23%2F25%2Fkebab-2505237_960_720.jpg&f=1");

        photosRecyclerView.setAdapter(new StaggeredPhotosAdapter(items, DetailActivity.this));

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
                PhotosActivity.items = items;
                startActivity(new Intent(DetailActivity.this, PhotosActivity.class));
            }

            @Override
            public void onItemLongClick(View view, int position) {
            }
        }));

        seeAllTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        populateFields();
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
                        }
                        else {
                            Log.e("Error getting documents", "" + task.getException());
                        }
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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
}
