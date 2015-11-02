package bank;

/**
 * Simple class to represent currency value and type.
 * @author Charles Chen
 *
 */
public class Currency {
	String type;
	Double amt;
	
	public Currency(Double amount, String currType)
	{
		amt = amount;
		type = currType;
	}
	
	public String toString()
	{
		return String.valueOf(amt) + " " + type;
	}
}
