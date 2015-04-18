package ircmodbot;


/**
 * Class for managing Users in the chat. Depending on use,
 * keeps track of name, and various other tokens. Use for tracking info
 * in each mod.
 * @author Charles
 *
 */
public class User
{
   private String name;
   
   User(String name)
   {
      if(!setName(name))
      {
         name = "SKK";
      }
   }
   
   public boolean setName(String name)
   {
      if(name == null || name.length() <= 0)
         return false;
      this.name = name;
      return true;
   }
   
   public String getName()
   {
      return name;
   }
}
