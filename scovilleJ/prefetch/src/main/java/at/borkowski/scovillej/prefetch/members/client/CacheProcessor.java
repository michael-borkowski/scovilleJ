package at.borkowski.scovillej.prefetch.members.client;

import java.util.HashMap;
import java.util.Map;

public class CacheProcessor {
   private Map<String, Long> cache = new HashMap<>();

   public boolean hasFile(String file) {
      return cache.containsKey(file);
   }

   public long getTimestamp(String file) {
      return cache.get(file);
   }

   public void save(String file, byte[] data, long tick) {
      cache.put(file, tick);
   }
}
