import java.util.ArrayList;
import java.util.Iterator;
import org.jibble.pircbot.*;

public class SKKBot extends PircBot 
{
   private ArrayList<Module> modules;
   private String channel;

   public SKKBot() 
   {
      this("SKKBot");
   }

   public SKKBot(String name) 
   {
      this.setName(name);
      modules = new ArrayList<Module>();
   }

   public void onMessage(String channel, String sender,
      String login, String hostname, String message) 
   {
      Iterator<Module> it = modules.iterator();
      Module currentModule;
      String trigger;

      while(it.hasNext())
      {
         currentModule = it.next();
         trigger = currentModule.getTrigger();
         if(OpHelp.command(message, trigger))
         {
            // Remove the key specifier and give it to the module to process
            // commands.
            message = OpHelp.removeCommand(message, trigger);
            currentModule.onMessage(channel, sender, login, hostname, message);
         }
      }
   }

   public void onJoin(String channel,String sender, 
      String login,String hostname) 
   {
      Iterator<Module> it = modules.iterator();
      while(it.hasNext())
      {
         // Send directly to all modules if needed. Perhaps do a check
         // of some kind like onMessage?
         it.next().onJoin(channel, sender, login, hostname);
      }
   }

   public void onPrivateMessage(String sender, String login, String hostname,
      String message)
   {
      Iterator<Module> it = modules.iterator();
      Module currentModule;
      String trigger;

      while(it.hasNext())
      {
         currentModule = it.next();
         trigger = currentModule.getTrigger();
         if(trigger.equalsIgnoreCase(message.substring(0, trigger.length())))
         {
            // Remove the key specifier and give it to the module to process
            // commands.
            message = message.substring(trigger.length()+1, message.length());
            currentModule.onPrivateMessage(sender, login,
               hostname, message);
         }
      }
   }

   // Custom function for storing channel name since PircBot
   // requires channel verification for it to receive channel name.
   public void addChannelName(String channel)
   {
      this.channel = channel;
   }

   /**
    * Function to call when you want to add a bot to the module.
    * @param mod
    * @return
    */
   public boolean addModule(Module mod)
   {
      if(mod == null)
      {
         return false;
      }

      mod.setBot(this);
      modules.add(mod);

      return true;
   }

   public ArrayList<Module> getModules()
   {
      return modules;
   }

   public String getChannelName()
   {
      return channel;
   }
}