package irchackbot;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Simple extension of FileSystem that supports Json reading for more complex
 * datastructures. Reads directly from files. Use FileMemory for optional
 * load to memory.
 * Functions right now require passing of fileArray which are called
 * in public functions.
 * TODO: REFACTOR TO MAKE PROPER BYTE DEPENDENT FILE SYSTEM 
 * ALLOW RELEASE AND ACQUIRE OF ASSET IN SYSTEM
 * TODO: Check if dataKeys is proper length.
 * 
 */
public abstract class FileData<D> extends FileSystem
{
	private static final Logger LOGGER = Logger.getLogger(FileData.class);

	/*Default Values for Help Functions*/
	/// Default key used.
	protected String idKey = "name";
	/// Default container name used.
	protected String containerName = "users";
	protected String fileName = "userdata.json";
	protected String dataKeys[] = {"id"};

	/// Variable to keep track of amount of entries.
	protected int entryCount = 0;

	// Functions to work with converting JSONObjects into Java objects.
	public abstract D rawDataToData(String idVal, String[] key, String[] data);
	public abstract Pair<ArrayList<String>, ArrayList<String>> dataToRawData(D data);

	/**
	 * Overridden function for array version of addData().
	 */
	public boolean addData(String key, D data)
	{
		// Please don't hate me...
		@SuppressWarnings("unchecked")
		D internalData[] = (D[]) new Object[1];
		internalData[0] = data;
		return addData(key, internalData);
	}
	
	/**
	 * Simple function to call in order to add new data in to system.
	 */
	public boolean addData(String key, D data[])
	{
		if(data == null)
			return false;

		JSONArray fileArray = readJsonFile(fileName);
		int index = getIndexOfData(fileArray, key);
		if(index != -1)
			return false; // Data already exists.
		addDataToFile(data, fileArray);
		return true;
	}

	/**
	 * Overridden function for array version of forceAddData().
	 */
	public boolean forceAddData(String key, D data)
	{
		@SuppressWarnings("unchecked")
		D internalData[] = (D[]) new Object[1];
		internalData[0] = data; 
		return forceAddData(key, internalData);
	}

	/**
	 * Function to overwrite data in file if exists.
	 * Otherwise, adds the file.
	 * Returns false if key or data is null.
	 */
	public boolean forceAddData(String key, D data[])
	{
		if(data == null || key == null)
			return false;

		JSONArray fileArray = readJsonFile(fileName);
		if(updateDataInFile(key, data, fileArray))
			return true; // Data already exists. Updated it.

		// Add it in to the file.
		addDataToFile(data, fileArray);
		return true;
	}

	/**
	 * Overriden function for array version of updateData().
	 */
	public boolean updateData(String key, D data)
	{
		@SuppressWarnings("unchecked")
		D internalData[] = (D[]) new Object[1];
		internalData[0] = data;
		return updateData(key, data);
	}

	/**
	 * Function that updates the data in the file.
	 * Returns false if data does not exist in file.
	 * Will not doing anything if data D.N.E.
	 */
	public boolean updateData(String key, D data[])
	{
		return updateDataInFile(key, data, readJsonFile(fileName));
	}

	/**
	 * Simple general function to call in order to get data.
	 */
	public D getData(String idKey)
	{
		return getDataInFile(idKey, readJsonFile(fileName));
	}


	/* JSon Parsing Related Functions */

	/**
	 * Function to update the file data in file.
	 * If data does not exist, returns false.
	 * Will check if the data is in the file.
	 * Returns false if parameters are invalid. 
	 */
	@SuppressWarnings("unchecked")
	private boolean updateDataInFile(String key, D data[], JSONArray fileArray)
	{
		// Internally checks for valid input.
		int index = getIndexOfData(fileArray, key);
		if(index == -1)
			return false;
		for(D indvData : data)
		{
			Pair<ArrayList<String>, ArrayList<String>> rawData = dataToRawData(indvData);
			ArrayList<String> names = rawData.getLeft();
			ArrayList<String> values = rawData.getRight();

			if(names.size() != values.size())
			{
				LOGGER.debug("Invalid data conversion. Keys != Values.");
				return false;
			}

			for(int x = 0; x < names.size(); ++x)
			{
				JSONObject dataObject = (JSONObject)fileArray.get(index);
				dataObject.put(names.get(x).toString(),values.get(x).toString());
			}
		}
		JSONObject container = new JSONObject();
		container.put(getContainerName(), fileArray);
		writeJsonFile(fileName, container);
		return true;
	}


	/**
	 * Parses all related data from file into an array of strings. String is
	 * null if data key does not exist. If any fields null, will return null.
	 */
	private D getDataInFile(String idKey, JSONArray fileArray)
	{
		if(dataKeys == null || fileName == null || 
				idKey == null ||  fileName.length() == 0)
			return null;

		String[] values = new String[dataKeys.length];
		// Checks full null in function.
		int index = getIndexOfData(fileArray, idKey);

		if(index == -1)
			return null;

		for(int x = 0; x < dataKeys.length; ++x)
			values[x] = ((JSONObject)fileArray.get(index)).get(dataKeys[x]).toString();

		return rawDataToData(idKey, dataKeys, values);
	}


