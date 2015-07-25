package ircmodbot;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.apache.commons.lang3.tuple.MutablePair;

/**
 * Simple extension of FileSystem class that allows handling
 * of loading files to and from memory. Type of stored object
 * is in memory.
 * @author Charles
 *
 */
public abstract class FileMemory<K, D> extends FileData<D>
{
   // Container to store the Users into memory.
   public ArrayDeque<MutablePair<K, D>>userMem;
   
   FileMemory()
   {
      
      
   }
   
   public void loadDataIntoMemory()
   {
      
      
   }
   
   /**
    * Grabs the given data from memory. 
    * Primarily a helper function called from getData()
    */
   protected D getDataInMemory(K name)
   {
      Iterator<MutablePair<K,D>> it = userMem.iterator();
      D result = null;
      MutablePair<K, D> search = null;
      while(it.hasNext())
      {
         search = it.next();
         if(search.getKey().equals(name))
         {
            result = (D)search.getRight();
            break;
         }
      }
      return result;
   }

   public abstract D rawDataToData(String idVal, String[] key, String[] data);
   public abstract LinkedHashMap<String, String> dataToRawData(D data);
}
