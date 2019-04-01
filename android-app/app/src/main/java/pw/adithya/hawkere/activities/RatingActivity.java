package pw.adithya.hawkere.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;
import pw.adithya.hawkere.R;
import pw.adithya.hawkere.SessionManager;
import pw.adithya.hawkere.objects.Rating;
import pw.adithya.hawkere.objects.User;

public class RatingActivity extends AppCompatActivity {
    FirebaseFirestore firestore;
    public static String placeID;
    private User user;
    private Rating rating = null;
    private MaterialRatingBar hygieneRatingBar, varietyRatingBar, seatingRatingBar, foodRatingBar;
    private boolean ratingExists = false;
    private String documentId = "";
    private EditText reviewEditText;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        firestore = FirebaseFirestore.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);

        hygieneRatingBar = findViewById(R.id.hygiene_rating_bar);
        varietyRatingBar = findViewById(R.id.variety_rating_bar);
        seatingRatingBar = findViewById(R.id.seating_rating_bar);
        foodRatingBar = findViewById(R.id.food_rating_bar);

        hygieneRatingBar.setMax(5);
        varietyRatingBar.setMax(5);
        seatingRatingBar.setMax(5);
        foodRatingBar.setMax(5);

        reviewEditText = findViewById(R.id.edittext_review);
        ImageView tickImageView = findViewById(R.id.imageview_tick);

        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Uploading...");
        progressDialog.setCancelable(false);

        toolbar.setTitle("Rate / Review");
        setSupportActionBar(toolbar);
        toolbar.setOverflowIcon(getDrawable(R.drawable.ic_check_white_48dp));

        SessionManager sessionManager = new SessionManager(this);

        if (sessionManager.isLoggedIn()) {
            user = sessionManager.getUser();
            Log.e("User", user.getEmailID() + "");
        } else {
            startActivity(new Intent(RatingActivity.this, LoginActivity.class));
        }

        populateFields();

        tickImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();

                if (hygieneRatingBar.getProgress() != 0 && varietyRatingBar.getProgress() != 0 &&
                        seatingRatingBar.getProgress() != 0 && foodRatingBar.getProgress() != 0)
                    submitRating();
                else
                    Toasty.error(RatingActivity.this, "All rating fields must be filled up").show();

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

                                if (r.getPlaceID().equals(placeID) && r.getUserID().equals(user.getUserID())) {
                                    rating = r;

                                    hygieneRatingBar.setRating((float) (rating.getHygieneRating()));
                                    varietyRatingBar.setRating((float) (rating.getVarietyRating()));
                                    foodRatingBar.setRating((float) (rating.getFoodRating()));
                                    seatingRatingBar.setRating((float) (rating.getSeatingRating()));

                                    ratingExists = true;
                                    documentId = document.getId();
                                }
                            }

                        } else {
                            Log.e("Error getting documents", "" + task.getException());
                        }
                    }
                });
    }

    private void submitRating() {
        double totalRating = foodRatingBar.getRating() + varietyRatingBar.getRating() + seatingRatingBar.getRating() + hygieneRatingBar.getRating();

        Map<String, Object> rating = new HashMap<>();
        rating.put("foodRating", foodRatingBar.getRating());
        rating.put("hygieneRating", hygieneRatingBar.getRating());
        rating.put("seatingRating", seatingRatingBar.getRating());
        rating.put("varietyRating", varietyRatingBar.getRating());
        rating.put("placeID", placeID);
        rating.put("userID", user.getUserID());
        rating.put("review", reviewEditText.getText().toString());
        rating.put("authorName", user.getFirstName() + " " + user.getLastName());
        rating.put("timestamp", Calendar.getInstance().getTimeInMillis());
        rating.put("totalRating", totalRating / 4);
        rating.put("authorPic", user.getProfileImageUrl());

        if (!ratingExists) {
            firestore.collection("Ratings")
                    .add(rating)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            progressDialog.hide();
                            Toasty.success(RatingActivity.this, "Rating added successfully").show();
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.hide();
                            Toasty.error(RatingActivity.this, "An error occured, please try again").show();
                            Log.e("Rating", e.getMessage() + "");
                        }
                    });
        } else {
            firestore.collection("Ratings")
                    .document(documentId)
                    .update(rating)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressDialog.hide();
                            Toasty.success(RatingActivity.this, "Rating added successfully").show();
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.hide();
                            Toasty.error(RatingActivity.this, "An error occured, please try again").show();
                        }
                    });
        }
    }
}
