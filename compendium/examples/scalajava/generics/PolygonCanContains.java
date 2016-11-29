import java.util.ArrayList;

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
