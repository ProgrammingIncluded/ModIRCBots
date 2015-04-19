package ircmodbot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * Class to allow permissions to specified users registered in the
 * database. Any user not in data base should not be able to get access
 * to modifying data. Each Bot has their own unique permissions.
 * File Manager Permissions.
 * 
 * Blacklist takes dominance, if duplicate names.
 * Reads the lists every time to check a user's permission. For now...
 * @author Charles
 *
 */
public class FilePermissions extends FileSystem
{
   static final Logger LOGGER = Logger.getLogger(FilePermissions.class);
   /* Static Const. Variables */
   // Assumed in root folder.
   static final String DEF_WHITELIST_FILE = "whitelist.txt";
   static final String DEF_BLACKLIST_FILE = "blacklist.txt";

   /* Local Var*/
   private File whiteListFile = null;
   private File blackListFile = null;

   FilePermissions(String whiteListPath, String blackListPath) 
   {
      if(!setWhiteList(whiteListPath))
      {
         if(!setWhiteList(DEF_WHITELIST_FILE))
         {
            if(LOGGER.isDebugEnabled())
            {
               LOGGER.debug("Unable to find default white list, "
                     + "using only mod permissions.");
            }
         }
      }
      
      if(!setBlackList(blackListPath))
      {
         if(!setBlackList(DEF_BLACKLIST_FILE))
         {
            if(LOGGER.isDebugEnabled())
            {
               LOGGER.debug("Unable to find default black list.");
            }
         }
      }
   }
   
   
   /**
    * Gets the local mod permission. With specified root file for the
    * mod file.
    * @return
    */
   /*
   public boolean getModPermission(User user, Module mod, )
   {
      
   }
   */
   /**
    * Gets the global permission of the user. If user is 
    * in both files, Blacklist takes dominance.
    */
   public boolean getGlobalPermission(User user)
   {
      return getGlobalPermission(user.getName());
   }
   
   /**
    * Gets the global permission of the user. If user is 
    * in both files, Blacklist takes dominance.
    */
   public boolean getGlobalPermission(String username)
   {
      //Check blacklist first.
      ArrayList<String> names = readBlackList();
      for(String name : names)
      {
         if(name.equals(username))
         {
            return false;
         }
      }
      
      names = readWhiteList();
      for(String name : names)
      {
         if(name.equals(username))
         {
            return true;
         }
      }
      // Check if whitelist empty.
      if(names.size() == 0)
         return true;
      else
         return false; // Whitelist is used, so user not in list.
   }

   /**
    * Sets the white list of the program.
    */
   public boolean setWhiteList(String relFilePath)
   {
      if(relFilePath == null || relFilePath.length() <= 0 
            || relFilePath.equals(blackListFile))
            return false;
      try
      {
         whiteListFile = getFilePath(relFilePath);
      }
      catch(IOException e)
      {
         LOGGER.error("Unable to set Whitelist.", e);
         return false;
      }
      return true;
   }
   
   /**
    * Sets the black list of the program.
    */
   public boolean setBlackList(String relFilePath)
   {
      if(relFilePath == null || relFilePath.length() <= 0 
         || relFilePath.equals(whiteListFile))
         return false;
      try
      {
         blackListFile = getFilePath(relFilePath);
      }
      catch(IOException e)
      {
         LOGGER.error("Unable to set Blacklist.", e);
         return false;
      }
      return true;
   }

   /**
    * Returns a list of names for white listed people.
    * If nothing is returned, that means no names are in
    * white list or bad format.
    */
   public ArrayList<String> readWhiteList()
   {
      return parseList(whiteListFile);
   }
   

   /**
    * Returns a list of names for black listed people.
    * If nothing is returned, that means no names are in
    * black list or bad format.
    */
   public ArrayList<String> readBlackList()
   {
      return parseList(blackListFile);
   }
   /**
    * Helper function to parse lists.
    */
   private ArrayList<String> parseList(File file)
   {
      ArrayList<String> result = new ArrayList<String>();
      if(file == null || file.exists() == false)
         return result;
      
      BufferedReader bufferReader = null;
      try
      {
         bufferReader = new BufferedReader(
            new FileReader(file.getAbsolutePath()));
         String line = "";
         while((line = bufferReader.readLine())!=null)
         {
            result.add(line);
         }
         bufferReader.close();
      }
      catch(IOException e)
      {
         LOGGER.error("Unable to read permissions file.", e);
         return result;
      }
      return result;
   }
}
