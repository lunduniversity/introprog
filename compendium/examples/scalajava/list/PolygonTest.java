package week10.list;
import se.lth.cs.pt.window.SimpleWindow;

public class PolygonTest {
	public static void main(String[] args) {
		SimpleWindow w = new SimpleWindow(400, 400, "Polygon");
		w.setLineWidth(3);
		Polygon p = new Polygon();
		p.addVertex(100, 100);
		p.addVertex(200, 100);
		p.addVertex(150, 170);
		p.addVertex(175, 300);
		p.addVertex(50, 200);
		p.draw(w);
		
		w.waitForMouseClick();
		w.clear();
		p.move(150, 100);
		p.insertVertex(5, 10, 10);  
		p.draw(w); 
		for (int i = 0; i < p.size(); i++) {
			System.out.println(p.getVertex(i));
		}
		p.removeVertex(0);
		w.waitForMouseClick();
		p.draw(w);
	}
}