package Server;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.TimeUnit;

public class ACSDataCache {
  private final Cache<String, String> cache;

  private ACSDataCache(Cache<String, String> cache) {
    this.cache = cache;
  }

  public void put(String key, String value) {
    cache.put(key, value);
  }

  public String get(String key) {
    return cache.getIfPresent(key);
  }

  public static class Builder {
    private int maxSize;
    private Long expireAfterWrite = null;
    private Long expireAfterAccess = null;
    private TimeUnit timeUnit = TimeUnit.MINUTES;

    public Builder setMaxSize(int maxSize) {
      this.maxSize = maxSize;
      return this;
    }

    public Builder setExpireAfterWrite(long duration, TimeUnit unit) {
      this.expireAfterWrite = duration;
      this.timeUnit = unit;
      return this;
    }

    public Builder setExpireAfterAccess(long duration, TimeUnit unit) {
      this.expireAfterAccess = duration;
      this.timeUnit = unit;
      return this;
    }

    public ACSDataCache build() {
      CacheBuilder<Object, Object> cacheBuilder = CacheBuilder.newBuilder().maximumSize(maxSize);

      if (expireAfterWrite != null) {
        cacheBuilder.expireAfterWrite(expireAfterWrite, timeUnit);
      }
      if (expireAfterAccess != null) {
        cacheBuilder.expireAfterAccess(expireAfterAccess, timeUnit);
      }

      return new ACSDataCache(cacheBuilder.build());
    }
  }
}
