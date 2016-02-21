import java.util.Scanner;

import ircmodbot.*;

public class Main {

	private ModBot modBot;
	public GeneralSLoader loader;
	
	// Variable to decide if main loop should exit.
	private boolean exit = false;
	
	Main()
	{
		// Nothing here...
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
		loader = new GeneralSLoader("system/script", modBot);
		
		String input = "";
		Scanner reader = new Scanner(System.in);
		while(!exit)
		{
			System.out.print("> ");
			input = reader.nextLine();
			parseAdminInput(input);
		}
		reader.close();
		return true;
	}

	private void parseAdminInput(String input)
	{
		if(input.equalsIgnoreCase("exit"))
			exit = true;
		else if(input.equalsIgnoreCase("reload"))
			loader.reload();
		//else if(input.equalsIgnoreCase("reloadAdmin"))
			
	}
	
	public static void main(String[] args)
	{
		Main main = new Main();
		main.run();
		System.exit(0);
	}
}
