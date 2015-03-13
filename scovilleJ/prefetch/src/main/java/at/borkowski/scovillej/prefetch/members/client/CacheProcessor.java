package at.borkowski.scovillej.prefetch.members.client;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the cache sub-processor of {@link FetchClient}. It is responsible
 * for caching results of requests.
 */
public class CacheProcessor {
   private Map<String, Long> cache = new HashMap<>();

   /**
    * Returns <code>true</code> if the cache contains the given file
    * 
    * @param file
    *           the file to be checked
    * @return whether the cache contains the file
    */
   public boolean hasFile(String file) {
      return cache.containsKey(file);
   }

   /**
    * Returns the timestamp with which the file has been saved. Behavior is
    * undefined (currently throws a {@link NullPointerException}) if the file is
    * not stored (see {@link #hasFile(String)}).
    * 
    * @param file
    *           the file to be checked
    * @return the timestamp at which the file has been saved
    */
   public long getTimestamp(String file) {
      return cache.get(file);
   }

   /**
    * Saves a file to the cache.
    * 
    * @param file
    *           the file saved
    * @param data
    *           the content
    * @param tick
    *           the current tick (timestamp of the file)
    */
   public void save(String file, byte[] data, long tick) {
      cache.put(file, tick);
   }
}
