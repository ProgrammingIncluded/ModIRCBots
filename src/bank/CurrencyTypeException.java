package bank;

public class CurrencyTypeException extends Exception
{
   CurrencyTypeException(String type)
   {
      super("Currency type does not exist: " + type + ".");
   }
}
