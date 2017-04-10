package vn.tiki.ab;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigValue;
import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;
import rx.Emitter;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Cancellable;
import rx.functions.Func1;

/**
 * Created by Giang Nguyen on 10/25/16.
 */

public class ValueRequest {

  private final FirebaseRemoteConfig remoteConfig;
  private final String key;
  private final long timeout;
  private final long cacheExpiration;
  private final WeakReference<Activity> activityWeakReference;

  ValueRequest(@Nullable Activity activity, FirebaseRemoteConfig remoteConfig, long cacheExpiration,
      String key, long timeout) {
    activityWeakReference = new WeakReference<>(activity);
    this.remoteConfig = remoteConfig;
    this.cacheExpiration = cacheExpiration;
    this.key = key;
    this.timeout = timeout;
  }

  public <T> Observable<T> parseValue(final ValueParser<T> valueParser) {
    return getValue().map(new Func1<FirebaseRemoteConfigValue, T>() {
      @Override public T call(FirebaseRemoteConfigValue remoteConfigValue) {
        return valueParser.parse(remoteConfigValue.asString());
      }
    });
  }

  public Observable<String> stringValue() {
    return getValue().map(new Func1<FirebaseRemoteConfigValue, String>() {
      @Override public String call(FirebaseRemoteConfigValue remoteConfigValue) {
        return remoteConfigValue.asString();
      }
    });
  }

  public Observable<Boolean> booleanValue() {
    return getValue().map(new Func1<FirebaseRemoteConfigValue, Boolean>() {
      @Override public Boolean call(FirebaseRemoteConfigValue remoteConfigValue) {
        return remoteConfigValue.asBoolean();
      }
    });
  }

  private Observable<FirebaseRemoteConfigValue> getValue() {
    return Observable
        .create(new Action1<Emitter<FirebaseRemoteConfigValue>>() {
          @Override
          public void call(final Emitter<FirebaseRemoteConfigValue> asyncEmitter) {
            final OnFailureListener onFailureListener = new OnFailureListener() {
              @Override public void onFailure(@NonNull Exception e) {
                asyncEmitter.onError(e);
              }
            };
            final OnSuccessListener<Void> onSuccessListener = new OnSuccessListener<Void>() {
              @Override public void onSuccess(Void aVoid) {
                remoteConfig.activateFetched();
                asyncEmitter.onNext(remoteConfig.getValue(key));
              }
            };
            final OnCompleteListener<Void> onCompleteListener = new OnCompleteListener<Void>() {
              @Override public void onComplete(@NonNull Task<Void> task) {
                asyncEmitter.onCompleted();
              }
            };

            final Task<Void> fetch = remoteConfig.fetch(cacheExpiration);
            final Activity activity = activityWeakReference.get();
            if (activity == null) {
              fetch
                  .addOnFailureListener(onFailureListener)
                  .addOnSuccessListener(onSuccessListener)
                  .addOnCompleteListener(onCompleteListener);
            } else {
              fetch
                  .addOnFailureListener(activity, onFailureListener)
                  .addOnSuccessListener(activity, onSuccessListener)
                  .addOnCompleteListener(activity, onCompleteListener);

              asyncEmitter
                  .setCancellation(new Cancellable() {
                    @Override public void cancel() throws Exception {
                      activityWeakReference.clear();
                    }
                  });
            }
          }
        }, Emitter.BackpressureMode.BUFFER)
        .timeout(timeout, TimeUnit.MILLISECONDS)
        .onErrorResumeNext(new Func1<Throwable, Observable<? extends FirebaseRemoteConfigValue>>() {
          @Override
          public Observable<? extends FirebaseRemoteConfigValue> call(Throwable throwable) {
            return Observable
                .just(remoteConfig.getValue(key));
          }
        });
  }
}
