package modules;

import java.io.IOException;
import java.util.NoSuchElementException;

import org.apache.commons.lang3.RandomStringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import ircmodbot.Module;
import ircmodbot.OpHelp;

public class ZoraelMod extends Module
{
   Document doc;
   final String YES_MSG = "is the innovator of this work.";
   final String NO_MSG = "is not the innovator of this work.";
   final int DEF_TRIES = 25;
   final int DEF_DEEP_TRIES = 100;

   public ZoraelMod()
   {
      super("ZOR");
      moduleName = "Zo Zo Mod";
   }

   public void onMessage(String channel, String sender,
      String login, String hostname, String message)
   {
      int linkSize = 5;
      int tries = DEF_TRIES;
      String link = "";
      String command = OpHelp.command(message, "Youtube");
      if(command.length() != 0)
      {
         try
         {
            link = generateRandILink("http://youtube.com/watch?v=",
               11, tries);
         }
         catch(NoSuchElementException e)
         {
            bot.sendMessage(channel, "Unable to determine the creator.");
            return;
         }
      }
      else
      {
         if(OpHelp.command(message, "deepsearch").length() != 0)
         {
            linkSize = 7;
            bot.sendMessage(channel, 
               "Going into the deep reaches to find his potential work."
               + " This may take some time.");
            tries = DEF_DEEP_TRIES;
         }
         

         try
         {
            link = generateRandILink("http://i.imgur.com/",linkSize, tries);
         }
         catch(NoSuchElementException e)
         {
            bot.sendMessage(channel, "Unable to determine the creator.");
            return;
         }
      }

      // Create confirmation message.
      String confirmMessage = sender + " ";
      if(Math.random() < 0.5)
         confirmMessage += YES_MSG;
      else
         confirmMessage += NO_MSG;
      // Send all info!
      bot.sendMessage(channel, confirmMessage);
      bot.sendMessage(channel, "PSST... Here is the link... " + link);
   }

   public void onJoin(String channel,String sender, 
      String login,String hostname)
   {
      return ;
   }

   public void onPrivateMessage(String sender, String login, 
      String hostname, String message)
   {
      onMessage(sender,sender, login, hostname, message);
   }
   
   /**
    * Generates a random imgur like link with the link size which represents
    * the imgur like link rand strings in the back of url.
    */
   private String generateRandILink(String startLink, int linkSize, int tries) 
      throws NoSuchElementException
   {
      if(linkSize < 0 || tries <= 0 || startLink.length() <= 0)
         return startLink+"random";
      
      String link = "";
      Elements checker;
      int counter = 0;
      while(true)
      {
         link =  RandomStringUtils.random(linkSize, true, true);
         try
         {
            doc = Jsoup.connect(startLink + link).get();
         }
         catch(IOException e)
         {
            ++counter;
            if(counter >= tries)
               throw new NoSuchElementException();
            continue;
         }
         checker = doc.select("#hallway");
         // Found the link! use it!
         if(checker.size() <= 0)
            break;
      }
      return startLink + link;
   }
}
