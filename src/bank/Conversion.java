package bank;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.lang3.tuple.MutablePair;

public class Conversion
{
   // Rate to with respective to bank currency.
   // rate = curr/default 
   public ArrayList<MutablePair<String, Long>> rates;
   
   // Constructor to set default currency name.
   Conversion(String defCur)
   {
      rates = new ArrayList<MutablePair<String, Long>>();
      rates.add(new MutablePair<String, Long>(defCur, new Long(1)));
      // Other currency test rate. 
      rates.add(new MutablePair<String, Long>("SHELL", new Long(5)));
   }
   
   /**
    * Throws CurrencyTypeException if format is invalid.
    * Checks if fromType is equal to toType, if it is, returns original
    * value. Case insensitive.
    */
   public long convertCurrency(long value, String fromType, String toType)
     throws CurrencyTypeException
   {
	  if(fromType.equalsIgnoreCase(toType))
		  return value;
	  
      Iterator<MutablePair<String, Long>> it = rates.iterator();
      boolean fromCheck = false;
      boolean toCheck = false;
      long fromTypeRate, toTypeRate;
      fromTypeRate = 0;
      toTypeRate = 0;
      while(it.hasNext())
      {
         MutablePair<String, Long> result = it.next();
         if(result.left.equalsIgnoreCase(fromType))
         {
            fromCheck = true;
            fromTypeRate = result.right; 
         }
         else if(result.left.equalsIgnoreCase(toType))
         {
            toCheck = true;
            toTypeRate = result.right;
         }
         else if(fromCheck == true && toCheck == true)
            break;
      }
      
      if(fromCheck == false)
         throw new CurrencyTypeException(fromType);
      else if(toCheck == false)
         throw new CurrencyTypeException(toType);
      
      if(fromTypeRate == 0)
         return 0;
      
      // Convert to federal currency.
      return (1/fromTypeRate) * value * toTypeRate;
   }
   
   /**
    * Simple search to see if currency exists.
    */
   public boolean currencyExists(String type)
   {
      Iterator<MutablePair<String, Long>> it = rates.iterator();
      while(it.hasNext())
      {
         if(it.next().left.equalsIgnoreCase(type))
            return true;
      }
      
      return false;
   }
}
