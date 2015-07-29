package bank;

public class Account
{
   long id;
   long amt;
   Account()
   {
      id = 0;
      amt = 0;
   }
   
   Account(long id, long amt)
   {
      this.id = id;
      this.amt = amt;
   }
}
