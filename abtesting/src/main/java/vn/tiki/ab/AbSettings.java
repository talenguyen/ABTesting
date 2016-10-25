package vn.tiki.ab;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by Giang Nguyen on 10/24/16.
 */

public interface AbSettings {

  boolean debug();

  Map<String, Object> defaults();

  long cacheExpiration();

  class Builder {
    private boolean debug;
    private long cacheExpiration;
    private Map<String, Object> defaults;

    public Builder debug(boolean debug) {
      this.debug = debug;
      return this;
    }

    public Builder cacheExpiration(long cacheExpiration, TimeUnit timeUnit) {
      this.cacheExpiration = TimeUnit.SECONDS.convert(cacheExpiration, timeUnit);
      return this;
    }

    public Builder defaults(Map<String, Object> defaults) {
      this.defaults = defaults;
      return this;
    }

    public AbSettings build() {
      if (defaults == null) {
        throw new NullPointerException("defaults must not be null");
      }

      return new AbSettings() {
        @Override public boolean debug() {
          return debug;
        }

        @Override public long cacheExpiration() {
          return cacheExpiration;
        }

        @Override public Map<String, Object> defaults() {
          return defaults;
        }
      };
    }
  }
}
