package vn.tiki.ab;

import android.support.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigValue;
import java.util.concurrent.TimeUnit;
import rx.AsyncEmitter;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by Giang Nguyen on 10/25/16.
 */

public class ValueRequest {

  private final FirebaseRemoteConfig remoteConfig;
  private final String key;
  private final long timeout;
  private final long cacheExpiration;

  ValueRequest(FirebaseRemoteConfig remoteConfig, long cacheExpiration, String key, long timeout) {
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
    return Observable.fromEmitter(new Action1<AsyncEmitter<FirebaseRemoteConfigValue>>() {
      private boolean canceled;

      @Override public void call(final AsyncEmitter<FirebaseRemoteConfigValue> asyncEmitter) {
        remoteConfig.fetch(cacheExpiration).addOnFailureListener(new OnFailureListener() {
          @Override public void onFailure(@NonNull Exception e) {
            if (canceled) {
              canceled = false;
              return;
            }
            asyncEmitter.onError(e);
          }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
          @Override public void onSuccess(Void aVoid) {
            if (canceled) {
              canceled = false;
              return;
            }
            remoteConfig.activateFetched();
            asyncEmitter.onNext(remoteConfig.getValue(key));
          }
        }).addOnCompleteListener(new OnCompleteListener<Void>() {
          @Override public void onComplete(@NonNull Task<Void> task) {
            if (canceled) {
              canceled = false;
              return;
            }
            asyncEmitter.onCompleted();
          }
        });

        asyncEmitter.setCancellation(new AsyncEmitter.Cancellable() {
          @Override public void cancel() throws Exception {
            canceled = true;
          }
        });
      }
    }, AsyncEmitter.BackpressureMode.BUFFER)
        .timeout(timeout, TimeUnit.MILLISECONDS)
        .onErrorResumeNext(new Func1<Throwable, Observable<? extends FirebaseRemoteConfigValue>>() {
          @Override
          public Observable<? extends FirebaseRemoteConfigValue> call(Throwable throwable) {
            return Observable.just(remoteConfig.getValue(key));
          }
        });
  }
}
