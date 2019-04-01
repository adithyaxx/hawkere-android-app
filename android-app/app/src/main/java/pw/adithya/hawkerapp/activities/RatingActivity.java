package pw.adithya.hawkerapp.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;
import pw.adithya.hawkerapp.R;

public class RatingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        Toolbar toolbar = findViewById(R.id.toolbar);

        MaterialRatingBar hygieneRatingBar = findViewById(R.id.hygiene_rating_bar);
        MaterialRatingBar varietyRatingBar = findViewById(R.id.variety_rating_bar);
        MaterialRatingBar seatingRatingBar = findViewById(R.id.seating_rating_bar);
        MaterialRatingBar foodRatingBar = findViewById(R.id.food_rating_bar);

        EditText reviewEditText = findViewById(R.id.edittext_review);

        toolbar.setTitle("Rate / Review");
        setSupportActionBar(toolbar);
    }
}
