package ircmodbot;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.io.*;

/**
 * A neccessary mod that manages file input and out put for each custom
 * mods that require file writing or saving.
 * @author Charles
 *
 */
public class FileManagerMod extends Module
{
   public static final int MAX_USERS = 10;

   private ArrayList<String>modFileNames;
   private String FILE_DIR = "modules";
   private String userNamesListDir = "users";
   private String logFileName;
   private int registeredUsers;
   //private static String rootDir;

   public FileManagerMod()
   {
      super("FM");
      moduleName = "File Manager Mod";

      modFileNames = new ArrayList<String>();
      createLogFile();
      getUsers();
      //rootDir = System.getProperty("user.dir");
      if(!checkFileExists(FILE_DIR))
      {
         log("Directory file does not exist");
         System.exit(0);
      }
   }

   public void onMessage(String channel, String sender,
      String login, String hostname, String message)
   {
      // Currently nothing really needed here...
      return ;
   }

   public void onPrivateMessage(String sender, String login, 
      String hostname, String message)
   {
      return ;
   }

   public void onJoin(String channel, String sender, String login,
         String hostname)
   {
      return ;
   }

   public File getModFile(String modName, String user)
   {
      String[] users = getUsers();
      if(!OpHelp.linearSearch(users, user))
      {
         if(users.length > MAX_USERS)
         {
            log("Number of registered Users exceeded: Max " + MAX_USERS);
            return null;
         }
         try
         {
            String userListDir = FILE_DIR+"/"+userNamesListDir+".txt";
            File userFile = getFile(userListDir, true);
            PrintWriter out = new PrintWriter(new FileWriter(userFile, true));
            out.println(user);
            out.close();
         } 
         catch (IOException e)
         {
            log("Unable to register username." + user);
            return null;
         }
      }
      // Find the mod folder.
      if(modFileNames.indexOf(modName) == -1)
      {
         log("No mod registered with such index in File Manager.");
         return null;
      }
      String dir = FILE_DIR+"/"+modName+"/"+user+".txt";
      File modFile = getFile(dir, true);
      return modFile;
   }

   public boolean addModFileName(String mod)
   {
      if(mod == null || mod.length() == 0 || modFileNames.indexOf(mod) != -1)
      {
         return false;
      }
      modFileNames.add(mod);
      return true;
   }

   public boolean log(String str)
   {
      if(str == null)
      {
         return false;
      }
      try
      {
         File dir = new File(logFileName);
         if(!dir.exists())
         {
            dir.createNewFile();
         }

         PrintWriter out = new PrintWriter(new FileWriter(dir, true));
         out.println(str);
         out.close();
      }
      catch(IOException e)
      {
         return false;
      }
      return true;
   }

   public int getNumUsers()
   {
      return registeredUsers;
   }

   // Gets users and updates registeredUsers value.
   public String[] getUsers()
   {
      ArrayList<String>userList = new ArrayList<String>();
      try
      {
         File users = getFile(FILE_DIR+"/"+userNamesListDir+".txt", true);
         BufferedReader br = new BufferedReader(new FileReader(users));
         // Did not use custom countLine function, even though faster, since
         // this allows us to get string info better and simpler code.
         String user = "";
         while((user = br.readLine()) != null)
         {
            userList.add(user);
         }
         br.close();
      }
      catch (FileNotFoundException e)
      {
         log("Critical Error: User's data not found.");
         System.exit(0);
      }
      catch (IOException e)
      {
         log("Critical Error: User's data cannot be read.");
         System.exit(0);
      }

      String[] strArray = new String[userList.size()];
      strArray = userList.toArray(strArray);
      return strArray;
   }

   // Gets file. If given true, will create subdirectory + file data if
   // does not exist.
   public File getFile(String dir, boolean writeDir)
   {
      File file = new File(dir);
      File parent = file.getParentFile();
      if(!parent.exists() && writeDir == true)
      {
         if(!parent.mkdirs())
         {
            log("Unable to produce directory(s): " + dir);
            file = null;
         }
      }
      else if(!file.exists())
      {
         try
         {
            file.createNewFile();
         } 
         catch (IOException e)
         {
            log("Unable to create new file data:" + dir);
            file = null;
         }
      }
      return file;
   }

   private boolean checkFileExists(String str)
   {
      return new File(str).exists();
   }

   private void createLogFile()
   {
      SimpleDateFormat time = new SimpleDateFormat("MMddyyyykkmmss");
      String reformattedStr = time.format(Calendar.getInstance().getTime());
      logFileName = "log_" + reformattedStr + ".txt"; 
   }
}