package vn.tiki.ab;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.concurrent.TimeUnit;

import rx.AsyncEmitter;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by KenZira on 10/20/16.
 */

public class ABNetworkRequest {

    private static final String TAG = "ABNetworkRequest";

    private String key;
    private int timeOut;
    private int cacheExpiration = 120;

    private FirebaseRemoteConfig firebaseRemoteConfig;

    ABNetworkRequest(String key, int timeOut, int cacheExpiration) {
        this.key = key;
        this.timeOut = timeOut;
        this.cacheExpiration = cacheExpiration;
        this.firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
    }

    public Observable<String> execute() {
        return Observable.fromEmitter(
                new Action1<AsyncEmitter<String>>() {
                    private boolean canceled;

                    @Override
                    public void call(final AsyncEmitter<String> emitter) {

                        emitter.setCancellation(setFlagCanceled());

                        firebaseRemoteConfig.fetch(cacheExpiration)
                                .addOnSuccessListener(emitValue(emitter))
                                .addOnFailureListener(emitError(emitter))
                                .addOnCompleteListener(emitCompleted(emitter));
                    }

                    @NonNull
                    private OnCompleteListener<Void> emitCompleted(final AsyncEmitter<String> emitter) {
                        return new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (canceled) {
                                    return;
                                }
                                emitter.onCompleted();
                            }
                        };
                    }

                    @NonNull
                    private OnFailureListener emitError(final AsyncEmitter<String> emitter) {
                        return new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                if (canceled) {
                                    return;
                                }
                                emitter.onError(e);
                            }
                        };
                    }

                    @NonNull
                    private OnSuccessListener<Void> emitValue(final AsyncEmitter<String> emitter) {
                        return new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "Before active: " + firebaseRemoteConfig.getString(key));
                                firebaseRemoteConfig.activateFetched();
                                if (canceled) {
                                    return;
                                }
                                final String value = firebaseRemoteConfig.getString(key);
                                Log.d(TAG, "After active: " + value);
                                emitter.onNext(value);
                            }
                        };
                    }

                    @NonNull
                    private AsyncEmitter.Cancellable setFlagCanceled() {
                        return new AsyncEmitter.Cancellable() {
                            @Override
                            public void cancel() throws Exception {
                                canceled = true;
                            }
                        };
                    }
                }, AsyncEmitter.BackpressureMode.BUFFER)
                .timeout(timeOut, TimeUnit.MILLISECONDS)
                .onErrorReturn(defaultValueForKey(key));
    }

    @NonNull
    private Func1<Throwable, String> defaultValueForKey(final String key) {
        return new Func1<Throwable, String>() {
            @Override
            public String call(Throwable throwable) {
                return firebaseRemoteConfig.getString(key);
            }
        };
    }

}
