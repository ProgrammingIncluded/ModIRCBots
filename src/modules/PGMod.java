package modules;
import ircmodbot.Module;


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
      String login, String hostname, String message)
   {
      bot.sendMessage(channel, "Echo: " + message);
   }

   public void onJoin(String channel,String sender, 
      String login,String hostname)
   {
      return ;
   }

   public void onPrivateMessage(String sender, String login, 
      String hostname, String message)
   {
      
   }
}
