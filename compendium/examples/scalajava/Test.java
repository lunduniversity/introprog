class Point {
    private int x;
    private int y;
    
    public Point(int x, int y){
        this.x = x;
        this.y = y;
    }
}

public class Test {
    public static void main(String[] args){
        int i1 = 0;
        int i4;
        Point p = new Point(0, 0);
        final int CONSTANT = 42;
        
        String s = "Abbasillen";

        //Loopa över index framlänges:

        for (int i = 0; i < s.length(); i++){
            System.out.println(s.charAt(i));
        }

        //Loopa över index baklänges:
         
        for (int i = s.length()-1; i >= 0; i--){
            System.out.println(s.charAt(i));
        }


        //Loopa över alla tecken:

        for (char ch: s.toCharArray()) {
          System.out.println(ch);
        }

        int[] xs = {42, 43, 44};

        String[] strings = new String[10];

        strings[0] = "first";

        strings[1] = "second";

        int n = strings.length;   //OBS! EJ length()


        String x = "hej";

        boolean isString = x instanceof String;
        
        int y = 42;

        double z = (double) y;
        
double r = Math.random();
int answer = (r > 0.5) ? 42 : 0;

    }
}
