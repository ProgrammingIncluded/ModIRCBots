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
public class GeneralSLoader extends SLoader
{
	public static final Logger LOGGER = Logger.getLogger(GeneralSLoader.class);
	private Interpreter interpreter;
	private ArrayList<String> scriptCommands;
	
	private ModBot bot;
	
	public GeneralSLoader(ModBot bot)
	{
		super(bot);
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
