package cslib.examples;

import java.util.Scanner;

import cslib.window.SimpleWindow;

public class SimpleWindowTestNonBlocking {

	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		boolean quit = false;
		SimpleWindow w = new SimpleWindow(200, 200, "Non-blocking window"); 
		while (!quit) {
		    w.waitForEvent(500);
        if (w.getEventType() == SimpleWindow.MOUSE_EVENT) {
			      System.out.println("Mouse x,y: " + w.getMouseX()+ "," + w.getMouseY());
				} else if (w.getEventType() == SimpleWindow.KEY_EVENT) {
					  System.out.println("Key:" + w.getKey());
				} else if (w.getEventType() == SimpleWindow.CLOSE_EVENT) {
					  System.out.println("Klickade i stängrutan!");
					  quit = true;
				} else if (w.getEventType() == SimpleWindow.TIMEOUT_EVENT) {
			    SimpleWindow.delay(500);
			    System.out.print(" tick ");
			  }
		}
    System.out.println("Hejdå!");
    System.exit(0);
	}
}
