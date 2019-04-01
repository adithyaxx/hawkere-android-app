package pw.adithya.hawkere;

import android.app.Application;

import com.google.firebase.FirebaseApp;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        FirebaseApp.initializeApp(this);
        super.onCreate();
    }
}
