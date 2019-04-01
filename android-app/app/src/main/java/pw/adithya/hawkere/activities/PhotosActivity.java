package pw.adithya.hawkere.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import pw.adithya.hawkere.R;
import pw.adithya.hawkere.adapters.PhotosAdapter;

public class PhotosActivity extends AppCompatActivity {
    public static ArrayList<String> items;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);

        RecyclerView photosRecyclerView = findViewById(R.id.recyclerview_photos2);
        PhotosAdapter photosAdapter = new PhotosAdapter(items, PhotosActivity.this);
        photosRecyclerView.setAdapter(photosAdapter);
        photosRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
