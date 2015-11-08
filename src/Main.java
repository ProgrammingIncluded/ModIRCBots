import java.io.IOException;

import ircmodbot.*;
import bsh.EvalError;
import bsh.Interpreter;

public class Main {
   public static void main(String[] args){

      // Set debug bot options.
      ModBot.configure("system/log4j.properties");
      Interpreter inter = new Interpreter();
      FileSystem defaultFS = new FileSystem();
      try
      {
    	  String mainPath = defaultFS.getFilePath("script/main.bsh").toString();
    	  inter.source(mainPath);
      }
      catch(EvalError e)
      {
    	  ModBot.LOGGER.error("Script format error. ", e);
      }
      catch(IOException e)
      {
    	  ModBot.LOGGER.error("Script does not exist. ", e);
      }
      catch(Exception e)
      {
    	  ModBot.LOGGER.error("Problem with script: ", e);
      }
   }
}
