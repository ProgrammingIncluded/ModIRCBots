package ircmodbot;

import java.util.ArrayList;

import org.apache.commons.lang3.tuple.Pair;


/**
 * Class for managing Users in the chat. Depending on use,
 * keeps track of name, and various other tokens. Try to not use these
 * tokens but rather store info in each module.
 * 
 * TODO: Perhaps add user extra data into user file.
 * 
 * @author Charles
 *
 */
public class User
{
   private String name;
   private long ID;
   private ArrayList<Pair<Integer, Integer>> tokens;
   
   User(String name, long ID)
   {
      if(!setName(name))
      {
         name = "SKK";
      }
      else if(!setID(ID))
      {
         ID = 0;
      }
   }
   
   public boolean setID(long ID)
   {
      this.ID = ID;
      return true;
   }
   
   public boolean setName(String name)
   {
      if(name == null || name.length() <= 0)
         return false;
      this.name = name;
      return true;
   }
   
   public long getID()
   {
      return ID;
   }
   
   public String getName()
   {
      return name;
   }
   
   /*Overwritten Functions*/
   public boolean equals(User usr)
   {
      // 
      if(this.name.equalsIgnoreCase(usr.name))
         return true;
      return false;
   }
}
