package bank;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class Account
{
   long id;
   double amt;
   Account()
   {
      id = 0;
      amt = 0;
   }
   
   Account(long id, double amt)
   {
      this.id = id;
      this.amt = amt;
   }
   
   public static double roundDouble(Double amt)
   {
	   // Use Decimal Format?
	   DecimalFormat df = new DecimalFormat("#.##");
	   df.setRoundingMode(RoundingMode.CEILING);
	   return Double.valueOf(df.format(amt));
   }
   
   public static void roundAmt(Account acc)
   {
      acc.amt = Account.roundDouble(acc.amt);
   }
}
