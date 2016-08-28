import java.util.ArrayList;

public class Autoboxing {
    public static void main(String[] args) {
        ArrayList<Integer> xs = new ArrayList<Integer>();
        for (int i = 0; i < 42; i++) {
            xs.add(new Integer(i));
        }
        for (Integer x: xs) { 
            int y = x.intValue() * 10;
            System.out.print(y + " ");
        }
        int pos = xs.size();
        xs.add(pos, new Integer(0));
        System.out.println("\n\n[0]: " + xs.get(0).intValue());
        System.out.println("[" + pos + "]: " + xs.get(pos));
        if (xs.get(0) == xs.get(pos)) {
            System.out.println("EQUAL");
        } else {
            System.out.println("NOT EQUAL");
        }
    }
}
