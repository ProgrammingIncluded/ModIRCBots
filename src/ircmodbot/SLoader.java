package ircmodbot;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.log4j.Logger;

import bsh.EvalError;
import bsh.Interpreter;

/**
 * Abstract class to framework general script loading from file.
 * Generalized so various implementations of read the scripts can be performed.
 * @author Charles
 *
 */
public abstract class SLoader extends FileSystem {
	public static final Logger LOGGER = Logger.getLogger(GeneralSLoader.class);
	protected Interpreter interpreter;
	protected ArrayList<String> scriptCommands;
	protected ArrayList<String> scriptFolder;

	protected String loaderFile = "loader.bsh";
	
	public SLoader()
	{
		this(null);
	}
	
	public SLoader(String root)
	{
		if(root != null)
			this.setRoot(root);

		interpreter = new Interpreter();
		scriptCommands = new ArrayList<String>();
		scriptFolder = new ArrayList<String>();
		readLoaderFile();
	}
	
	public boolean setLoaderFile(String loaderFile)
	{
		if(loaderFile == null)
			return false;
		
		this.loaderFile = loaderFile;
		return true;
	}

	public String getLoaderFile()
	{
		return loaderFile;
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
	
	public void addScriptFolder(String folder)
	{
		scriptFolder.add(folder);
	}
	
	public void addScriptFolder(ArrayList<String> names)
	{
		scriptFolder.addAll(names);
	}
	
	public void addScriptFolder(String[] names)
	{
		scriptFolder.addAll(Arrays.asList(names));
	}
	
	/**
	 * Reads the loader file.
	 */
	protected void readLoaderFile()
	{
		scriptCommands.clear();
		scriptFolder.clear();
		try
		{
			String path = getFilePath(loaderFile).toString();
			interpreter.set("LOADER", this);
			interpreter.source(path); 
		}
		catch(EvalError e)
		{
			LOGGER.error("Script format error. ", e);
		}
		catch(Exception e)
		{
			LOGGER.error("Unkown loader error.", e);
			System.out.println("No loader file. Please add create loader in scripts/loader.bsh");
		}	
	}
	
	public abstract boolean reload();
}
