
public class ESMod extends Module
{
   private String regenPass;
   private String randPass;

   ESMod()
   {
      super("ESM");
      moduleName = "Exit System Mod";
      randPass = Math.random()*100 + "";
      regenPass = "givemekey";
   }

   public void onMessage(String channel, String sender,
      String login, String hostname, String message)
   {
      if(OpHelp.command(message,"exit"))
      {
         message = OpHelp.subString(message ,5, message.length());
         if(message.equalsIgnoreCase(randPass))
         {
            bot.sendMessage(channel, "Shutting down.");
            System.exit(0);
         }
      }
   }

   public void onPrivateMessage(String sender, String login, 
      String hostname, String message)
   {
      if(OpHelp.command(message, "pregen"))
      {
         message = OpHelp.removeCommand(message, "pregen");
         System.out.println(message);
         if(OpHelp.command(message, regenPass))
         {
            regenPassword();
            bot.sendMessage(sender, randPass);
         }
      }
   }

   public void onJoin(String channel,String sender, 
      String login,String hostname)
   {
      return ;
   }

   public void regenPassword()
   {
      randPass = Math.random()*100+"";
   }
}
