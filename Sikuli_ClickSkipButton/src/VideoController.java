import java.awt.Robot;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.*;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.sikuli.api.DesktopScreenRegion;
import org.sikuli.api.ImageTarget;
import org.sikuli.api.ScreenRegion;
import org.sikuli.api.Target;
import org.sikuli.api.robot.Mouse;
import org.sikuli.api.robot.desktop.DesktopMouse;
import org.sikuli.api.visual.Canvas;
import org.sikuli.api.visual.DesktopCanvas;


public class VideoController {

	
	public static void main(String[] args) throws IOException, InterruptedException{
		WebDriver driver;
		FirefoxBinary pathToFirefoxBinary= new FirefoxBinary(new File("D:/Program Files/Mozilla Firefox/firefox.exe"));

		ProfilesIni profile = new ProfilesIni(); 
		FirefoxProfile ffprofile = profile.getProfile("default");
		ffprofile.setPreference("network.proxy.type", 0);

		//driver = new FirefoxDriver();
		// driver = new FirefoxDriver(ffprofile);		
		driver = new FirefoxDriver(pathToFirefoxBinary,ffprofile);
		
		
		Thread.sleep(2000);
		
		driver.manage().window().maximize();    
		driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);

		//driver.get("https://www.youtube.com/watch?v=XU3PsRNNypc"); 
		driver.get("http://www.youtube.com/watch?v=HcA1YA6Fp0E"); //ADV for 3.59 SECONDS
		
		
		ScreenRegion s1 = new DesktopScreenRegion();
		Target t1= new ImageTarget(new File("D:/EclipseJuno/Workspace_PractiseSelenium/Sikuli_ClickSkipButton/resources/SkipAdI.png"));
		
		ScreenRegion r = s1.wait(t1,80000); //waiting for the target to appear over the screen, after 80 seconds time out
		r= s1.find(t1);    //locating the target image over the screen
		
		
		Canvas canvas = new DesktopCanvas();  	// CONSTRUCT A CANVAS OBJECT OF TYPE DESKTOP CANVAS
		canvas.addBox(r); 						// ADD A BOX REGION AROUND  a  SCREENREGION R, THIS IS FOR HIGHLIGHTING AFTER IT IS FOUND
		canvas.addLabel(r, "WE FOUND IT");  	// AFTER FINDING <WE CAN HIGHLGHT THIS INA  CANVAS AND ALSO PUT A LABEL SAYING WE FOUND IT AND 
		canvas.display(3);                  	// AFTER FINDING THIS OVER CANVAS WE CAN HIGHLIGHT THIS FOR  3 SECONDS 
		
		if (r == null){
				System.out.println("NOT FOUND");
		}
		else{
				Mouse mouse = new DesktopMouse(); 	// Create a mouse object
				mouse.click(r.getCenter());         // Use the above mouse object then we are clicking on the center of the target region
			}
		
    driver.close();
		
		
	} //public static void main close
}




//  Runtime.getRuntime().exec("D:\nagaraju\abcd.exe");

/*Robot rb=  new Robot();
rb.keyPress(KeyEvent.VK_D);
rb.keyPress(KeyEvent.VK_SEMICOLON);
rb.keyRelease(KeyEvent.VK_SEMICOLON);
rb.keyPress(KeyEvent.VK_BACKSLASH);
rb.keyRelease(KeyEvent.VK_BACKSLASH);

rb.keyRelease(KeyEvent.VK_D);

*/
