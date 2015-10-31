package bank;

/**
 * Simple class to represent currency value and type.
 * @author Charles Chen
 *
 */
public class Currency {
	String type;
	Long amt;
	
	public Currency(Long amount, String currType)
	{
		amt = amount;
		type = currType;
	}
	
	public String toString()
	{
		return String.valueOf(amt) + " " + type;
	}
}
