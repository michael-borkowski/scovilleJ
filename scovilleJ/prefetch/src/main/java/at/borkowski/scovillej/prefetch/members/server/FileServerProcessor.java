package at.borkowski.scovillej.prefetch.members.server;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the file server sub-processor of {@link FetchServer}. It is
 * responsible for providing files by name.
 */
public class FileServerProcessor {
   private final Map<String, Integer> files = new HashMap<>();

   /**
    * Adds files to the file server. Files are described by their file name and
    * length (content is irrelevant).
    * 
    * @param files
    *           the files to add
    */
   public void addFiles(Map<String, Integer> files) {
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
    * The length of a file from the file server
    * 
    * @param file
    *           the file name to fetch
    * @return the file length
    */
   public int getFileLength(String file) {
      return this.files.get(file);
   }

}
