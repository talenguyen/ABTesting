package vn.tiki.ab.sample;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;
import com.google.firebase.FirebaseApp;
import java.util.concurrent.TimeUnit;
import vn.tiki.ab.ABTesting;
import vn.tiki.ab.AbSettings;

/**
 * Created by KenZira on 10/23/16.
 */

public class ABApplication extends Application {

  private ABTesting ab;

  public static ABApplication get(Context context) {
    return (ABApplication) context.getApplicationContext();
  }

  @Override public void onCreate() {
    super.onCreate();

    FirebaseApp.initializeApp(this);

    ab = new ABTesting(new AbSettings.Builder()
        .debug(BuildConfig.DEBUG)
        .cacheExpiration(2, TimeUnit.SECONDS)
        .defaults(defaultAbValues())
        .build());
  }

  @NonNull private ArrayMap<String, Object> defaultAbValues() {
    final ArrayMap<String, Object> map = new ArrayMap<>();
    map.put("onboard_navigation", "Home");
    return map;
  }

  public ABTesting getAb() {
    return ab;
  }
}
