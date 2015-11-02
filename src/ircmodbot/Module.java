package ircmodbot;
import org.jibble.pircbot.*;

public abstract class Module 
{
   protected String trigger;
   protected String moduleName;

   protected ModBot bot;

   protected Module()
   {
      this(null, null, null);
   }

   protected Module(String triggerWord)
   {
      this(null, null, triggerWord);
   }
   
   protected Module(String moduleName, String triggerWord)
   {
	   this(null, moduleName, triggerWord);
   }
   
   protected Module(ModBot bot, String moduleName, String triggerWord)
   {
      if(!setBot(bot))
         bot = null;

      if(!setTrigger(triggerWord))
         triggerWord = "ERR" + String.valueOf(Math.random()*10);
      
      if(!setName(moduleName))
    	  moduleName = "Unknownmod" + String.valueOf(Math.random());
   }

   protected Module(ModBot bot, String triggerWord)
   {
      this(bot, null, triggerWord);
   }

   abstract public void onMessage(String channel, String sender,
      String login, String hostname, String message);

   abstract public void onJoin(String channel,String sender, 
         String login,String hostname);
   abstract public void onPrivateMessage(String sender, String login, 
      String hostname, String message);

   public boolean setBot(ModBot bot)
   {
      if(bot == null)
      {
         return false;
      }

      this.bot = bot;
      return true;
   }

   public boolean setTrigger(String triggerWord)
   {
      if(triggerWord == null)
      {
         return false;
      }

      trigger = triggerWord;
      return true;
   }
   
   public boolean setName(String name)
   {
	   if(name == null)
		   return false;
	   
	   moduleName = name;
	   return true;
   }

   public String getTrigger()
   {
      return trigger;
   }

   public String getName()
   {
      return moduleName;
   }

   public void wait(int sec) 
   {
      try 
      {
         Thread.sleep(1000*sec);
      } catch(InterruptedException ex) 
      {
         return ;
      }
   }
}
