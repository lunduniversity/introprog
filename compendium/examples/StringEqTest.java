public class StringEqTest {
    public static void main(String[] args){
        boolean eqTest1 = 
          (new String("hej")) == (new String("hej"));
        boolean eqTest2 = 
          (new String("hej")).equals(new String("hej"));
        int eqTest3 = 
          (new String("hej")).compareTo(new String("hej"));
        System.out.println(eqTest1);
        System.out.println(eqTest2);
        System.out.println(eqTest3);
    }
}
