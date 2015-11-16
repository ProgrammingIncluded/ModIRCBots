package ircmodbot;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.log4j.Logger;

import bsh.EvalError;
import bsh.Interpreter;

public abstract class SLoader extends FileSystem {
	public static final Logger LOGGER = Logger.getLogger(GeneralSLoader.class);
	private Interpreter interpreter;
	private ArrayList<String> scriptCommands;
	
	private ModBot bot;
	
	public SLoader(ModBot bot)
	{
		scriptCommands = new ArrayList<String>();
		this.bot = bot;
		reload();
	}
	
	public ArrayList<String> getScriptCommands()
	{
		return scriptCommands;
	}
	
	public void addScriptName(String name)
	{
		scriptCommands.add(name);
	}
	
	public void addScriptName(ArrayList<String> names)
	{
		scriptCommands.addAll(names);
	}
	
	public void addScriptName(String[] names)
	{
		scriptCommands.addAll(Arrays.asList(names));
	}
	
	public boolean setBot(ModBot bot)
	{
		if(bot == null)
			return false;
		this.bot = bot;
		return true;
	}
	
	/**
	 * Reads the loader file.
	 */
	public ArrayList<String> readLoaderFile()
	{
		scriptCommands.clear();
		try
		{
			String path = getFilePath("script/loader.bsh").toString();
			interpreter.set("LOADER", this);
			interpreter.source(path); 
		}
		catch(EvalError e)
		{
			LOGGER.error("Script format error. ", e);
		}
		catch(Exception e)
		{
			System.out.println("No loader file. Please add create loader in scripts/loader.bsh");
		}	
		
		return scriptCommands;
	}
	
	public abstract boolean reload();
}
