package pw.adithya.hawkere.activities;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;

import pw.adithya.hawkere.R;
import pw.adithya.hawkere.adapters.PhotosAdapter;
import pw.adithya.hawkere.adapters.StaggeredPhotosAdapter;
import pw.adithya.hawkere.objects.Photo;

public class PhotosActivity extends AppCompatActivity {
    public static ArrayList<Photo> photos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);

        Toolbar toolbar = findViewById(R.id.toolbar);

        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("Photos");
        setSupportActionBar(toolbar);

        Collections.sort(photos);

        PhotosAdapter photosAdapter = new PhotosAdapter(photos, PhotosActivity.this);
        RecyclerView photosRecyclerView = findViewById(R.id.recyclerview_photos);
        photosRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        photosRecyclerView.setAdapter(photosAdapter);
    }
}
