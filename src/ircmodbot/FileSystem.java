package ircmodbot;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;

/**
 * Interface to implement when you want to 
 * work with files in system.
 * @author Charles
 *
 */
public class FileSystem
{
   /// Default root directory relative to program directory.
   static final String DEF_SYSTEM_FOLDER = "system";
   
   /// Var. to hold location of working directory.
   private File root = new File(DEF_SYSTEM_FOLDER);

   /**
    * Creates a file path to file relative to root.
    * Throws IO Exception if file does not exist.
    * @return
    */
   public File getFilePath(String relPath) throws IOException
   {
      File result = new File(root.getPath(), relPath);
      if(result.exists() == false)
         throw new IOException();
      return result;
   }

   public boolean setRoot(File root)
   {
      if(root == null || root.exists() == false)
         return false;
      this.root = root;
      return true;
   }
   
   public File getRoot()
   {
      return root;
   }
}
