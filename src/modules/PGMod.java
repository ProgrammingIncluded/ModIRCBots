package modules;
import java.util.ArrayList;

import ircmodbot.Module;
import ircmodbot.OpHelp;


/**
 * A simple dummy test module.
 */
public class PGMod extends Module
{
   PGMod()
   {
      super("PGM");
      moduleName = "Play Ground Mod";
   }

   public void onMessage(String channel, String sender,
      String login, String hostname, String message, ArrayList<String> cmd)
   {
      bot.sendMessage(channel, "Echo: " + OpHelp.command(this.getTrigger(), message));
   }

   public void onJoin(String channel,String sender, 
      String login,String hostname)
   {
      return ;
   }

   public void onPrivateMessage(String sender, String login, 
      String hostname, String message, ArrayList<String> cmd)
   {
      
   }
}
