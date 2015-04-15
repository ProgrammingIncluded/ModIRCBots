import ircmodbot.*;
import modules.ESMod;
import modules.InfoMod;
import modules.QuoteMod;
import modules.TQuoteMod;
import modules.ZoraelMod;

public class Main {
   public static void main(String[] args) throws Exception {

      // Now start our bot up.
      ModBot bot = new ModBot("SKKBot");
      // Enable debugging output.
      bot.setVerbose(true);
      // Connect to the IRC server.
      bot.connect("irc.rizon.net");
      // Join the #pircbot channel.
      bot.joinChannel("#lelandcs");
      bot.addChannelName("#lelandcs");

      // Add modules to bot
      bot.addModule(new ESMod());
      bot.addModule(new InfoMod());

      QuoteMod qm = new QuoteMod();
      bot.addModule(qm);
      bot.addModule(new TQuoteMod(qm));
      
      bot.addModule(new ZoraelMod());

      /*
      FileManagerMod fm = new FileManagerMod();
      bot.addModule(fm);
      fm.addModFileName("testModule");
      File file = fm.getModFile("testModule", "SuperKaitoKid");
      PrintWriter out = new PrintWriter(new FileWriter(file, true));
      out.println("It works!");
      out.close();
      */
   }
}
