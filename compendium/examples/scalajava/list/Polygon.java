package week10.list;

import java.util.ArrayList;
import se.lth.cs.pt.window.SimpleWindow;

public class Polygon {
    protected ArrayList<Point> vertices; // lista med hörnpunkter
    
    /** Skapar en polygon */
    public Polygon() {
        vertices = new ArrayList<Point>();
    }
    
    /** Definierar en ny punkt med koordinaterna x,y */
    public void addVertex(int x, int y) {  
        vertices.add(new Point(x, y));
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
        Point start = vertices.get(0);
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
    	vertices.add(pos, new Point(x, y));
    }
    
    /** Tar bort punkten på plats pos. Efterföljande
        element flyttas */
    public void removeVertex(int pos) {
    	vertices.remove(pos);
    }
    
    public Point getVertex(int pos) {
    	return vertices.get(pos);
    }
    
    public int size() {
    	return vertices.size();
    }
    
    public boolean hasVertex(int x, int y){
        for (int i =0; i < vertices.size(); i++){
            if (vertices.get(i).getX() == x && vertices.get(i).getY() == y){
                return true;
            }
        }
        return false;
    }
}
