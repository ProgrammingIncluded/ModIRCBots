package ircmodbot;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Iterator;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Class in charge of assigning each user a unique ID.
 * Due to the nature of Twitch and/or IRC, loading all users into RAM may not
 * be feasible, as such, UserBase will read from file dynamically and load
 * a max amount of users into memory.
 * 
 * Must require FilePermissions in order to work.
 * @author Charles
 *
 */
public class UserBase extends FileSystem
{
   // The logger.
   private static final Logger LOGGER = Logger.getLogger(UserBase.class);
   public static final int MAX_MEM_USERS = 2000;
   
   private int userCount = 0;
   private FilePermissions filePerm;
   
   // Container to store the Users into memory.
   public ArrayDeque<MutablePair<String, User>>userMem; // SO MANY CHOICES :>
   
   UserBase(FilePermissions fm)
   {
      if(fm == null)
      {
         LOGGER.error("Unable to setup FilePermissions in UserBase");
         System.exit(1);
      }
      else if(!this.setRoot("system/UserBase"))
      {
         LOGGER.error("Unable to set root directory for UserBase.");
         System.exit(1);
      }
      userMem = new ArrayDeque<MutablePair<String, User>>(MAX_MEM_USERS);
      loadUsersIntoMemory();
      filePerm = fm;
   }
   
   /**
    * Function to register given name with the system. Returns user info.
    * TODO: Remove reading of database twice.
    */
   public User registerUser(String name)
   {
      if(!filePerm.getGlobalPermission(name))
         return null;
      User user = getUser(name);
      if(user != null) // User registered.
         return user;
      
      user = new User(name, 1+userCount);
      User users[] = {user};
      addUsersIntoDataBase(users);
      return user;
   }
   
   public User getUser(String name)
   {
      if(!filePerm.getGlobalPermission(name))
         return null;
      User result = getUserInMemory(name);
      if(result != null)
         return result;
      return getUserInDataBase(name);
   }
   
   private boolean addUserToMemory(User usr)
   {
      if(usr == null)
         return false;

      if(userMem.size() > MAX_MEM_USERS)
         userMem.removeLast();

      userMem.addFirst(new MutablePair<String, User>(usr.getName(), usr));
      return true;
   }
   
   private User getUserInDataBase(String name)
   {
      User result = null;
      JSONArray users = readJson();
      if(users == null)
         return null;
      try
      {
         Iterator<?> it = users.iterator();
         while(it.hasNext())
         {
            JSONObject curUser = (JSONObject)it.next();
            if(curUser.get("name").toString().equalsIgnoreCase(name))
            {
               long id = Long.parseLong(curUser.get("id").toString());
               result = new User(name, id);
               addUserToMemory(result);
               break;
            }
         }
      }
      catch(NullPointerException e)
      {
         LOGGER.error("Unable to read UserData in UserBase.", e);
         LOGGER.debug("Unable to read UserData in UserBase", e);
      }
      catch(NumberFormatException e)
      {
         LOGGER.error("Unable to read UserData in UserBase.", e);
         LOGGER.debug("Unable to read UserData in UserBase", e);
      }
      
      return result;
   }
   
   private User getUserInMemory(String name)
   {
      Iterator<MutablePair<String, User>> it = userMem.iterator();
      User result = null;
      MutablePair<String, User> search = null;
      while(it.hasNext())
      {
         search = it.next();
         if(search.getKey().equalsIgnoreCase(name))
         {
            result = (User)search.getRight();
            break;
         }
      }
      return result;
   }
   
   private JSONArray readJson()
   {
      JSONParser parser = new JSONParser();
      JSONObject obj = null;
      try
      {
         obj = (JSONObject) parser.parse(
            new FileReader(this.getFilePath("userdata.json")));
      }
      catch(IOException e)
      {
         LOGGER.error("Unable to read UserData in UserBase", e);
         LOGGER.debug("Unable to read UserData in UserBase", e);
      } 
      catch (ParseException e)
      {
         LOGGER.error("Unable to read UserData in UserBase.", e);
         LOGGER.debug("Unable to read UserData in UserBase", e);
      }
      JSONArray users = null;
      if(obj != null)
         users = (JSONArray) obj.get("users");
      return users;
   }
   
   /**
    * Reads the user files and updates user count.
    * Clears any old parsed memory objects.
    */
   private void loadUsersIntoMemory()
   {
      // Put parse json here.
      JSONArray users = readJson();
      if(users == null)
         return ;
      userCount = users.size();
      userMem.clear();
      for(int x = 0; x < users.size() && x < MAX_MEM_USERS; ++x)
      {
         JSONObject curUser =  (JSONObject) users.get(x);
         long id = Long.parseLong(curUser.get("id").toString());
         String name = curUser.get("name").toString();
         userMem.add(new MutablePair<String, User>(name, new User(name, id)));
      }
   }
   
   @SuppressWarnings("unchecked")
   private void addUsersIntoDataBase(User users[])
   {
      if(users == null)
         return ;
      JSONArray userJSON = new JSONArray();
      for(User user : users)
      {
         ++userCount;
         JSONObject userData = new JSONObject();
         userData.put("name", user.getName());
         userData.put("id", user.getID());
         userJSON.add(userData);
      }
      // Add old data back.
      JSONArray finalArray = readJson();
      if(finalArray == null)
         finalArray = userJSON;
      else 
         appendJsonArray(finalArray, userJSON);
         
      JSONObject container = new JSONObject();
      container.put("users", finalArray);
      try
      {
         File file = this.getFilePath("userdata.json");
         if(!file.exists())
            file.createNewFile();
         FileWriter fileWriter = new FileWriter(file);
         fileWriter.write(container.toJSONString());
         fileWriter.flush();
         fileWriter.close();
      }
      catch (IOException e)
      {
         LOGGER.error("Unable to open UserData file.", e);
         System.exit(1);
      }
   }
   
   /**
    * Appends one JSONArray to another.
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
