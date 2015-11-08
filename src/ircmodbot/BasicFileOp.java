package ircmodbot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * Simple class to house all common file functions
 * used in the ModBot.
 * @author Charles Chen
 *
 */
public class BasicFileOp {
	
	/**
	 * Function to read files formated with text line by line.
	 * Returns an array of each line.
	 * If file does not exist, returns empty ArrayList.
	 */
	static public ArrayList<String> parseFileLBL(File file) throws PartialLBLException
	{
		ArrayList<String> result = new ArrayList<String>();
		if(file == null || file.exists() == false)
			return result;
		
		try
		{
			BufferedReader bufferReader = null;
			bufferReader = new BufferedReader(new FileReader(file.getAbsolutePath()));
			String line = "";
			while((line = bufferReader.readLine())!=null)
			{
				result.add(line);
			}
			bufferReader.close();
		}
		catch(Exception e)
		{
			throw new PartialLBLException(e.getMessage(), result);
		}
		return result;
	}
}

/**
 * Exception to represent an exception where data might be read
 * half way. If so, exception contains what has been parsed in the data.
 *
 */
class PartialLBLException extends Exception
{
	public final ArrayList<String> result;
	
	PartialLBLException(String msg)
	{
		super(msg);
		result = null;
	}
	
	PartialLBLException(String msg, ArrayList<String> result)
	{
		super(msg);
		this.result = result;
	}
	
	public ArrayList<String> getResult()
	{
		return result;
	}
}
