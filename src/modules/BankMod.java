package modules;

import org.apache.log4j.Logger;

import bank.Bank;
import bank.TransactionException;
import ircmodbot.FileSystem;
import ircmodbot.ModBot;
import ircmodbot.Module;
import ircmodbot.OpHelp;

public class BankMod extends Module
{
	private static final Logger LOGGER = Logger.getLogger(BankMod.class);
	private Bank bank;

	public BankMod(ModBot bot)
	{
		super(bot, "BankMod", "BKM");
		bank = new Bank(bot.getUserBase());
		// Test Code
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
		bot.sendMessage(sender, parseCommand(message));
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
		bot.sendMessage(sender, parseCommand(message));
	}

	private String parseCommand(String msg)
	{
		String result = "";
		String command = OpHelp.subString(msg, 0, msg.indexOf(' '));
	    msg = OpHelp.subString(msg, msg.indexOf(' ') + 1, msg.length());
		
	    if(command == null)
	    	return "Invalid command.";
	    	
		if(command.equalsIgnoreCase("cur"))
	    	result = checkCurrency(msg);
	    else
	    	result = "No valid command given.";
	    
		return result;
	}
	
	/**
	 * Function in charge of getting currency value.
	 * Should be formatted as: user (currency)
	 * if currency is not given, uses default currency.
	 */
	private String checkCurrency(String msg)
	{
		String result = "";
		String user = OpHelp.subString(msg, 0, msg.indexOf(' '));
		// No currency given, so must be user.
		if(user == null)
			user = msg;
		
		// Get rest of message if applicable.
	    msg = OpHelp.subString(msg, msg.indexOf(' ') + 1, msg.length());
	    
	    if(user == null)
	    	return "Invalid user.";
	    
	    if(bot.getUserBase().getUser(user) == null)
	    	return "User does not exist in database.";
	    	
	    String curr = OpHelp.subString(msg, msg.indexOf(' ') + 1, msg.length());
	    
	    if(curr == null)
	    	return bank.getValue(user).toString();
	    
	    return bank.getValue(user, curr).toString();
	}

}
