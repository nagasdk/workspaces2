package testClasses;

import java.io.File;

import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class TestClass_Sample1 {

	static FirefoxBinary ffBinary= null;
	static FirefoxProfile ffProfile= null;
	static Proxy proxyServer = null;
	static DesiredCapabilities DC = null; 
	
	@BeforeTest
	public void TestInitializations(){
		File ffPath=new File("D:/Program Files/Mozilla Firefox/firefox.exe");
		 ffBinary= new FirefoxBinary(ffPath);
		
		ProfilesIni profile = new ProfilesIni(); 
		ffProfile = profile.getProfile("default");
		
		proxyServer = new Proxy();
		proxyServer.setProxyAutoconfigUrl("http://infypacsrv/pearl.pac");
		DC = new DesiredCapabilities();
		DC.setCapability(CapabilityType.PROXY, proxyServer);
		
	}
	
  @Test
  public void f() {
	  WebDriver drv1= new  FirefoxDriver(ffBinary,ffProfile,DC);
	  drv1.get("http://google.com");
	  System.out.println(drv1.getCurrentUrl());
  
  }
}
