import java.util.List;
import java.util.ArrayList;

public class JPoint {
    private int x, y;
  
    public JPoint(int x, int y){
      this.x = x;
      this.y = y;
    }

    public JPoint(int x, int y, boolean save){
        this(x, y);
        if (save) {
            saved.add(0, this);
        }
    }
  
    public JPoint(){
        this(0, 0);
    }
  
    public int getX(){
        return x;
    }
  
    public int getY(){
        return y;
    }
    
    public double distanceTo(JPoint that) {
      return distanceBetween(this, that);
    }
    
    @Override public String toString() {
        return "JPoint(" + x + ", " + y + ")";
    }

    private static List<JPoint> saved = new ArrayList<JPoint>();

    public static Double distanceBetween(JPoint p1, JPoint p2) {
        return Math.hypot(p1.x - p2.x, p1.y - p2.y);
    }
    
    public static void showSaved() {
        System.out.print("Saved: ");
        for (int i = 0; i < saved.size(); i++){
          System.out.print(saved.get(i));
          if (i < saved.size() - 1) {
            System.out.print(", ");
          }
        } 
        System.out.println();
    }
}
