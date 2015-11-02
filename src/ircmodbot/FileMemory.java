package ircmodbot;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Simple extension of FileSystem class that allows handling
 * of loading files to and from memory. Type of stored object
 * is in memory. Not a strict memory enforcement as ArrayDeque as internal
 * pointers to keep track of. Give MAX_MEM_DATA is based on powers of 2.
 * @author Charles
 *
 */
public abstract class FileMemory<D> extends FileData<D>
{
   // Max amount of data allowed to store in class. 
   public final int MAX_MEM_DATA;

   // Container to store the Users into memory.
   public ArrayDeque<MutablePair<String, D>>dataMem;

   public FileMemory()
   {
      this(2000);
   }

   public FileMemory(int maxMemData)
   {
      if(maxMemData <= 0)
         MAX_MEM_DATA = 2000;
      else
         MAX_MEM_DATA = maxMemData;
      dataMem = new ArrayDeque<MutablePair<String, D>>(MAX_MEM_DATA);
   }
   
   /**
    * Overloaded addData function in order to add new Data to memory after
    * adding to file. Writes directly in to memory and file. Does not 
    * do memory first.
    */
   public boolean addData(String key, D data)
   {
      // Perhaps one can implement write to memory and then
      // flush to harddrive?
      if(!super.addData(key, data))
         return false;
      return addDataToMemory(key, data);
   }
   
   /**
    * Main function to use in order to modify existing data if exists.
    */
   public boolean forceAddData(String key, D data)
   {
      if(!super.forceAddData(key, data))
         return false;
      D memData = getDataInMemory(key);
      if(memData == null)
         return addDataToMemory(key, data);
      memData = data;
      return true;
   }
   
   
   /**
    * Main function to call in order to grab user. Automatically checks if
    * user is in memory. If not, fetches data in file.
    */
   public D getData(String idKeyValue)
   {
      D result = getDataInMemory(idKeyValue);
      if(result != null)
         return result;
      result = super.getData(idKeyValue);
      addDataToMemory(idKeyValue, result); // Already checks if result is null.
      return result;
   }
   
   public boolean loadDataIntoMemory()
   {
      return loadDataIntoMemory(fileName, idKey, dataKeys);
   }
   
   /**
    * Opens the file and loads the file into memory. Loads from the file
    * sequentially. For safety, keys should be given to the function.
    * TODO: Check idKeyVal
    */
   public boolean loadDataIntoMemory(String fileName, String idKeyVal,
      String dataKeys[])
   {
      if(dataKeys == null || idKeyVal == null || fileName == null)
         return false;

      // Put parse json here.
      JSONArray users = readJsonFile(fileName);
      if(users == null)
         return false;
      dataMem.clear();
      // Read as much data as possible in to the memory. 
      for(int x = 0; x < users.size() && x < MAX_MEM_DATA; ++x)
      {
         JSONObject curUser =  (JSONObject) users.get(x);
         String[] values = new String[dataKeys.length];
         
         for(int i = 0; i < dataKeys.length; ++i)
            values[i] = curUser.get(dataKeys[i]).toString();
         
         String key = curUser.get(idKeyVal).toString();
         MutablePair<String, D> result = new MutablePair<String, D>
            (key, rawDataToData(key, dataKeys, values));
         dataMem.add(result);
      }
      return true;
   }

   /**
    * Grabs the given data from memory. 
    * Primarily a helper function called from getData()
    */
   protected D getDataInMemory(String keyName)
   {
      Iterator<MutablePair<String,D>> it = dataMem.iterator();
      D result = null;
      MutablePair<String, D> search = null;
      while(it.hasNext())
      {
         search = it.next();
         if(search.getKey().equals(keyName))
         {
            result = (D)search.getRight();
            break;
         }
      }
      return result;
   }

   /**
    * Adds given user to runtime memory.
    */
   protected boolean addDataToMemory(String key, D data)
   {
      if(data == null)
         return false;

      if(dataMem.size() >= MAX_MEM_DATA)
         dataMem.removeLast();

      dataMem.addFirst(new MutablePair<String, D>(key, data));
      return true;
   }
   
   // See overriden function.
   public abstract D rawDataToData(String idVal, String[] key, String[] data);
   public abstract Pair<ArrayList<String>, ArrayList<String>> dataToRawData(D data);
   
}
