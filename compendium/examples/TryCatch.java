// TryCatch.java

public class TryCatch {
    public static void main(String[] args) {
        int input;
        int output;
        if (args[0].equals("safe")) {
            try {
                input = Integer.parseInt(args[1]);
                System.out.println("Skyddad division!");
                output = 42 / input;
            } catch (Exception e) {
                System.out.println("Undantag fångat: " + e);
                System.out.println("Dividerar ändå med säker default!");
                input = 1;
                output = 42 / input;
            }
        } else {
            input = Integer.parseInt(args[0]);
            System.out.println("Oskyddad division!");
            output = 42 / input;
        }
        System.out.println("42 / " + input + " == " + output);      
    }
}
