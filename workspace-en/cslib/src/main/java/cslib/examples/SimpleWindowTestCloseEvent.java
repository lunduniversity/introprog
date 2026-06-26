package cslib.examples;

import java.util.Scanner;

import cslib.window.SimpleWindow;

public class SimpleWindowTestCloseEvent {

	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		int i = 0;
		boolean quit = false;
		System.out.println("Write w<enter> multiple times to open new windows.");
		System.out.println("Write q<enter> for quit.");
		SimpleWindow.setExitOnLastClose(false);
		while (!quit) {
			  String line = scan.nextLine();
			  if (line.equals("w")) {
				  SimpleWindow w = new SimpleWindow(200, 200, "Test" + i); 
				  w.moveTo(20, 20);
				  w.writeText("Click in the window!");
				  w.waitForMouseClick();
				  w.moveTo(w.getMouseX(), w.getMouseY());
				  w.writeText("KLICK!");
				  System.out.println("Mouse x,y: " + w.getMouseX()+ "," + w.getMouseY());
			  } else if (line.equals("q")) {
				  quit = true;
			  }
			  i += 1;
    }

    System.out.println("Hello!");
    System.exit(0);
	}
}
