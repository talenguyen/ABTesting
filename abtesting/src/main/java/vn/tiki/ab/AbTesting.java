package vn.tiki.ab;

import android.app.Activity;
import android.support.annotation.NonNull;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

public class AbTesting {

  private final FirebaseRemoteConfig firebaseRemoteConfig;
  private final long cacheExpiration;

  /**
   *
   * @param settings
     */
  public AbTesting(AbSettings settings) {
    this.firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

    final FirebaseRemoteConfigSettings remoteConfigSettings =
        new FirebaseRemoteConfigSettings.Builder().setDeveloperModeEnabled(settings.debug())
            .build();
    firebaseRemoteConfig.setConfigSettings(remoteConfigSettings);

    firebaseRemoteConfig.setDefaults(settings.defaults());
    cacheExpiration = settings.cacheExpiration();
  }

  public RequestBuilder request() {
    return new RequestBuilder(firebaseRemoteConfig, cacheExpiration);
  }
}