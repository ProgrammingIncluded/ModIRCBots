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

public abstract class FileData<D> extends FileSystem
{
   private static final Logger LOGGER = Logger.getLogger(FileData.class);
   /// Default key used. TODO: Create Getter and Setter
   private String idKey = "name";
   /// Default container name used.
   private String containerName = "users";
   /// Variable to keep track of amount of entries.
   private int entryCount = 0;
   
   public abstract D rawDataToData(String idVal, String[] key, String[] data);
   public abstract LinkedHashMap<String, String> dataToRawData(D data);
   
   /* JSon Parsing Related Functions */
   /**
    * Parses all related data from file into an array of strings. String is
    * null if data key does not exist.
    */
   protected D getDataInFile(String fileName, String idKeyVal, String dataKeys[])
   {
      String[] values = new String[dataKeys.length];
      boolean found = false;
      JSONArray users = readJson(fileName);
      if(users == null)
         return null;
      try
      {
         Iterator<?> it = users.iterator();
         while(it.hasNext() && found == false)
         {
            JSONObject curUser = (JSONObject)it.next();
            if(curUser.get(idKey).toString().equalsIgnoreCase(idKeyVal))
            {
               for(int x = 0; x < dataKeys.length; ++x)
               {
                  values[x] = curUser.get(dataKeys[x]).toString();
               }
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

      return rawDataToData(idKeyVal, dataKeys, values);
   }   
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
         LOGGER.error("Unable to open FileData.", e);
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
   private void appendJsonArray(JSONArray array, JSONArray value)
   {
      if(array == null || value == null)
         return;
      for(Object obj : value)
      {
         array.add(obj);
      }
   }
}
