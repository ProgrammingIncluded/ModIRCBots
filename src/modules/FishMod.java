package modules;

import bank.TransactionException;
import ircmodbot.Module;

public class FishMod extends Module
{
	private BankMod bMod;
	private String fisherName = "Botato";
	private String currencyType = "FUSH";
	// Currency = 0.5 * ibs
	private double fishRate = 0.5;
	
	public FishMod(BankMod bMod)
	{
		super("FishMod","");
		this.bMod = bMod;
	}
	

	@Override
	public void onMessage(String channel, String sender, String login,
			String hostname, String message)
	{
		if(!sender.equalsIgnoreCase(fisherName))
			return;
		parseCommand(message);
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
		if(!sender.equalsIgnoreCase(fisherName))
			return;
		parseCommand(message);
	}
	
	private void makeFishTransaction(String user, double fishIBS)
	{
		try
		{
			bMod.getBank().transact(bMod.getBank().getBankName(), user, fishIBS*fishRate, currencyType);
		} 
		catch (TransactionException e) 
		{
			return;
		}
	}
	
	private void parseCommand(String msg)
	{
		int indexIb = msg.indexOf("lbs.");
		int indexW = msg.indexOf("weighing");
		if(indexIb == -1 || indexW == -1)
			return;
		
		String value = msg.substring(indexW + 7 + 2,  indexIb - 1);
		double dValue = 1.0;
		try
		{
			dValue = Double.valueOf(value);
		}
		catch(NumberFormatException e)
		{
			return;
		}
		
		String user = msg.substring(0, msg.indexOf(' '));
		makeFishTransaction(user, dValue);
	}
}
