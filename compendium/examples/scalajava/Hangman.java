import java.net.URL;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Scanner;

public class Hangman {
     static String[] hangman = new String[]{
        " ======  ",
        " |/   |  ",
        " |    O  ",
        " |   -|- ",
        " |   / \\ ",
        " |       ",
        " |       ",
        " ========"};

    public static void printHangman(int n){
        for(int i = 0; i < n; i++){
            System.out.println(hangman[i]);
        }
    }
    
    public static String hideSecret(String secret, 
                                    Set<Character> found){
        String result = "";
        for (int i = 0; i < secret.length(); i++) {
            if (found.contains(secret.charAt(i))) {
                result += secret.charAt(i);
            } else { 
                result += '_';
            }
        }
        return result;
    }
    
    public static boolean foundAll(String secret, 
                                   Set<Character> found){
        boolean foundMissing = false;
        int i = 0;
        while (i < secret.length() && !foundMissing) {
            foundMissing = !found.contains(secret.charAt(i));
            i++;
        } 
        return !foundMissing;
    }
    
    public static char makeGuess(){
        Scanner scan = new Scanner(System.in);
        String guess = "";
        do {
           System.out.println("Gissa ett tecken: ");
           guess = scan.next();
        } while (guess.length() > 1);
        return Character.toLowerCase(guess.charAt(0));
    }

    public static int play(String secret){
        Set<Character> found = new HashSet<Character>();
        int bad = 0;
        while (bad < hangman.length && !foundAll(secret, found)){
            printHangman(bad);
            System.out.print("\nFelgissningar: " + bad + "\t");
            System.out.println(hideSecret(secret, found));
            char guess = makeGuess();
            if (secret.indexOf(guess) >= 0) {
                found.add(guess);
            } else {
              bad++;
            }
        }
        if (foundAll(secret, found)) {
            System.out.println("BRA! :)");
        } else {
            System.out.println("Hängd! :(");
        }
        System.out.println("Rätt svar: " + secret);
        return bad;           
    }

    public static String download(String address, String coding){
        String result = "eslöv";
        try {   
            URL url = new URL(address);
            ArrayList<String> words = new ArrayList<String>();
            Scanner scan = new Scanner(url.openStream(), coding);
            while (scan.hasNext()) {
                words.add(scan.next());    
            }
            int rnd = (int) (Math.random() * words.size());
            result = words.get(rnd);
        } catch (Throwable e) {
            System.out.println("Error: " + e);
            System.out.println("Använder nödlösning.");
        }
        return result;
    }
    
    public static void main(String[] args){
        int guesses = 0;
        if (args.length > 0) {
           int rnd = (int) (Math.random() * args.length);
           guesses = play(args[rnd]);
        } else {
            String runeberg = 
                "http://runeberg.org/words/ord.ortsnamn.posten";
            String latin1 = "ISO-8859-1";
            String secret = download(runeberg, latin1);
            guesses = play(secret);
        }
        System.out.println("Antal felgissningar: " + guesses);
    }
}
