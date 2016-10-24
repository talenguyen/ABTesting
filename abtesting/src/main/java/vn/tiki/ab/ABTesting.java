package vn.tiki.ab;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import rx.AsyncEmitter;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subjects.BehaviorSubject;

/**
 * Created by KenZira on 10/21/16.
 */

public class ABTesting {

    private static final String TAG = "MainActivity";

    private static ABTesting instance;
    private int timeOut;
    private int cacheExpiration;
    private String key;

    private FirebaseRemoteConfig firebaseRemoteConfig;

    private BehaviorSubject<Boolean> resultSubject;
    private Observable<String> syncObservable;

    private boolean isSynchronizing;

    private ABTesting(int timeExpiration) {
        this.firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        this.cacheExpiration = timeExpiration;
        this.resultSubject = BehaviorSubject.create();
        this.syncObservable = Observable.empty();
    }

    public static void initialize(int timeExpiration) {
        if (instance == null) {
            instance = new ABTesting(timeExpiration);
        }
    }

    public static ABTesting getInstance() {
        if (instance == null) {
            throw new NullPointerException("Must call initialize first");
        }
        return instance;
    }

    public ABTesting setTimeOut(int timeOut) {
        this.timeOut = timeOut;
        return this;
    }

    public ABTesting setDefaultValue(Map<String, Object> defaultValue) {
        firebaseRemoteConfig.setDefaults(defaultValue);
        return this;
    }

    public ABTesting setExperimentFor(String key){
        this.key = key;
        return this;
    }

    public Observable<String> sync() {
        isSynchronizing = true;
        syncObservable = Observable.fromEmitter(
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

                                Log.e(TAG, "onComplete: " );

                                emitter.onCompleted();

                                if (task.isSuccessful()) {
                                    resultSubject.onNext(true);
                                }

                                isSynchronizing = false;
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
                                resultSubject.onError(e);
                            }
                        };
                    }

                    @NonNull
                    private OnSuccessListener<Void> emitValue(final AsyncEmitter<String> emitter) {
                        return new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                firebaseRemoteConfig.activateFetched();
                                if (canceled) {
                                    return;
                                }

                                Log.e(TAG, "onSuccess: " + firebaseRemoteConfig.getString(key) );
                                emitter.onNext(firebaseRemoteConfig.getString(key));
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
                }, AsyncEmitter.BackpressureMode.BUFFER);
        return syncObservable;
    }

    public Observable<String> executeAsync(){

        int status = firebaseRemoteConfig.getInfo().getLastFetchStatus();
        if (status == FirebaseRemoteConfig.LAST_FETCH_STATUS_NO_FETCH_YET && isSynchronizing) {
            Log.e(TAG, "getExperimentFor: " + "synchronizing");
            return getAsyncObservable();
        } else if (status == FirebaseRemoteConfig.LAST_FETCH_STATUS_NO_FETCH_YET) {
            Log.e(TAG, "getExperimentFor: " + "start-sync");
            return sync().onErrorReturn(defaultValueForKey());
        } else if (status == FirebaseRemoteConfig.LAST_FETCH_STATUS_FAILURE) {
            Log.e(TAG, "getExperimentFor: " + "failure");
            return sync().onErrorReturn(defaultValueForKey());
        } else if (status == FirebaseRemoteConfig.LAST_FETCH_STATUS_THROTTLED) {
            Log.e(TAG, "getExperimentFor: " + "throttle");
            return sync().onErrorReturn(defaultValueForKey());
        } else {
            Log.e(TAG, "getExperimentFor: " + "success");
            return Observable.just(firebaseRemoteConfig.getString(key));
        }

    }

    public Observable<String> execute() {

        int status = firebaseRemoteConfig.getInfo().getLastFetchStatus();
        if (status == FirebaseRemoteConfig.LAST_FETCH_STATUS_NO_FETCH_YET && isSynchronizing) {
            Log.e(TAG, "getExperimentFor: " + "synchronizing");
            return getTimedOutRunningTask();
        } else if (status == FirebaseRemoteConfig.LAST_FETCH_STATUS_NO_FETCH_YET) {
            Log.e(TAG, "getExperimentFor: " + "start-sync");
            return sync().timeout(timeOut, TimeUnit.MILLISECONDS).onErrorReturn(defaultValueForKey());
        } else if (status == FirebaseRemoteConfig.LAST_FETCH_STATUS_FAILURE) {
            Log.e(TAG, "getExperimentFor: " + "failure");
            return sync().timeout(timeOut, TimeUnit.MILLISECONDS).onErrorReturn(defaultValueForKey());
        } else if (status == FirebaseRemoteConfig.LAST_FETCH_STATUS_THROTTLED) {
            Log.e(TAG, "getExperimentFor: " + "throttle");
            return sync().timeout(timeOut, TimeUnit.MILLISECONDS).onErrorReturn(defaultValueForKey());
        } else {
            Log.e(TAG, "getExperimentFor: " + "success");
            return Observable.just(firebaseRemoteConfig.getString(key));
        }
    }

    private Observable<String> getAsyncObservable(){
        return resultSubject.map(new Func1<Boolean, String>() {
            @Override
            public String call(Boolean aBoolean) {
                return firebaseRemoteConfig.getString(key);
            }
        }).onErrorReturn(defaultValueForKey());
    }

    private Observable<String> getTimedOutRunningTask() {
        return syncObservable.timeout(timeOut,TimeUnit.MILLISECONDS).onErrorReturn(defaultValueForKey());
    }

    @NonNull
    private Func1<Throwable, String> defaultValueForKey() {
        return new Func1<Throwable, String>() {
            @Override
            public String call(Throwable throwable) {
                return firebaseRemoteConfig.getString(key);
            }
        };
    }
}
