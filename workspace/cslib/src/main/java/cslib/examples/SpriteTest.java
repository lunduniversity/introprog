package cslib.examples;

import cslib.window.SimpleWindow;
import cslib.window.Sprite;
import java.io.File;

public class SpriteTest {

	static final int WIN_SIZE = 600;
	static final int SPRITE_SIZE = 30;
	static final int ROTATION = 5;
	static final int LEAP = 10;

	public static void main(String[] args) {
		SimpleWindow w = new SimpleWindow(WIN_SIZE, WIN_SIZE, "SpriteTest");
		File f = new File(".");
		System.out.println("Current directory:" +  f.getAbsolutePath());
		Sprite turtle = new Sprite("src/main/resources/turtle.png", SPRITE_SIZE, SPRITE_SIZE);
		w.addSprite(turtle);
		turtle.moveMidTo(WIN_SIZE / 2, WIN_SIZE / 2);
		while (true) {
			System.out.println("Riktning:   " + turtle.getDirection());
			System.out.println("Läge (x,y): (" + turtle.getX() + "," + turtle.getY() + ")");
			System.out.println("Mitt (x,y): (" + turtle.getMidX() + "," + turtle.getMidY() + ")");

			w.waitForEvent(); 

			if (w.getEventType() == SimpleWindow.KEY_EVENT) {
				switch (w.getKey()) {
				case 'a':
					turtle.rotate(+ROTATION);
					break;
				case 's':
					turtle.rotate(-ROTATION);
					break;
				case ' ':
					turtle.forward(LEAP);
					break;
				case 'r':
					int someRandomX = (int) (Math.random() * WIN_SIZE + 1);
					int someRandomY = (int) (Math.random() * WIN_SIZE + 1);
					turtle.moveTo(someRandomX, someRandomY);
					break;
				case 'h':
					turtle.setVisible(! turtle.isVisible());
					break;	
				default:
					break;
				}
			}
		}
	}

}
