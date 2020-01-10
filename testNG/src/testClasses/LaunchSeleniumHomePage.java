package testClasses;

import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class LaunchSeleniumHomePage {
	
	
	LaunchSeleniumHomePage(){
		System.out.print("\n In theDefault Constructor method");

	}
	
	
	/*@BeforeSuite
	public void oneTimeSuiteSetup(){
		System.out.print("\nIn the BeforeSuite method >>");
	}

	@AfterSuite
	public void oneTimeSuiteBreakDown(){
		System.out.print("\nIn the AfterSuite method >>");
	}*/

	
	@BeforeClass
	public void oneTimeSetUp(){
		System.out.print("\nIn the BeforeClass method >>");		
	}
	
	@AfterClass
	public void oneTimeCloseUp(){
		System.out.print("\nIn the AfterClass method<>");
	}	
	
	@BeforeTest
	public void beforeTest(){
		
		
		System.out.print("\nIn the Beforetest method >>");
	}

	@AfterTest
	public void afterTest(){
		System.out.print("\nIn the Aftertest method >>");
	}
		
	@Test
	public void test1(){
		System.out.print("\nIn the test1 method >>");	
	}
	
	@Test
	public void test2(){
		System.out.print("\nIn the test2 method >>");	
	}
		
	@Test
	public void test3(){
		System.out.print("\nIn the test3 method >>");	
	}

	

/*	@AfterMethod
	public void disk(){
		
	}
	
	*/
	
	
}
