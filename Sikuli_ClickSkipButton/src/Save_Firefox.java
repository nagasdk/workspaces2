import java.io.File;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.sikuli.api.DesktopScreenRegion;
import org.sikuli.api.ImageTarget;
import org.sikuli.api.ScreenRegion;
import org.sikuli.api.Target;
import org.sikuli.api.robot.Key;
import org.sikuli.api.robot.Keyboard;
import org.sikuli.api.robot.Mouse;
import org.sikuli.api.robot.desktop.DesktopKeyboard;
import org.sikuli.api.robot.desktop.DesktopMouse;


public class Save_Firefox {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		WebDriver driver;
		FirefoxBinary pathToFirefoxBinary= new FirefoxBinary(new File("D:/Program Files/Mozilla Firefox/firefox.exe"));

		ProfilesIni profile = new ProfilesIni(); 		FirefoxProfile ffprofile = profile.getProfile("default");		ffprofile.setPreference("network.proxy.type", 0);

		driver = new FirefoxDriver(pathToFirefoxBinary,ffprofile);
		
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		driver.manage().window().maximize();    
		driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);

 
		// driver.get("http://www.youtube.com/watch?v=HcA1YA6Fp0E"); //ADV for 3.59 SECONDS
		driver.get("http://www.youtube.com"); //ADV for 3.59 SECONDS
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Mouse mouse = new DesktopMouse(); 	// Create a mouse object
		Keyboard KB = new DesktopKeyboard();
		
		ScreenRegion s1 = new DesktopScreenRegion();
		Target t1= new ImageTarget(new File("resources/Filename_LABEL.png"));
		
		ScreenRegion r = s1.wait(t1,3000); //waiting for the target to appear over the screen, after 80 seconds time out
		r= s1.find(t1);    //locating the target image over the screen
		
		
		/*		Canvas canvas = new DesktopCanvas();  	// CONSTRUCT A CANVAS OBJECT OF TYPE DESKTOP CANVAS
				canvas.addBox(r); 						// ADD A BOX REGION AROUND  a  SCREENREGION R, THIS IS FOR HIGHLIGHTING AFTER IT IS FOUND
				canvas.addLabel(r, "WE FOUND IT");  	// AFTER FINDING <WE CAN HIGHLGHT THIS INA  CANVAS AND ALSO PUT A LABEL SAYING WE FOUND IT AND 
				canvas.display(3);                  	// AFTER FINDING THIS OVER CANVAS WE CAN HIGHLIGHT THIS FOR  3 SECONDS 
		*/		
		if (r != null){
				mouse.click(r.getCenter());         // Use the above mouse object then we are clicking on the center of the target region		
				KB.type(Key.DELETE);KB.type(Key.DELETE);
				KB.type("c:\\helo12.html");
				
				t1= new ImageTarget(new File("resources/File_Save.png"));
				r=s1.find(t1);
				if( r!= null)
					mouse.click(r.getCenter());	// CLICK on SAVE bUT
				else
					System.out.println("UNABLE TO FIND THE SAVE BUTTON");
				
			}
		else
			System.out.println("SAVE_FILENAME LABEL NOT FOUND");
			
    driver.close();
	}
}
