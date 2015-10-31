package bank;

/**
 * Class in charge of emulating a transaction within the system from one
 * price with holder to another. DEPRECATE
 * @author Charles
 *
 */
public class Transaction
{
   private String currencyType;
   private String from;
   private String to;
   private long amt;
   Transaction()
   {
      
   }
   
   Transaction(String currencyType, String from, String to, long amt)
   {
      
   }
   
   public boolean setCurrencyType(String value)
   {
      if(value == null)
         return false;
      
      currencyType = value;
      return true;
   }
   
   public String getCurrencyType()
   {
      return currencyType;
   }
   
   public boolean setFrom(String value)
   {
      if(value == null)
         return false;
      
      from = value;
      return true;
   }
   
   public String getFrom()
   {
      return from;
   }
   
   public boolean setTo(String value)
   {
      if(value == null)
         return false;
      
      to = value;
      return true;
   }
   
   public String getTo()
   {
      return to;
   }
   
   public boolean setAmount(long value)
   {
      amt = value;
      return true;
   }
   
   public long getAmount()
   {
      return amt;
   }
}
