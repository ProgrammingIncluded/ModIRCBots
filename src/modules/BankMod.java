package modules;

import org.apache.log4j.Logger;

import bank.Bank;
import bank.TransactionException;
import ircmodbot.FileSystem;
import ircmodbot.ModBot;
import ircmodbot.Module;

public class BankMod extends Module
{
   private static final Logger LOGGER = Logger.getLogger(BankMod.class);
   private Bank bank;

   public BankMod(ModBot bot)
   {
      super(bot, "BankMod", "BKM");
      bank = new Bank(bot.getUserBase());
      
      try
      {
         bank.transact("SKK", "Botato", 10, "SKKC");
      }
      catch(TransactionException e)
      {
         LOGGER.debug(e);
      }
   }

   @Override
   public void onMessage(String channel, String sender, String login,
         String hostname, String message)
   {


   }

   @Override
   public void onJoin(String channel, String sender, String login,
         String hostname)
   {


   }

   @Override
   public void onPrivateMessage(String sender, String login, String hostname,
         String message)
   {


   }

}
