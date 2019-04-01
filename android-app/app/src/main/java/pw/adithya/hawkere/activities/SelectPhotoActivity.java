package pw.adithya.hawkere.activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yanzhenjie.album.Action;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumConfig;
import com.yanzhenjie.album.AlbumFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import pw.adithya.hawkere.MediaLoader;
import pw.adithya.hawkere.R;

public class SelectPhotoActivity extends AppCompatActivity {
    public static String photoUrl, placeID;
    private ImageView photoImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_photo);

        Toolbar toolbar = findViewById(R.id.toolbar);
        photoImageView = findViewById(R.id.imageview_photo);
        Button retakeButton = findViewById(R.id.button_retake);
        ImageView tickImageView = findViewById(R.id.imageview_tick);

        toolbar.setTitle("Upload a Photo");
        setSupportActionBar(toolbar);

        Glide.with(this).load(photoUrl).into(photoImageView);

        retakeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                openGallery();
            }
        });

        tickImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadPhoto();
            }
        });
    }

    private void uploadPhoto()
    {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        final StorageReference ref = storageRef.child("images/mountains.jpg");
        UploadTask uploadTask = ref.putFile(Uri.fromFile(new File(photoUrl)));

        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();

                    uploadPhotoURL(downloadUri.toString());
                } else {

                }
            }
        });
    }

    private void uploadPhotoURL(String url)
    {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        Map<String, Object> photo = new HashMap<>();
        photo.put("placeID", placeID);
        photo.put("url", url);

        firestore.collection("Photos")
                .add(photo)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toasty.success(SelectPhotoActivity.this, "Rating added successfully").show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toasty.error(SelectPhotoActivity.this, "An error occured, please try again").show();
                        Log.e("Rating", e.getMessage() + "");
                    }
                });
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
                        SelectPhotoActivity.placeID = placeID;
                        SelectPhotoActivity.photoUrl = result.get(0).getPath();
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
