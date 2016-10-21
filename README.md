# NoAdapter

[![Build Status](https://travis-ci.org/tikivn/NoAdapter.svg?branch=master)](https://travis-ci.org/tikivn/NoAdapter)

Too much boilerplate and effort to implement a list using RecyclerView. But, most of them can be omitted. This library will take care of the heavy part and leave you the easy part.

![](logo.png)

## Sample Usage
  * `Data Binding`: *We take advantage of `Data Binding` to avoid to implement ViewHolder then `Data Binding` is required*.
    
    ```gradle
    android {
      dataBinding {
        enabled true
      }
    }
    ```
  * Layout
    
    ```xml
    <?xml version="1.0" encoding="utf-8"?>
    <layout xmlns:android="http://schemas.android.com/apk/res/android">
    
      <data>
    
        <variable
          name="onClick"
          type="android.view.View.OnClickListener"/>
    
        <variable
          name="item"
          type="vn.tiki.noadapter.sample.entity.User"/>
    
      </data>
    
      <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="@{item.name}"
        android:onClick="@{onClick}"
        />
    
    </layout>
    ```
    
    * `item`: - data for binding
    * `onClick`: - set for views which you want to handle `OnClickListener`       
         
  * Build Adapter
    
    ```java
    adapter = new OnlyAdapter.Builder()
            .layoutSelector(new LayoutSelector() {
              @Override public int layoutForType(int type) {
                return R.layout.item;
              }
            })
            .onItemClickListener(new OnItemClickListener() {
              @Override public void onItemClick(View view, Object item, int position) {
                switch(view.getId()) {
                  case R.id.text1:
                    Log.d(TAG, "text1 is clicked");
                    break;
                  case R.id.text2:
                    Log.d(TAG, "text2 is clicked");
                    break;
                }
              }
            })
            .build();
    recyclerView.setAdapter(adapter);
    ```
   
See more in the sample

## Download

Download [the latest JAR][1] or grab via Gradle:
```groovy
compile 'vn.tiki.noadapter:noadapter:1.0.0'
```
or Maven:
```xml
<dependency>
  <groupId>vn.tiki.noadapter</groupId>
  <artifactId>noadapter</artifactId>
  <version>1.0.0</version>
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
