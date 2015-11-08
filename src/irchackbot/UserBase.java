package irchackbot;

import java.util.ArrayList;


import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;

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
public class UserBase extends FileMemory<User>
{
   // The logger.
   private static final Logger LOGGER = Logger.getLogger(UserBase.class);
   public static final int MAX_MEM_USERS = 2000;

   private FilePermissions filePerm;

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

      // Set up FileData attributes.
      setDefIdKey("name");
      setContainerName("users");
      setDefFileName("userdata.json");

      String[] dataKeys = {"id"};
      setDefDataKeys(dataKeys);

      loadUsersIntoMemory();
      filePerm = fm;
   }

   /**
    * Function to register given name with the system. Returns user info.
    */
   public boolean registerUser(String name)
   {
      if(!filePerm.getGlobalPermission(name) || name == null)
         return false;
      User user = getUser(name);
      if(user != null) // User registered.
         return false;

      user = new User(name, 1+entryCount);
      return addData(name, user);
   }

   /**
    * Function to get user from the either memory or file. Main
    * function to call.
    */
   public User getUser(String name)
   {
      if(!filePerm.getGlobalPermission(name))
         return null;
      return getData(name);
   }

   /**
    * Adds given user to runtime memory.
    */
   private boolean addUserToMemory(User usr)
   {
      return addDataToMemory(usr.getName(), usr);
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

   public Pair<ArrayList<String>, ArrayList<String>> dataToRawData(User data)
   {
	  ArrayList<String> names = new ArrayList<String>();
	  ArrayList<String> values = new ArrayList<String>();

	  names.add("name"); values.add(data.getName());
	  names.add("id"); values.add(String.valueOf(data.getID()));
	  
      Pair<ArrayList<String>, ArrayList<String>> result = 
    		  new ImmutablePair<ArrayList<String>, ArrayList<String>>(names, values);
      
      return result;
   }


   /**
    * Reads the user files and updates user count.
    * Clears any old parsed memory objects.
    */
   private void loadUsersIntoMemory()
   {
      loadDataIntoMemory();
   }
}
