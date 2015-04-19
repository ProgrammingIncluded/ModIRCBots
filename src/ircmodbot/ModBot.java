package ircmodbot;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;
import org.jibble.pircbot.*;

/**
 * Generic bot class to create a mod bot.
 * Add modules to the bot for different abilities. Right now, can be
 * optimized by adding a list for quicker command find. Also, commands
 * have to be unique. Commands can also not be chained.
 * 
 * Configure must be first called to use class.
 * @author Charles
 *
 */
public class ModBot extends PircBot 
{
   private ArrayList<Module> modules;
   private String channel;
   private FilePermissions filePerm;

   public ModBot() 
   {
      this("ModBot");
   }

   public ModBot(String name)
   {
      this.setName(name);
      modules = new ArrayList<Module>();
      filePerm = new FilePermissions("whitelist.txt", "blacklist.txt");
   }

   public void onMessage(String channel, String sender,
      String login, String hostname, String message) 
   {
      Iterator<Module> it = modules.iterator();
      Module currentModule;
      String trigger;

      // Check global permissions.
      if(!filePerm.getGlobalPermission(sender))
         return;
      
      // Perhaps use list instead in order to search faster?
      while(it.hasNext())
      {
         currentModule = it.next();
         trigger = currentModule.getTrigger();
         String modMessage = OpHelp.command(message, trigger);
         if(modMessage.length() != 0)
         {
            // No duplicate messages!
            message = modMessage;
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

      // Perhaps use list instead in order to search faster?
      while(it.hasNext())
      {
         currentModule = it.next();
         trigger = currentModule.getTrigger();
         String modMessage = OpHelp.command(message, trigger);
         if(modMessage.length() != 0)
         {
            // No duplicate messages!
            message = modMessage;
            currentModule.onPrivateMessage(sender, login, hostname, message);
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

   /**
    * Function to first call to set up logging.
    */
   public static boolean configure(String logPropFile)
   {
      File file = new File(logPropFile);
      if(!file.exists() == false)
      {
         PropertyConfigurator.configure(file.getAbsolutePath());
         return true;
      }
      BasicConfigurator.configure();
      return false;
   }
}