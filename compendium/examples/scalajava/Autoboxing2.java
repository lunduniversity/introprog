import java.util.ArrayList;

public class Autoboxing2 {
    public static void main(String[] args) {
        ArrayList<Integer> xs = new ArrayList<Integer>();
        for (int i = 0; i < 42; i++) {
            xs.add(i);
        }
        for (int x: xs) { 
            int y = x * 10;
            System.out.print(y + " ");
        }
        int pos = xs.size();
        xs.add(pos, 0);
        System.out.println("\n\n[0]: " + xs.get(0));
        System.out.println("[" + pos + "]: " + xs.get(pos));
        if (xs.get(0).equals(xs.get(pos))) {
            System.out.println("EQUAL");
        } else {
            System.out.println("NOT EQUAL");
        }
    }
}
