package vn.tiki.ab.sample;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

/**
 * Created by KenZira on 10/23/16.
 */

public class ABApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseApp.initializeApp(this);

        FirebaseRemoteConfigSettings config = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        FirebaseRemoteConfig.getInstance().setConfigSettings(config);
    }
}
