package modules;
import ircmodbot.Module;
import ircmodbot.OpHelp;

import java.util.Calendar;
import java.util.Random;

import org.jibble.pircbot.Colors;

public class QuoteMod extends Module
{
   public QuoteMod()
   {
      super("QM");
      moduleName = "Quoting Mod";
   }

   public void onMessage(String channel, String sender,
      String login, String hostname, String message)
   {
      displayQuote(channel,message);
   }

   public void onPrivateMessage(String sender, String login, 
      String hostname, String message)
   {
      displayQuote(bot.getChannelName(),message);
   }

   public void onJoin(String channel,String sender, 
      String login,String hostname)
   {
      return ;
   }

   public void noticeQuote(String sender, String message)
   {
      int year = Calendar.getInstance().get(Calendar.YEAR);
      String name = message.substring(0, message.indexOf(' '));
      message = OpHelp.command(message, name);
      bot.sendNotice(sender, Colors.BOLD + randColor()
         + "\"" + message +  "\"" + Colors.NORMAL + " - "
         + name + ", " + year);
   }
   
   public void displayQuote(String channel,String message)
   {
      int year = Calendar.getInstance().get(Calendar.YEAR);
      String name = message.substring(0, message.indexOf(' '));
      message = OpHelp.command(message, name);
      bot.sendMessage(channel, Colors.BOLD + randColor()
         + "\"" + message +  "\"" + Colors.NORMAL + " - "
         + name + ", " + year);
   }

   public String randColor()
   {
      Random random = new Random();
      int rand = random.nextInt(10 + 1);
      String color = "";
      switch(rand)
      {
         case 0:
            color = Colors.BLACK;
            break;
         case 1:
            color = Colors.BLUE;
            break;
         case 2:
            color = Colors.BROWN;
            break;
         case 3:
            color = Colors.CYAN;
            break;
         case 4:
            color = Colors.DARK_GRAY;
            break;
         case 5:
            color = Colors.GREEN;
            break;
         case 6:
            color = Colors.BLACK;
            break;
         case 7: 
            color = Colors.OLIVE;
            break;
         case 8:
            color = Colors.DARK_BLUE;
         case 9:
            color = Colors.RED;
         case 10:
            color = Colors.TEAL;
         default:
            color = Colors.DARK_BLUE;
            break;
      }
      return color;
   }
}