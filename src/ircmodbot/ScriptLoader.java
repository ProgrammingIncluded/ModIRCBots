package ircmodbot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.log4j.Logger;

import bsh.EvalError;
import bsh.Interpreter;

/**
 * Class to load scripts from file.
 * Script name must equal to file name of script and closure name.
 */
public class ScriptLoader extends FileSystem 
{
	public static final Logger LOGGER = Logger.getLogger(ScriptLoader.class);
	private Interpreter interpreter;
	private ArrayList<String> scriptCommands;
	
	private ModBot bot;
	
	public ScriptLoader(ModBot bot)
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
	
	private boolean readMainScript()
	{
		try
		{
			String path = getFilePath("script/main.bsh").toString();
			interpreter.set("BOT", bot);
			interpreter.source(path);
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
	
	/**
	 * Reload scripts.
	 * If loader file D.N.E. at time of call, previously loaded mods are
	 * still cleared. Returns true if scripts are loaded correctly returns
	 * false if parsing went wrong.
	 */
	public boolean reload()
	{
		interpreter = new Interpreter();
		bot.clearModules();
		
		readMainScript();
		
		if(readLoaderFile().isEmpty())
		{
			return false;
		}
		
		for(String cmd : scriptCommands)
		{
			if(cmd == null)
				continue;
			try
			{
				String mainPath = getFilePath("script/" + cmd + ".bsh").toString();
				interpreter.set("BOT", bot);
				interpreter.source(mainPath);
				Object obj = interpreter.get(cmd);
				if(!(obj instanceof ircmodbot.Module))
					throw new Exception("Class is not a Module.");
				Module mod = (Module) obj;
				mod.setBot(bot);
				bot.addModule(mod);
			}
			catch(EvalError e)
			{
				LOGGER.error("Script format error. ", e);
			}
			catch(IOException e)
			{
				LOGGER.error("Script does not exist. " + cmd, e);
			}
			catch(Exception e)
			{
				LOGGER.error("Problem with script: ", e);
			}
		}
		return true;
	}
}
