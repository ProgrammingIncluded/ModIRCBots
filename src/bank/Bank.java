package bank;

import java.util.LinkedHashMap;

import org.apache.log4j.Logger;

import ircmodbot.FileMemory;
import ircmodbot.User;
import ircmodbot.UserBase;

/**
 * Class in charge of keep track of currency for each registered player.
 * @author Charles
 *
 */
public class Bank extends FileMemory<Account>
{
	private static final Logger LOGGER = Logger.getLogger(FileMemory.class);

	/// Pointer to user checking.
	private UserBase userBase;
	/// Converter used for converting currency.
	private Conversion converter;
	/// Default currency used by bank.
	private String defCurrency = "SKKC";

	/**
	 * Default constructor for bank. 
	 */
	public Bank(UserBase userBase)
	{
		this(userBase, "SKKC");
	}

	public Bank(UserBase userBase, String defCurrency)
	{
		super();
		this.userBase = userBase;
		if(!this.setRoot("system/data"))
		{
			LOGGER.error("Unable to set root directory for UserBase.");
			System.exit(1);
		}
		this.setDefIdKey("id");
		this.setContainerName("users");
		this.setDefFileName("bank.json");

		String keys[] = {"amt"};
		this.setDefDataKeys(keys);

		converter = new Conversion(defCurrency);
	}

	/**
	 * Accessor function to get current money that user has in bank.
	 * Returns zero if account does not exist.
	 */
	public Currency getValue(String user)
	{
		return getValue(user, defCurrency);
	}

	/**
	 * Accessor function to get current money that user has in bank.
	 * Returns zero if account does not exist.
	 */
	public Currency getValue(String userName, String curr)
	{
		User user = userBase.getUser(userName);
		Account account = this.getData(String.valueOf(user.getID()));

		if(account == null)
			return new Currency(0L, defCurrency);

		Currency result = null;
		try
		{
			result = new Currency(converter.convertCurrency(account.amt, defCurrency, curr), curr);
		}
		catch(CurrencyTypeException e)
		{
			LOGGER.debug("Invalid currency given. Using default...", e);
			result = new Currency(account.amt, defCurrency);
		}
		return result;
	}

	/**
	 * Call function to transact a user's currency to another.
	 */
	public boolean transact(String fromUserName, String toUserName, long value,
			String type) throws TransactionException
	{
		// Get users.
		User fromUser = userBase.getUser(fromUserName);
		User toUser = userBase.getUser(toUserName);
		
		// Check if user exists.
		if(fromUser == null)
			throw new TransactionException("User does not exist: " + fromUser);
		else if(toUser == null)
			throw new TransactionException("User does not exist: " + toUser);
		
		// Convert the currency to default value for transaction.
		long convertValue = 1;
		if(!type.equalsIgnoreCase(this.defCurrency))
		{
			try
			{
				convertValue = converter.convertCurrency(value, type, defCurrency);
			}
			catch(CurrencyTypeException e)
			{
				LOGGER.error("Unable to convert currency in Bank: " + type);
				LOGGER.debug("Unable to convert currency in Bank: " + type);
				throw new TransactionException("Invalid currency: " + type);
			}
		}
		// Get Accounts
		Account fromAcc = this.getData(String.valueOf(fromUser.getID()));
		Account toAcc = this.getData(String.valueOf(toUser.getID()));
		// Register new account if not exist.
		if(fromAcc == null)
			fromAcc = registerAccount(fromUser.getID());
		else if (toAcc == null)
			toAcc = registerAccount(toUser.getID());
		
		Long fromAmt = fromAcc.amt;
		Long toAmt = toAcc.amt;

		if(fromAmt < value)
			throw new TransactionException("Not enough money: " + fromUserName);

		this.forceAddData(fromUserName, new Account(fromUser.getID(), fromAmt - value));
		this.forceAddData(toUserName, new Account(toUser.getID(), toAmt + value));
		return true;
	}

	public boolean processTransaction(Transaction t) throws TransactionException
	{
		return transact(t.getFrom(), t.getTo(), 
				t.getAmount(), t.getCurrencyType());
	}

	/**
	 * Creates account with given unique ID and sets value to zero.
	 * ID must not be used before or returns false.
	 */
	public Account registerAccount(long id)
	{
		Account result = new Account(id, 0);
		if(this.addData(String.valueOf(id), result) == false)
		{
			return null;
		}
		return result;
	}
	
	/**
	 * Sets default currency of bank. Use with caution as it will reload
	 * converter due to rate changes relative to default currency.
	 */
	public boolean setDefCurrency(String currency)
	{
		if(defCurrency == null)
			return false;
		
		converter = new Conversion(currency);
		defCurrency = currency;
		
		return true;
	}

	public String getDefCurrency()
	{
		return defCurrency;
	}

	public Account rawDataToData(String idVal, String[] key, String[] data)
	{
		if(data == null || data.length == 0)
			return new Account();

		return new Account(Long.valueOf(idVal), Long.valueOf(data[0]));
	}

	public LinkedHashMap<String, String> dataToRawData(Account data)
	{
		LinkedHashMap<String, String> result = new LinkedHashMap<String,String>();
		result.put(this.getDefIdKey(), String.valueOf(data.id));
		result.put(this.getDefDataKeys()[0], String.valueOf(data.amt));
		return result;
	}
}
