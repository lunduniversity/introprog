package cslib.examples;

import java.util.Scanner;

import cslib.window.SimpleWindow;

public class SimpleWindowTestCloseEvent {

	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		int i = 0;
		boolean quit = false;
		System.out.println("Skriv w<enter> många gånger för att öppna nya fönster.");
		System.out.println("Skriv q<enter> för quit.");
		SimpleWindow.setExitOnLastClose(false);
		while (!quit) {
			  String line = scan.nextLine();
			  if (line.equals("w")) {
				  SimpleWindow w = new SimpleWindow(200, 200, "Test" + i); 
				  w.moveTo(20, 20);
				  w.writeText("Klicka i fönstret!");
				  w.waitForMouseClick();
				  w.moveTo(w.getMouseX(), w.getMouseY());
				  w.writeText("KLICK!");
				  System.out.println("Mouse x,y: " + w.getMouseX()+ "," + w.getMouseY());
			  } else if (line.equals("q")) {
				  quit = true;
			  }
			  i += 1;
    }

    System.out.println("Hejdå!");
    System.exit(0);
	}
}
