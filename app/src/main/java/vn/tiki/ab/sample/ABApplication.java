package vn.tiki.ab.sample;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDex;
import android.support.v4.util.ArrayMap;
import com.google.firebase.FirebaseApp;
import com.squareup.leakcanary.LeakCanary;
import java.util.concurrent.TimeUnit;
import vn.tiki.ab.AbSettings;
import vn.tiki.ab.AbTesting;

/**
 * Created by KenZira on 10/23/16.
 */

public class ABApplication extends Application {

  private AbTesting ab;

  public static ABApplication get(Context context) {
    return (ABApplication) context.getApplicationContext();
  }

  @Override public void onCreate() {
    super.onCreate();

    if (!LeakCanary.isInAnalyzerProcess(this)) {
      LeakCanary.install(this);
    }

    FirebaseApp.initializeApp(this);

    ab = new AbTesting(new AbSettings.Builder()
        .debug(BuildConfig.DEBUG)
        .cacheExpiration(2, TimeUnit.SECONDS)
        .defaults(defaultAbValues())
        .build());
  }

  @Override
  protected void attachBaseContext(Context base) {
    super.attachBaseContext(base);
    MultiDex.install(this);
  }

  @NonNull private ArrayMap<String, Object> defaultAbValues() {
    final ArrayMap<String, Object> map = new ArrayMap<>();
    map.put("onboard_navigation", "Home");
    return map;
  }

  public AbTesting getAb() {
    return ab;
  }
}
