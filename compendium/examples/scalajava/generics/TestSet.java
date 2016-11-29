import java.util.Set;
import java.util.HashSet;


public class TestSet {
    public static void main(String[] args){
        Set s = new HashSet();
        s.add(42);
        s.add("hej");
        System.out.println(s);
    }
}
