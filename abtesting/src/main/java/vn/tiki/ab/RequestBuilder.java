package vn.tiki.ab;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import java.util.concurrent.TimeUnit;

/**
 * Created by Giang Nguyen on 10/25/16.
 */

public class RequestBuilder {
  private final FirebaseRemoteConfig remoteConfig;
  private final long cacheExpiration;
  private String key;
  private long timeout;
  private Activity activity;

  RequestBuilder(@NonNull FirebaseRemoteConfig remoteConfig, @Nullable Activity activity,
      long cacheExpiration) {
    this.remoteConfig = remoteConfig;
    this.cacheExpiration = cacheExpiration;
    this.activity = activity;
  }

  public RequestBuilder key(String key) {
    this.key = key;
    return this;
  }

  public RequestBuilder timeout(long timeout, TimeUnit timeUnit) {
    this.timeout = TimeUnit.MILLISECONDS.convert(timeout, timeUnit);
    return this;
  }

  public ValueRequest build() {
    return new ValueRequest(activity, remoteConfig, cacheExpiration, key, timeout);
  }
}
