package ircmodbot;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Simple extension of FileSystem that supports Json reading for more complex
 * datastructures. Reads directly from files. Use FileMemory for optional
 * load to memory.
 */
public abstract class FileData<D> extends FileSystem
{
   private static final Logger LOGGER = Logger.getLogger(FileData.class);

   /*Default Values for Help Functions*/
   /// Default key used.
   protected String defIdKey = "name";
   /// Default container name used.
   protected String containerName = "users";
   protected String defFileName = "userdata.json";
   protected String defDataKeys[] = {"id"};

   /// Variable to keep track of amount of entries.
   protected int entryCount = 0;

   public abstract D rawDataToData(String idVal, String[] key, String[] data);
   public abstract LinkedHashMap<String, String> dataToRawData(D data);

   /**
    * Simple function to call in order to add new data in to system.
    */
   public boolean addData(String key, D data)
   {
      if(data == null)
         return false;

      D dataInternal = getDataInFile(key);
      if(dataInternal != null)
         return false; // Data is already exists.
      addDataToFile(data);
      return true;
   }

   /**
    * Simple general function to call in order to get data.
    */
   public D getData(String idKey)
   {
      return getDataInFile(idKey);
   }


   /* JSon Parsing Related Functions */
   /**
    * Overwritten function for getDataInFile() for easier use.
    */
   protected D getDataInFile(String keyVal)
   {
      return getDataInFile(defFileName, keyVal, defDataKeys);
   }

   /**
    * Parses all related data from file into an array of strings. String is
    * null if data key does not exist.
    */
   protected D getDataInFile(String fileName, String idKey, String dataKeys[])
   {
      if(dataKeys == null || fileName == null || 
            idKey == null ||  fileName.length() == 0)
         return null;

      String[] values = new String[dataKeys.length];
      boolean found = false;
      JSONArray users = readJson(fileName);
      entryCount = users.size();
      if(users == null)
         return null;
      try
      {
         Iterator<?> it = users.iterator();
         while(it.hasNext() && found == false)
         {
            JSONObject curUser = (JSONObject)it.next();
            if(curUser.get(defIdKey).toString().equalsIgnoreCase(idKey))
            {
               for(int x = 0; x < dataKeys.length; ++x)
                  values[x] = curUser.get(dataKeys[x]).toString();

               found = true;
            }
         }
      }
      catch(NullPointerException e)
      {
         LOGGER.error("Unable to read FileData in FileSystem.", e);
         LOGGER.debug("Unable to read FileData in FileSystem.", e);
      }
      if(found == false)
         return null;

      return rawDataToData(idKey, dataKeys, values);
   }

   /**
    * Overriden helper function for the addDataToFile().
    */
   protected void addDataToFile(D data)
   {
      if(defFileName == null || defFileName.length() == 0)
         return ;
      // Please don't hate me...
      D internalData[] = (D[]) new Object[1];
      internalData[0] = data;
      addDataToFile(defFileName, internalData);
   }

   /**
    * Function to add data to json file. Data given is in array format
    * as it is more beneficial. 
    */
   @SuppressWarnings("unchecked")
   protected void addDataToFile(String fileName,D data[])
   {
      if(data == null)
         return ;
      JSONArray userJSON = new JSONArray();
      for(D indData : data)
      {
         ++entryCount;
         LinkedHashMap<String, String> rawData = dataToRawData(indData);
         JSONObject userData = new JSONObject();
         ArrayList<String> names = new ArrayList<String>(rawData.keySet());
         ArrayList<String> values = new ArrayList<String>(rawData.values());
         for(int x = 0; x < rawData.size(); ++x)
         {
            userData.put(names.get(x), values.get(x));
         }
         userJSON.add(userData);
      }
      // Add old data back.
      JSONArray finalArray = readJson(fileName);
      if(finalArray == null)
         finalArray = userJSON;
      else 
         appendJsonArray(finalArray, userJSON);

      JSONObject container = new JSONObject();
      container.put("users", finalArray);
      try
      {
         File file = this.getFilePath(fileName);
         if(!file.exists())
            file.createNewFile();
         FileWriter fileWriter = new FileWriter(file);
         fileWriter.write(container.toJSONString());
         fileWriter.flush();
         fileWriter.close();
      }
      catch (IOException e)
      {
         LOGGER.error("Unable to open" + fileName + " in FileData.", e);
         System.exit(1);
      }
   }

   /**
    * Helper function to parse Json specific database file.
    * Called by getUserInDataBase()
    */
   public JSONArray readJson(String filename)
   {
      JSONParser parser = new JSONParser();
      JSONObject obj = null;
      try
      {
         obj = (JSONObject) parser.parse(
               new FileReader(this.getFilePath(filename)));
      }
      catch(IOException e)
      {
         LOGGER.error("Unable to read "+filename+" in FileData", e);
         LOGGER.debug("Unable to read "+filename+" in FileData", e);
      } 
      catch (ParseException e)
      {
         LOGGER.error("Unable to read "+filename+" in FileData", e);
         LOGGER.debug("Unable to read "+filename+" in FileData", e);
      }
      JSONArray users = null;
      if(obj != null)
         users = (JSONArray) obj.get("users");
      return users;
   }

   /**
    * Appends one JSONArray to another. Simple helper function.
    */
   @SuppressWarnings("unchecked")
   protected
   void appendJsonArray(JSONArray array, JSONArray value)
   {
      if(array == null || value == null)
         return;
      for(Object obj : value)
      {
         array.add(obj);
      }
   }

   /*Mutators*/
   public boolean setDefIdKey(String defIdKey)
   {
      if(defIdKey == null || defIdKey.length() == 0)
         return false;
      this.defIdKey = defIdKey;
      return true;
   }

   public String getDefIdKey()
   {
      return defIdKey;
   }

   public boolean setContainerName(String value)
   {
      if(value == null || value.length() == 0)
         return false;
      this.containerName = value;
      return true;
   }

   public String getContainerName()
   {
      return containerName;
   }

   public boolean setDefFileName(String value)
   {
      if(value == null || value.length() == 0)
         return false;
      this.defFileName = value;
      return true;
   }

   public String getDefFileName()
   {
      return defFileName;
   }

   public boolean setDefDataKeys(String[] value)
   {
      if(value == null || value.length == 0)
         return false;
      this.defDataKeys = value;
      return true;
   }

   public String[] getDefDataKeys()
   {
      return defDataKeys;
   }
}
