package pw.adithya.hawkere.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.Collections;

import pw.adithya.hawkere.R;
import pw.adithya.hawkere.adapters.PhotosAdapter;
import pw.adithya.hawkere.adapters.ReviewsAdapter;
import pw.adithya.hawkere.objects.Photo;
import pw.adithya.hawkere.objects.Rating;

public class ReviewsActivity extends AppCompatActivity {
    public static ArrayList<Rating> reviews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);

        Toolbar toolbar = findViewById(R.id.toolbar);

        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("Photos");
        setSupportActionBar(toolbar);

        Collections.sort(reviews);

        ReviewsAdapter reviewsAdapter = new ReviewsAdapter(reviews, ReviewsActivity.this);
        RecyclerView reviewsRecyclerView = findViewById(R.id.recyclerview_reviews);
        reviewsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        reviewsRecyclerView.setAdapter(reviewsAdapter);
    }
}
