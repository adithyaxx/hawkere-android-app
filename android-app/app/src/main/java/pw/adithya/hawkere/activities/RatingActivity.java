package pw.adithya.hawkere.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
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

        EditText reviewEditText = findViewById(R.id.edittext_review);

        toolbar.setTitle("Rate / Review");
        setSupportActionBar(toolbar);

        SessionManager sessionManager = new SessionManager(this);

        if (sessionManager.isLoggedIn()) {
            user = sessionManager.getUser();
            Log.e("User", user.getEmailID() + "");
        } else {
            startActivity(new Intent(RatingActivity.this, LoginActivity.class));
        }

        populateFields();
        //submitRating();
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

                                if (r.getPlaceID().equals(placeID) && r.getUserID().equals(user.getUserID()))
                                {
                                    rating = r;

                                    hygieneRatingBar.setProgress((int)(rating.getHygieneRating()));
                                    varietyRatingBar.setProgress((int)(rating.getVarietyRating()));
                                    foodRatingBar.setProgress((int)(rating.getFoodRating()));
                                    seatingRatingBar.setProgress((int)(rating.getSeatingRating()));

                                    Toasty.success(RatingActivity.this, "YES").show();
                                }
                            }

                        } else {
                            Log.e("Error getting documents", "" + task.getException());
                        }
                    }
                });
    }

    private void submitRating() {
        Map<String, Object> rating = new HashMap<>();
        rating.put("foodRating", 5.0);
        rating.put("hygieneRating", 5.0);
        rating.put("seatingRating", 5.0);
        rating.put("varietyRating", 5.0);
        rating.put("placeID", placeID);
        rating.put("userID", user.getUserID());
        rating.put("review", "");
        rating.put("authorName", user.getFirstName() + " " + user.getLastName());
        rating.put("timestamp", Calendar.getInstance().getTimeInMillis());

        firestore.collection("Ratings")
                .add(rating)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toasty.success(RatingActivity.this, "Success").show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toasty.error(RatingActivity.this, "Failed").show();
                        Log.e("Rating", e.getMessage() + "");
                    }
                });
    }
}