	/**
	 * Function to add data to json file. Data given is in array format
	 * as it is more beneficial.  RE-Write to incorporate manual release of read.
	 * Adds the data to end of json array.
	 * Must give a fileArray, can be null. Will write to blank file.
	 * Does not involve search of file.
	 */
	@SuppressWarnings("unchecked")
	private void addDataToFile(D data[], JSONArray fileArray)
	{
		if(data == null)
			return ;
		JSONArray userJSON = new JSONArray();
		// Creates formatted data to add to file.
		for(D indData : data)
		{

			Pair<ArrayList<String>, ArrayList<String>> rawData = dataToRawData(indData);
			ArrayList<String> names = rawData.getLeft();
			ArrayList<String> values = rawData.getRight();

			if(names.size() != values.size())
			{
				LOGGER.debug("Invalid data conversion. Keys != Values.");
				return ;
			}

			++entryCount;
			JSONObject userData = new JSONObject();

			for(int x = 0; x < names.size(); ++x)
			{
				userData.put(names.get(x), values.get(x));
			}
			userJSON.add(userData);
		}
		// Add old data back.
		if(fileArray == null)
			fileArray = userJSON; // File empty, just set value.
		else 
			appendJsonArray(fileArray, userJSON); // Append

		JSONObject container = new JSONObject();
		container.put(getContainerName(), fileArray);

		// Write the file! Currently no catch for failure.
		writeJsonFile(fileName, container);
	}

	/**
	 * Function to return index of the data located in file.
	 * Returns -1 if invalid parameters or value D.N.E.
	 * Private helper function.
	 */
	private int getIndexOfData(JSONArray dataArray, String idKeyValue)
	{
		if(dataArray == null || idKeyValue == null)
			return -1;

		entryCount = dataArray.size();
		int result = -1;
		try
		{
			Iterator<?> it = dataArray.iterator();
			for(int i = 0; i < entryCount; ++i)
			{
				JSONObject curUser = (JSONObject) dataArray.get(i);
				if(curUser.get(idKey).toString().equalsIgnoreCase(idKeyValue))
				{
					result = i;
					break;
				}
			}
		}
		catch(NullPointerException e)
		{
			LOGGER.error("Unable to read FileData in FileSystem.", e);
			LOGGER.debug("Unable to read FileData in FileSystem.", e);
		}
		return result;
	}

	public boolean writeJsonFile(String fileName, JSONObject data)
	{
		try
		{
			File file = this.getFilePath(fileName);
			if(!file.exists())
				file.createNewFile();
			FileWriter fileWriter = new FileWriter(file);
			fileWriter.write(data.toJSONString());
			fileWriter.flush();
			fileWriter.close();
		}
		catch (IOException e)
		{
			LOGGER.error("Unable to open" + fileName + " in FileData.", e);
			return false;
			//System.exit(1);
		}
		return true;
	}

	/**
	 * Helper function to parse Json specific database file.
	 * Called by getUserInDataBase()
	 */
	public JSONArray readJsonFile(String filename)
	{
		JSONParser parser = new JSONParser();
		JSONObject obj = null;
		try
		{
			obj = (JSONObject) parser.parse(
					new FileReader(this.getFilePath(filename)));
		}
		catch(IOException e)
		{
			LOGGER.error("Unable to read "+filename+" in FileData", e);
			LOGGER.debug("Unable to read "+filename+" in FileData", e);
		} 
		catch (ParseException e)
		{
			LOGGER.error("Unable to read "+filename+" in FileData", e);
			LOGGER.debug("Unable to read "+filename+" in FileData", e);
		}
		JSONArray users = null;
		if(obj != null)
			users = (JSONArray) obj.get("users");
		return users;
	}

	/**
	 * Appends one JSONArray to another. Simple helper function.
	 */
	@SuppressWarnings("unchecked")
	public void appendJsonArray(JSONArray array, JSONArray value)
	{
		if(array == null || value == null)
			return;
		for(Object obj : value)
		{
			array.add(obj);
		}
	}

	/*Mutators*/
	public boolean setDefIdKey(String defIdKey)
	{
		if(defIdKey == null || defIdKey.length() == 0)
			return false;
		this.idKey = defIdKey;
		return true;
	}

	public String getDefIdKey()
	{
		return idKey;
	}

	public boolean setContainerName(String value)
	{
		if(value == null || value.length() == 0)
			return false;
		this.containerName = value;
		return true;
	}

	public String getContainerName()
	{
		return containerName;
	}

	public boolean setDefFileName(String value)
	{
		if(value == null || value.length() == 0)
			return false;
		this.fileName = value;
		return true;
	}

	public String getDefFileName()
	{
		return fileName;
	}

	public boolean setDefDataKeys(String[] value)
	{
		if(value == null || value.length == 0)
			return false;
		this.dataKeys = value;
		return true;
	}

	public String[] getDefDataKeys()
	{
		return dataKeys;
	}
}
