import java.util.Scanner;

import ircmodbot.*;
import bsh.Interpreter;

public class Main {
	
	
	private ModBot modBot;
	public final Interpreter mainInterpreter;
	public GeneralSLoader loader;
	
	Main()
	{
		mainInterpreter = new Interpreter();
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
		loader = new GeneralSLoader(modBot);
		
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
				loader.reload();
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
