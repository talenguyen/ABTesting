package vn.tiki.ab.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import java.util.concurrent.TimeUnit;
import rx.functions.Action1;
import vn.tiki.ab.ABTesting;

/**
 * Created by KenZira on 10/21/16.
 */

public class MainActivity extends AppCompatActivity {

  private static final String TAG = "MainActivity";
  private ABTesting ab;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    ab = ABApplication.get(this).getAb();
  }

  public void fetch(View view) {
    ab.request()
        .key("onboard_navigation")
        .timeout(5, TimeUnit.SECONDS)
        .build()
        .stringValue()
        .retry(3)
        .subscribe(new Action1<String>() {
          @Override public void call(String s) {
            Log.d(TAG, "call: " + s);
          }
        });
  }
}
