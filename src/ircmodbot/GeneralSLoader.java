package ircmodbot;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

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

	public String mainFile = "main.bsh";
	
	protected ModBot bot;
	
	public GeneralSLoader(ModBot bot)
	{
		this(null, bot);
	}
	
	public GeneralSLoader(String root, ModBot bot)
	{
		super(root);
		this.bot = bot;
		reload();
	}
	
	public boolean setBot(ModBot bot)
	{
		if(bot == null)
			return false;
		this.bot = bot;
		return true;
	}
	
	/**
	 * Sets the main file path for loader.
	 * File is run after every reloading.
	 */
	public boolean setMainFilePath(String path)
	{
		if(path == null)
			return false;

		this.mainFile = path;
		return true;
	}
	
	private boolean readMainScript()
	{
		try
		{
			// TODO: Change to constant?
			String path = getFilePath(mainFile).toString();
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
	
	private void loadFile(String cmd)
	{
		if(cmd == null)
			return;
		try
		{
			String mainPath = getFilePath(cmd).toString();
			interpreter.set("BOT", bot);
			interpreter.source(mainPath);
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
	
	/**
	 * Reload scripts.
	 * If loader file D.N.E. at time of call, previously loaded mods are
	 * still cleared. Returns true if scripts are loaded correctly returns
	 * false if parsing went wrong.
	 */
	public boolean reload()
	{
		// Clear all modules.
		bot.clearModules();
		
		if(this.scriptCommands.isEmpty() && this.scriptFolder.isEmpty())
		{
			return false;
		}
		
		// Load the files!
		for(String cmd : scriptCommands)
		{
			loadFile(cmd + ".bsh");
		}
		
		// Load the folders!
		for(String folder : scriptFolder)
		{
			File fold = new File(getRoot().getPath(), folder);
			if(fold.exists() && fold.isDirectory())
			{
				FilenameFilter filter = new FilenameFilter()
				{
					public boolean accept(File dir, String filename)
					{
						return filename.endsWith(".bsh");
					}
				};
				File[] files = fold.listFiles(filter);

				for(File file : files)
					loadFile(fold.getName() + "/" + file.getName());
			}
			else
			{
				LOGGER.error("The script folder does not exist under " 
						+ this.getRoot().getPath() + " : " + folder);
			}
		}
		readMainScript();
		// Remove all previous interpreted values.
		interpreter = new Interpreter();
		return true;
	}
}
