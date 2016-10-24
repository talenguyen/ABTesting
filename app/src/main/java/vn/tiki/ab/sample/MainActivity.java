package vn.tiki.ab.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import rx.Subscriber;
import vn.tiki.ab.ABNavigator;
import vn.tiki.ab.ABTesting;

/**
 * Created by KenZira on 10/21/16.
 */

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private ABNavigator navigator;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigator = new ABNavigator();

        navigator.registerControllerForKey(OnboardActivity.class, "Onboard");
        navigator.registerControllerForKey(HomeActivity.class, "Home");

        ABTesting.initialize(10);


        ABTesting.getInstance().sync().subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(String s) {

            }
        });

        ABTesting.getInstance().setTimeOut(3000)
                .setDefaultValue(getDefaultValue())
                .setExperimentFor("onboard_navigation")
                .executeAsync().subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {}

                    @Override
                    public void onNext(String value) {
                        Log.e(TAG, "onNext in main: " + value);
                        Intent intent = navigator.getIntentByKey(getApplicationContext(), value);
                        startActivity(intent);
                    }
                });
    }

    private Map<String, Object> getDefaultValue() {
        Map<String, Object> defaultValue = new HashMap<>();
        defaultValue.put("onboard_navigation", "Home");
        return defaultValue;
    }
}
