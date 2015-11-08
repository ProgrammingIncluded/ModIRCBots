import java.io.IOException;
import java.util.Scanner;

import ircmodbot.*;
import bsh.EvalError;
import bsh.Interpreter;

public class Main {
	
	
	private ModBot modBot;
	public final Interpreter mainInterpreter;
	
	Main()
	{
		mainInterpreter = new Interpreter();
	}
	
	// Helper function for main. Must be present.
	private boolean setMainScript()
	{
		FileSystem defaultFS = new FileSystem();
		try
		{
			String path = defaultFS.getFilePath("script/main.bsh").toString();
			mainInterpreter.set("bot", modBot);
			mainInterpreter.source(path);
			return true;
		}
		catch(EvalError e)
		{
			ModBot.LOGGER.error("Script format error. ", e);
		}
		catch(IOException e)
		{
			ModBot.LOGGER.error("Script does not exist. ", e);
		}
		catch(Exception e)
		{
			ModBot.LOGGER.error("Problem with script: ", e);
		}
		return false;
	}
	
	public boolean run()
	{
		// Set debug bot options.
		if(!ModBot.configure("system/log4j.properties"))
		{
			System.out.println("No custom properties at system/log4j.properties. "
				+ "Using basic properties.");
		}
		modBot = new ModBot();
		
		if(!setMainScript())
			return false;
		
		boolean exit = false;
		String input = "";
		Scanner reader = new Scanner(System.in);
		while(!exit)
		{
			System.out.print("> ");
			input = reader.nextLine();
			if(input.equalsIgnoreCase("exit"))
				exit = true;
			else if(input.equalsIgnoreCase("reload"))
				modBot.loader.reload();
		}
		reader.close();
		return true;
	}

	public static void main(String[] args)
	{
		Main main = new Main();
		main.run();
		System.exit(0);
	}
}
