import java.util.Scanner; 

public class SwitchForgotBreak {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        System.out.println("Skriv grönsak:");
        String g = scan.next();
        switch (g) {
        case "gurka": 
            System.out.println("gott!");
        case "tomat": 
            System.out.println("mycket gott!");
            break;
        case "broccoli": 
            System.out.println("ganska gott...");
            break;
        default:
            System.out.println("mindre gott...");
            break;
        }
    }
}
