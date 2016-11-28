package week10.generics;

import java.util.ArrayList;
import se.lth.cs.pt.window.SimpleWindow;
import week10.list.Point;

public class PolygonCanContains {
    protected ArrayList<PointCanEquals> vertices; // lista med hörnpunkter
    
    /** Skapar en polygon */
    public PolygonCanContains() {
        vertices = new ArrayList<PointCanEquals>();
    }
    
    /** Definierar en ny punkt med koordinaterna x,y */
    public void addVertex(int x, int y) {  
        vertices.add(new PointCanEquals(x, y));
    }
    
    /** Flyttar polygonen avståndet dx i x-led, dy i y-led */
    public void move(int dx, int dy) {
        for (int i = 0; i < vertices.size(); i++) {
        	vertices.get(i).move(dx, dy);
        }
    }
    
    /** Ritar polygonen i fönstret w */
    public void draw(SimpleWindow w) {
        if (vertices.size() == 0) {
            return;
        }
        PointCanEquals start = vertices.get(0);
        w.moveTo(start.getX(), start.getY());
        for (int i = 1; i < vertices.size(); i++) {
            w.lineTo(vertices.get(i).getX(), 
                     vertices.get(i).getY());
        }
        w.lineTo(start.getX(), start.getY());
    }

    /** Lägger in en ny punkt med koordinaterna x,y
        på plats pos. Efterföljande element flyttas */
    public void insertVertex(int pos, int x, int y) {
    	vertices.add(pos, new PointCanEquals(x, y));
    }
    
    /** Tar bort punkten på plats pos. Efterföljande
        element flyttas */
    public void removeVertex(int pos) {
    	vertices.remove(pos);
    }
    
    public PointCanEquals getVertex(int pos) {
    	return vertices.get(pos);
    }
    
    public int size() {
    	return vertices.size();
    }
    
    public boolean hasVertex(int x, int y){  // FUNKAR INTE!!!
        return vertices.contains(new Point(x, y)); // Point har ingen equals...
    }
    
    public boolean contains(int x, int y){  
        return vertices.contains(new PointCanEquals(x, y)); 
    }
}
