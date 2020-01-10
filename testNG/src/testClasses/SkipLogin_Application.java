package testClasses;

import org.testng.Assert;
import org.testng.SkipException;
import org.testng.TestNG.ExitCodeListener;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;


public class SkipLogin_Application {

	static String  strActualUser;
	static String  strExpectedUser;
	
	
	@BeforeTest
	public void confirmTheUserLoginStatus(){
		if (! isLoggedin()){
			throw new SkipException("User not Logged in, so skipping this part");
		}
	}
	
	  @Test
	  public void fetchExpectedValue_User(){
		  System.out.println("executing the  'fetchExpectedValue_User() method' , fethcing the expected value from input files");
			  strExpectedUser="XXX"; 
			  // strExpectedUser="YYY";

	  }
		

	
	
  @Test(dependsOnMethods={"getActualValue_User","fetchExpectedValue_User"})
  public void compareLoggedUser(){
	     System.out.println("compareLoggedUser() method, comparing the actual user with expected user");
//		 System.out.println("ActualUser" +strActualUser);
//		 System.out.println("ExpectedUser"+strExpectedUser);
	     
	     if ((!strActualUser.equals(null)) && (!strExpectedUser.equals(null))){
	    	 
			 Assert.assertEquals(strActualUser, strExpectedUser);
			 System.out.println("Found the ActualUser:" +strActualUser);
			 System.out.println("Fetched the ExpectedUser:"+strExpectedUser);

	     }
  }
 
  @Test
  public void getActualValue_User(){
		  strActualUser = "XXX";
		  System.out.println("executing the    'ActualValue_User() method' , getting the actual value from application");

  }

  
 /* @Test
  public void testLogin()  {
	 System.out.println("testLogin() method:" + "Verify if Login is Sucessful or not ");	 
  }*/

  
  @BeforeClass
  public void call_beforeclass(){
	  System.out.println("MOVING IN TO THE CLASS :" +this.getClass().getSimpleName());
  }
  
  @AfterClass
  public void call_afterclass(){
	  System.out.println("MOVING OUT OF THE CLASS :" + this.getClass().getSimpleName());
  }
  
	public boolean isLoggedin(){
		return false;
		
	}
	
  
}