package bank;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
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
	/// String used to identify various banks.
	private String bankName = "SKKBank";

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
			return new Currency(0.0, defCurrency);

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
	 * Call function to transact a user's currency to another. Returns false if user is same.
	 */
	public boolean transact(String fromUserName, String toUserName, Double value,
			String type) throws TransactionException
	{
		if(fromUserName.equalsIgnoreCase(toUserName))
			return false;
		// Get users.
		User fromUser = userBase.getUser(fromUserName);
		User toUser = userBase.getUser(toUserName);
		
		// Check if user exists.
		if(fromUser == null)
			throw new TransactionException("User does not exist: " + fromUser);
		else if(toUser == null)
			throw new TransactionException("User does not exist: " + toUser);
		
		// Convert the currency to default value for transaction.
		if(!type.equalsIgnoreCase(this.defCurrency))
		{
			try
			{
				value = converter.convertCurrency(value, type, defCurrency);
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
		if (toAcc == null)
			toAcc = registerAccount(toUser.getID());
		
		Double fromAmt = fromAcc.amt;
		Double toAmt = toAcc.amt;

		if(fromAmt < value)
			throw new TransactionException("Not enough money: " + fromUserName);
		
		Account fromFAccount = new Account(fromUser.getID(), fromAmt - value);
		Account toFAccount = new Account(toUser.getID(), toAmt + value);
		
		Account.roundAmt(fromFAccount);
		Account.roundAmt(toFAccount);
		
		this.forceAddData(String.valueOf(fromUser.getID()), fromFAccount);
		this.forceAddData(String.valueOf(toUser.getID()), toFAccount);
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
	public Account registerAccount(Long id)
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

	public boolean setBankName(String name)
	{
		if(name == null)
			return false;
		bankName = name;
		return true;
	}
	
	public Conversion getConversion()
	{
		return converter;
	}
	
	public String getDefCurrency()
	{
		return defCurrency;
	}
	
	public String getBankName()
	{
		return bankName;
	}

	public Account rawDataToData(String idVal, String[] key, String[] data)
	{
		if(data == null || data.length == 0)
			return new Account();

		return new Account(Long.valueOf(idVal), Double.valueOf(data[0]));
	}

	public  Pair<ArrayList<String>, ArrayList<String>> dataToRawData(Account data)
	{
		ArrayList<String> names = new ArrayList<String>();
		ArrayList<String> values = new ArrayList<String>();
		
		names.add(this.getDefIdKey()); values.add(String.valueOf(data.id));
		names.add(this.getDefDataKeys()[0]); values.add(String.valueOf(data.amt));
		
		Pair<ArrayList<String>, ArrayList<String>> result = 
				new ImmutablePair<ArrayList<String>, ArrayList<String>>(names, values);
		
		return result;
	}
}
