package vn.tiki.ab;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.Map;

/**
 * Created by KenZira on 10/20/16.
 */

public class ABNetworkRequestBuilder {

    private String key;
    private int timeOut;
    private int timeExpiration;

    private FirebaseRemoteConfig firebaseRemoteConfig;

    public ABNetworkRequestBuilder() {
        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
    }

    public ABNetworkRequestBuilder setTimeOut(int timeOut) {
        this.timeOut = timeOut;
        return this;
    }

    public ABNetworkRequestBuilder setTimeExpiration(int timeExpiration) {
        this.timeExpiration = timeExpiration;
        return this;
    }

    public ABNetworkRequestBuilder setExperimentFor(String key) {
        this.key = key;
        return this;
    }

    public ABNetworkRequestBuilder setDefaultValue(Map<String, Object> defaultValue) {
        firebaseRemoteConfig.setDefaults(defaultValue);
        return this;
    }

    public ABNetworkRequest build() {
        return new ABNetworkRequest(key, timeOut, timeExpiration);
    }
}
