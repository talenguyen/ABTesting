# ABTesting

[![Build Status](https://travis-ci.org/dbof10/ABTesting.svg?branch=master)](https://travis-ci.org/dbof10/ABTesting)

Solution to Remote config Firebase in reactive way

![](logo.png)

## Sample Usage
 
 ```java
 
 public class ABApplication extends Application {
 
  @Override 
  public void onCreate() {
    super.onCreate();
    
    FirebaseApp.initializeApp(this);

    ABTesting ab = new ABTesting(new AbSettings.Builder()
        .debug(BuildConfig.DEBUG)
        .cacheExpiration(2, TimeUnit.SECONDS)
        .defaults(defaultAbValues())
        .build());
  }
}

public class MainActivity extends AppCompatActivity {

  public void fetch(View view) {
    ABApplication.get(this).getAb()
        .request()
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

 ```
  

## Download

Download [the latest JAR][1] or grab via Gradle:
```groovy
compile 'vn.tiki.ab:abtesting:1.0.0-SNAPSHOT'
```
or Maven:
```xml
<dependency>
  <groupId>vn.tiki.ab</groupId>
  <artifactId>abtesting</artifactId>
  <version>1.0.0-SNAPSHOT</version>
</dependency>
```

Snapshots of the development version are available in [Sonatype's `snapshots` repository][snap].



## ProGuard

No specific



## License

    Copyright 2016 Tiki Corp

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.


 [1]: https://search.maven.org/remote_content?g=vn.tiki.noadapter&a=noadapter&v=LATEST
 [snap]: https://oss.sonatype.org/content/repositories/snapshots/
