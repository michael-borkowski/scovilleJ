package at.borkowski.scovillej.prefetch.members.server;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the file server sub-processor of {@link FetchServer}. It is
 * responsible for providing files by name.
 */
public class FileServerProcessor {
   private final Map<String, byte[]> files = new HashMap<>();

   /**
    * Adds files to the file server
    * 
    * @param files
    *           the files to add
    */
   public void addFiles(Map<String, byte[]> files) {
      this.files.putAll(files);
   }

   /**
    * Checks whether a file with the given name is present.
    * 
    * @param file
    *           the file name to check
    * @return <code>true</code> if the file is present
    */
   public boolean hasFile(String file) {
      return this.files.containsKey(file);
   }

   /**
    * Returns a file from the file server
    * 
    * @param file
    *           the file name to fetch
    * @return the file data
    */
   public byte[] getFile(String file) {
      return this.files.get(file);
   }

}
