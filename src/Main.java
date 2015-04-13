import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

public class Main {
   public static void main(String[] args) throws Exception {

      // Now start our bot up.
      SKKBot bot = new SKKBot();
      // Enable debugging output.
      bot.setVerbose(true);
      // Connect to the IRC server.
      bot.connect("irc.rizon.net");
      // Join the #pircbot channel.
      bot.joinChannel("#lelandcstest");
      bot.addChannelName("#lelandcstest");

      // Add modules to bot
      bot.addModule(new ESMod());
      bot.addModule(new InfoMod());

      QuoteMod qm = new QuoteMod();
      bot.addModule(qm);
      bot.addModule(new TQuoteMod(qm));

      FileManagerMod fm = new FileManagerMod();
      bot.addModule(fm);
      fm.addModFileName("testModule");
      File file = fm.getModFile("testModule", "SuperKaitoKid");
      PrintWriter out = new PrintWriter(new FileWriter(file, true));
      out.println("It works!");
      out.close();
   }
}
