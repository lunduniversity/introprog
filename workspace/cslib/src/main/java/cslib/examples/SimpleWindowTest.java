package cslib.examples;

import java.util.ArrayList;
import java.util.Scanner;

import cslib.window.SimpleWindow;

public class SimpleWindowTest {

	public static void main(String[] args) {
		ArrayList<SimpleWindow> ws = new ArrayList();
		Scanner scan = new Scanner(System.in);
		int i = 0;
		boolean quit = false;
		System.out.println("Skriv w<enter> för nytt fönster; q<enter> för quit");
		SimpleWindow.setExitOnLastClose(false);
		while (!quit) {
			i += 1;
			String line = scan.nextLine();
			if (line.equals("w")) {
				ws.add(new SimpleWindow(200, 200, "Test" + i));
			} else if (line.equals("q")) {
				quit = true;
			}
		}
        System.out.println("Hejdå!");
        System.exit(0);
	}
}
