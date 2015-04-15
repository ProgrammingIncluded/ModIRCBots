package modules;
import ircmodbot.Module;
import ircmodbot.OpHelp;

import java.util.ArrayList;

import org.jibble.pircbot.User;

// Allows everything that the user says to be quoted.
// Used with QuoteMod.
public class TQuoteMod extends Module
{
   private ArrayList<String>user;
   private ArrayList<Integer>chances;
   private QuoteMod qm;
   private int MAX_PHASE;
   private String TARGET_PHRASE;

   public TQuoteMod(QuoteMod qMod)
   {
      super("");
      qm = qMod;
      moduleName = "Target Quoting Mod";

      MAX_PHASE = 5;
      TARGET_PHRASE = "TQM";

      user = new ArrayList<String>();
      chances = new ArrayList<Integer>();
   }

   public void onMessage(String channel, String sender,
      String login, String hostname, String message)
   {
      String username = OpHelp.command(message, TARGET_PHRASE);
      if(username.length() != 0)
      {
         if(target(username))
         {
            bot.sendNotice(sender,username + " has now been targeted.");
         }
         
      }
      else
      {
         int index = user.indexOf(sender);
         if(index != -1)
         {
            chances.add(index, chances.get(index) - 1);
            String newMessage = sender + " " + message;
            qm.noticeQuote(sender, newMessage);
            if(chances.get(index) <= 0)
            {
               bot.sendNotice(sender,user.get(index)+" has been lost "
                  + "from targeting.");
               chances.remove(index);
               user.remove(index);
            }
         }
      }
   }

   public void onPrivateMessage(String sender, String login, 
      String hostname, String message)
   {
      return ;
   }

   public void onJoin(String channel,String sender, 
      String login,String hostname)
   {
      return ;
   }

   public boolean target(String name)
   {
      if(user.indexOf(name) != -1)
      {
         bot.sendMessage(bot.getChannelName(), "Cannot target a player that "
               + "is already targeted. ");
         return false;
      }
      else if(name.equals(bot.getNick()))
      {
         bot.sendMessage(bot.getChannelName(), "No.");
         return false;
      }

      User[] names = bot.getUsers(bot.getChannelName());
      for(int x = 0; x < names.length; ++x)
      {
         if(names[x].getNick().equals(name))
         {
            user.add(name);
            chances.add(MAX_PHASE);
            return true;
         }
      }

      bot.sendMessage(bot.getChannelName(), name + " has not been found.");
      return false;
   }
}