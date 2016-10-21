package vn.tiki.ab;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.util.ArrayMap;

/**
 * Created by KenZira on 10/20/16.
 */

public class ABNavigator {

    private final ArrayMap<String, Class<? extends Activity>> controllerMap = new ArrayMap<>();

    public void registerControllerForKey(Class<? extends Activity> controller, String key) {
        controllerMap.put(key, controller);
    }

    public void startActivityByKey(Context context, String key) {
        final Class<? extends Activity> activityClass = controllerMap.get(key);
        final Intent intent = new Intent(context, activityClass);
        context.startActivity(intent);
    }

    public Intent getIntentByKey(Context context, String key) {
        final Class<? extends Activity> activityClass = controllerMap.get(key);
        return new Intent(context, activityClass);
    }
}
