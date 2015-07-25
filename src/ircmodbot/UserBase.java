package ircmodbot;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.LinkedHashMap;

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
public class UserBase extends FileData<User>
{
   // The logger.
   private static final Logger LOGGER = Logger.getLogger(UserBase.class);
   public static final int MAX_MEM_USERS = 2000;

   private int userCount = 0;
   private FilePermissions filePerm;

   // Container to store the Users into memory.
   public ArrayDeque<MutablePair<String, User>>userMem;

   /**
    * Basic constructor for class. Requires FilePermissions as class
    * deals with file manipulation internally.
    */
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
      addDataToFile("userdata.json",users);
      return user;
   }

   /**
    * Function to get user from the either memory or file. Main
    * function to call.
    */
   public User getUser(String name)
   {
      if(!filePerm.getGlobalPermission(name))
         return null;
      User result = getUserInMemory(name);
      if(result != null)
         return result;
      return getUserInDataBase(name);
   }

   /**
    * Adds given user to runtime memory.
    */
   private boolean addUserToMemory(User usr)
   {
      if(usr == null)
         return false;

      if(userMem.size() > MAX_MEM_USERS)
         userMem.removeLast();

      userMem.addFirst(new MutablePair<String, User>(usr.getName(), usr));
      return true;
   }

   /**
    * Overwritten function from FileSystem to convert raw data to User.
    */
   public User rawDataToData(String idVal, String[] key, String[] data)
   {
      User result = null;
      long id = -1;
      try
      {
         id = Long.parseLong(data[0]);
      }
      catch(NumberFormatException e)
      {
         LOGGER.error("Unable to read Long in UserBase.", e);
         LOGGER.debug("Unable to read Long in UserBase.", e);
      }

      result = new User(idVal, id);
      return result;
   }
   
   public LinkedHashMap<String, String> dataToRawData(User data)
   {
      LinkedHashMap<String, String> result = 
         new LinkedHashMap<String, String>(2);

      result.put("name", data.getName());
      result.put("id", String.valueOf(data.getID()));

      return result;
   }
   
   /**
    * Helper function to get user from file.
    */
   private User getUserInDataBase(String name)
   {
      String dataKeys[] = {"id"};
      User resultUser = getDataInFile("userdata.json", "name", dataKeys);
      
      if(resultUser != null)
         addUserToMemory(resultUser);

      return resultUser;
   }

   /**
    * Helper function to get user from runtime memory.
    * Called from getUser().
    */
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

   /**
    * Reads the user files and updates user count.
    * Clears any old parsed memory objects.
    */
   private void loadUsersIntoMemory()
   {
      // Put parse json here.
      JSONArray users = readJson("userdata.json");
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
}
