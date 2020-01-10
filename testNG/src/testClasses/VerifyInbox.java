package testClasses;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;

public class VerifyInbox {
  
  @Test
  public void checkEmail() {
	  System.out.println("Checking the Email");
  }

  
  @BeforeClass
  public void beforeClass() {
	  System.out.println("MOVING IN TO THE CLASS : "+this.getClass().getSimpleName());
  }
  

  @AfterClass
  public void afterClass() {
	  System.out.println("MOVING OUT OF THE CLASS : "+this.getClass().getSimpleName());
  }

}
