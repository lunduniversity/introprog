package cslib.examples;

import java.util.Scanner;

import cslib.window.SimpleWindow;

public class SimpleWindowTest {

	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		int i = 0;
		boolean quit = false;
		System.out.println("Skriv w<enter> för nytt fönster; q<enter> för quit");
		SimpleWindow.setExitOnLastClose(false);
		while (!quit) {
			String line = scan.nextLine();
			if (line.equals("w")) {
				SimpleWindow w = new SimpleWindow(200, 200, "Test" + i); 
				w.moveTo(20, 20);
				w.writeText("Klicka i fönstret!");
				w.waitForMouseClick();
				w.writeText("##################");
				System.out.println("Mouse x,y: " + w.getMouseX()+ "," + w.getMouseY());
			} else if (line.equals("q")) {
				quit = true;
			}
			i += 1;
		}
        System.out.println("Klicka och knappa!");
		quit = false;
		SimpleWindow w = new SimpleWindow(200, 200, "Klicka och knappa och stäng"); 
		while (!quit) {
			w.waitForEvent();
			if (w.getEventType() == SimpleWindow.MOUSE_EVENT) {
				System.out.println("Mouse x,y: " + w.getMouseX()+ "," + w.getMouseY());
			} else if (w.getEventType() == SimpleWindow.KEY_EVENT) {
				System.out.println("Key:" + w.getKey());
			} else if (w.getEventType() == SimpleWindow.CLOSE_EVENT) {
				System.out.println("Klickade i stängrutan!");
				quit = true;
			}
		}
		w = new SimpleWindow(200, 200, "Non-blocking window"); 
		quit = false;
		while (!quit) {
			if (w.isEventAvailable()){
				if (w.getEventType() == SimpleWindow.MOUSE_EVENT) {
					System.out.println("Mouse x,y: " + w.getMouseX()+ "," + w.getMouseY());
				} else if (w.getEventType() == SimpleWindow.KEY_EVENT) {
					System.out.println("Key:" + w.getKey());
				} else if (w.getEventType() == SimpleWindow.CLOSE_EVENT) {
					System.out.println("Klickade i stängrutan!");
					quit = true;
				}
			}
			w.delay(500);
			System.out.println("Tick!");
		}
        System.out.println("Hejdå!");
        // System.exit(0);
	}
}
