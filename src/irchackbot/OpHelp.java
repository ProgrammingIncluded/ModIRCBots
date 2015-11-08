package irchackbot;

/**
 * Class for helping with some parsing with commands. 
 */
public class OpHelp
{
   // Simple classic linear search for in an array.
   static public boolean linearSearch(Object array[], Object obj)
   {
      if(obj == null || array == null || array.length == 0)
      {
         return false;
      }
      for(int x = 0; x < array.length; ++x)
      {
         if(array[x].equals(obj))
         {
            return true;
         }
      }
      return false;
   }

   /*
    * Custom subString method for those non exception returns.
    * Returns null if index is negative.
    */
   static public String subString(String str, int index, int endex)
   {
	  if(index < 0 || endex < 0)
		  return null;
	  
      if(str == null || str.length() < index || index == endex
         || endex > str.length())
      {
         return str;
      }
   
      return str.substring(index,endex);
   }

   /**
    * Helper function to check whether or not a command has been issued
    * within the given text. Is case sensitive. Returns str with removed
    * command if applicable or returns null.
    */
   static public String command(String str, String command)
   {
      String result = "";
      if(command == "" || command.length() <= 0)
      {
         return str;
      }
      result = subString(str, 0, command.length());
      if(result.equals(command))
      {
         // Check if next value is space or only this value..
         if(str.length() == command.length() 
            || (str.length() >= command.length() + 1 
            && str.charAt(command.length()) == ' '))
            return subString(str, command.length()+1, str.length());
            
         return "";
      }
      return "";
   }
}
