package pw.adithya.hawkere.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;
import pw.adithya.hawkere.objects.Detail;
import pw.adithya.hawkere.R;
import pw.adithya.hawkere.adapters.StaggeredPhotosAdapter;
import pw.adithya.hawkere.utils.RecyclerItemClickListener;

public class DetailActivity extends AppCompatActivity {
    public static Detail detail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);

        TextView descTextView = findViewById(R.id.textview_desc);
        TextView addressTextView = findViewById(R.id.textview_address);
        TextView hoursTextView = findViewById(R.id.textview_hours);
        TextView stallsTextView = findViewById(R.id.textview_stalls);
        TextView ratingTextView = findViewById(R.id.textview_rating);
        TextView reviewTextView = findViewById(R.id.textview_reviews_count);
        TextView seeAllTextView = findViewById(R.id.textview_see_all);

        MaterialRatingBar hygieneRatingBar = findViewById(R.id.hygiene_rating_bar);
        MaterialRatingBar varietyRatingBar = findViewById(R.id.variety_rating_bar);
        MaterialRatingBar seatingRatingBar = findViewById(R.id.seating_rating_bar);
        MaterialRatingBar foodRatingBar = findViewById(R.id.food_rating_bar);

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

                if(item.getItemId() == R.id.menu_rate)
                {
                    RatingActivity.placeID = detail.getPlaceID();
                    startActivity(new Intent(DetailActivity.this, RatingActivity.class));
                }
                else if(item.getItemId() == R.id.menu_photo)
                {
                    // do something
                }
                else if(item.getItemId() == R.id.menu_share)
                {
                    // do something
                }
                else{
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
}
