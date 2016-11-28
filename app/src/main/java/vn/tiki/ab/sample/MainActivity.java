package vn.tiki.ab.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import java.util.concurrent.TimeUnit;
import rx.Subscription;
import rx.functions.Action1;
import vn.tiki.ab.AbTesting;
import vn.tiki.ab.Parser;

/**
 * Created by KenZira on 10/21/16.
 */

public class MainActivity extends AppCompatActivity {

  private static final String TAG = "MainActivity";
  private AbTesting ab;
  private Subscription subscription;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    ab = ABApplication.get(this).getAb();
  }

  @Override protected void onResume() {
    super.onResume();
    subscription = ab.request(this)
        .key("onboard_navigation")
        .timeout(5, TimeUnit.SECONDS)
        .build()
        .parseValue(Parser.BOOLEAN)
        .subscribe(new Action1<Boolean>() {
          @Override public void call(Boolean value) {
            if (value) {
              startActivity(new Intent(MainActivity.this, OnboardActivity.class));
            } else {
              startActivity(new Intent(MainActivity.this, HomeActivity.class));
            }
            finish();
          }
        });
  }

  @Override protected void onPause() {
    super.onPause();
    if (subscription != null) {
      subscription.unsubscribe();
    }
  }

  public void fetch(View view) {


  }
}
