import java.io.File;
import org.sikuli.api.DesktopScreenRegion;
import org.sikuli.api.ImageTarget;
import org.sikuli.api.ScreenRegion;
import org.sikuli.api.Target;
import org.sikuli.api.robot.Key;
import org.sikuli.api.robot.Keyboard;
import org.sikuli.api.robot.Mouse;
import org.sikuli.api.robot.desktop.DesktopKeyboard;
import org.sikuli.api.robot.desktop.DesktopMouse;
import org.sikuli.api.visual.Canvas;
import org.sikuli.api.visual.DesktopCanvas;



public class Handle_SecuritySettingsDialog {
	
	
	
			public Mouse mouse = new DesktopMouse(); 	// Create a mouse object
			public Keyboard KB = new DesktopKeyboard();
			
			ScreenRegion s1 = new DesktopScreenRegion();
			Target t1= new ImageTarget(new File("resources/Filename_LABEL.png"));
			
			ScreenRegion r = s1.wait(t1,3000);  //waiting for the target to appear over the screen, after 80 seconds time out
			
			{
				
  		          
				
				
			r= s1.find(t1);    					
			if (r != null){
					mouse.click(r.getCenter());         // Use the above mouse object then we are clicking on the center of the target region		
					KB.type(Key.DELETE);KB.type(Key.DELETE);
					KB.type("c:\\helo12.html");
					
					t1= new ImageTarget(new File("resources/File_Save.png"));
					r=s1.find(t1);
					if( r!= null) mouse.click(r.getCenter());	// CLICK on SAVE bUT			
					else System.out.println("UNABLE TO FIND THE SAVE BUTTON");
				}
			else System.out.println("SAVE_FILENAME LABEL NOT FOUND");
		
}
}