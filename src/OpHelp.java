
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

   // Custom subString method for those non exception returns.
   static public String subString(String str, int index, int endex)
   {
      if(str == null || str.length() < index || index == endex
         || endex > str.length())
      {
         return str;
      }
   
      return str.substring(index,endex);
   }

   static public boolean command(String str, String command)
   {
      if(command == "" || command.length() == 0)
      {
         return true;
      }
      else if(subString(str, 0, command.length()).equalsIgnoreCase(command))
      {
         return true;
      }
      return false;
   }

   static public String removeCommand(String str, String command)
   {
      if(command == "")
      {
         return str;
      }
      else if(str.length() != command.length())
      {
         str = subString(str, command.length()+1, str.length());
      }
      else
      {
         str = "";
      }

      return str;
   }
}
