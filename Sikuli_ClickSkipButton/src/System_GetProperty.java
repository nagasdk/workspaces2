import static org.openqa.selenium.Platform.MAC;
import static org.openqa.selenium.Platform.WINDOWS;

import java.io.File;
import java.io.IOException;

import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriverException;


public class System_GetProperty {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
	//	System.out.println(locateFirefoxBinaryFromSystemProperty());
	    //System.out.println(System.getProperty("webdriver.firefox.bin"));
	    
		try {
			Runtime.getRuntime().exec("notepad.exe");
			// Runtime.getRuntime().exec("cmd /c firefox.exe");

			}
			catch(Exception e){
			e.printStackTrace();
		}
	    
	    
	    
	}

	  private static File locateFirefoxBinaryFromSystemProperty() {
		    String binaryName = System.getProperty("webdriver.firefox.bin");
		    if (binaryName == null)
		      return null;

		    File binary = new File(binaryName);
		    if (binary.exists())
		      return binary;

		    Platform current = Platform.getCurrent();
		    if (current.is(WINDOWS)) {
		      if (!binaryName.endsWith(".exe"))
		        binaryName += ".exe";

		    } else if (current.is(MAC)) {
		      if (!binaryName.endsWith(".app"))
		        binaryName += ".app";
		      binaryName += "/Contents/MacOS/firefox-bin";
		    }

		    binary = new File(binaryName);
		    if (binary.exists())
		      return binary;

		    throw new WebDriverException(
		        String
		            .format(
		                "\"webdriver.firefox.bin\" property set, but unable to locate the requested binary: %s",
		                binaryName
		            ));
		  }

}
