package modules;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import bank.Bank;
import bank.TransactionException;
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
	}

	public Bank getBank()
	{
		return bank;
	}
	
	@Override
	public void onMessage(String channel, String sender, String login,
			String hostname, String message, ArrayList<String> cmd)
	{
		bot.sendMessage(sender, parseCommand(cmd));
	}

	@Override
	public void onJoin(String channel, String sender, String login,
			String hostname)
	{
		
	}

	@Override
	public void onPrivateMessage(String sender, String login, String hostname,
			String message, ArrayList<String> cmd)
	{
		bot.sendMessage(sender, parseCommand(cmd));
	}

	private String parseCommand(ArrayList<String> cmd)
	{
		String result = "";
		
	    if(cmd.size() < 2)
	    	return "Invalid command.";
	    	
		if(cmd.get(1).equalsIgnoreCase("cur"))
	    	result = checkCurrency(cmd);
	    else
	    	result = "No valid command given.";
	    
		return result;
	}
	
	/**
	 * Function in charge of getting currency value.
	 * Should be formatted as: user (currency)
	 * if currency is not given, uses default currency.
	 */
	private String checkCurrency(ArrayList<String> cmd)
	{
		String curr = null;
		String user;
		
		if(cmd.size() >= 3)
			 user = cmd.get(2);
		else
			return "Invalid user";
		
		if(cmd.size() >= 4)
			curr = cmd.get(3);
			
		
	    if(bot.getUserBase().getUser(user) == null)
	    	return "User does not exist in database.";
	    
	    if(curr == null)
	    	return bank.getValue(user).toString();
	    
	    return bank.getValue(user, curr).toString();
	}

}
