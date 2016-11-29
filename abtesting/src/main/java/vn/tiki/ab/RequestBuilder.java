package vn.tiki.ab;

import android.app.Activity;
import android.support.annotation.NonNull;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import java.util.concurrent.TimeUnit;

/**
 * Created by Giang Nguyen on 10/25/16.
 */

public class RequestBuilder {
  private String key;
  private long timeout;
  private final FirebaseRemoteConfig remoteConfig;
  private final long cacheExpiration;
  private Activity activity;

  RequestBuilder(@NonNull FirebaseRemoteConfig remoteConfig, long cacheExpiration) {
    this.remoteConfig = remoteConfig;
    this.cacheExpiration = cacheExpiration;
  }

  public RequestBuilder key(String key) {
    this.key = key;
    return this;
  }

  public RequestBuilder bindLifeCycleTo(Activity activity){
    this.activity = activity;
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
