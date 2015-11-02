package bank;

public class Account
{
   long id;
   long amt;
   Account()
   {
      id = 0L;
      amt = 0L;
   }
   
   Account(long id, long amt)
   {
      this.id = id;
      this.amt = amt;
   }
}
