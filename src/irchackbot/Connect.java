package irchackbot;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

/**
 * Class to deal with receiving input from user regarding connection to server.
 * Should read off file from connect.txt, simple first line is server name, second line is channel,
 * third is the bot name.
 * @author Charles Chen
 *
 */
public class Connect extends FileSystem
{
	/// Variable to contain information about connect.
	private ArrayList<String> result;
	private boolean status;
	private int minLineParse;
	
	/// Logger for all your logging needs.
	public static final Logger LOGGER = Logger.getLogger(FileSystem.class);
	public static final String DEF_NAME_RETURN = "SKKERRBOT";
	
	Connect()
	{
		status = false;
		minLineParse = 3;
		readFile();
		
		if(result.size() >= minLineParse)
		{
			status = true;
		}
	}
	
	public String getChannelName()
	{
		if(status == false)
			return "ERRCHANNEL";
		return result.get(1);
	}
	
	public String getServerName()
	{
		if(status == false)
			return "ERRSERVER";
		return result.get(0);
	}
	
	public String getBotName()
	{
		if(status == false)
			return "ERRNAME";
		return result.get(2);
	}
	
	public boolean getStatus()
	{
		return status;
	}
	
	/**
	 * Function to read the file named connect.txt in Systems folder.
	 */
	private void readFile()
	{
		result = null;
		File path = null;
		try
		{
			path = getFilePath("connect.txt");
		}
		catch(IOException e)
		{
			LOGGER.error("Error in, connection.txt does not exist.", e);
			result = new ArrayList<String>();
		}
		
		try
		{
			result = BasicFileOp.parseFileLBL(path);
		}
		catch(PartialLBLException e)
		{
			LOGGER.error("Error in parsing connection list.", e);
			result = e.getResult();
		}
	}
}
