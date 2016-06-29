import java.net.URL;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Scanner;

public class Hangman {
    private static String[] hangman = new String[]{
        " ======  ",
        " |/   |  ",
        " |    O  ",
        " |   -|- ",
        " |   / \\ ",
        " |       ",
        " |       ",
        " ==========================   RIP  :("};
            
    private static String renderHangman(int n){
        StringBuilder result = new StringBuilder();    
        for (int i = 0; i < n; i++){
            result.append(hangman[i]);
        }
        return result.toString;
    }
    
    private static String hideSecret(String secret, 
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
    
    private static boolean foundAll(String secret, 
                                   Set<Character> found){
        boolean foundMissing = false;
        int i = 0;
        while (i < secret.length() && !foundMissing) {
            foundMissing = !found.contains(secret.charAt(i));
            i++;
        } 
        return !foundMissing;
    }
    
    private static char makeGuess(){
        Scanner scan = new Scanner(System.in);
        String guess = "";
        do {
           System.out.println("Gissa ett tecken: ");
           guess = scan.next();
        } while (guess.length() != 1);
        return Character.toLowerCase(guess.charAt(0));
    }

    public static String download(String address, String coding){
        String result = "lackalänga";
        try {   
            URL url = new URL(address);
            ArrayList<String> words = new ArrayList<String>();
            Scanner scan = new Scanner(url.openStream(), coding);
            while (scan.hasNext()) {
                words.add(scan.next());    
            }
            int rnd = (int) (Math.random() * words.size());
            result = words.get(rnd);
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        return result;
    }

    public static void play(String secret){
        Set<Character> found = new HashSet<Character>();
        int bad = 0;
        boolean won = false;
        while (bad < hangman.length && !won){
            System.out.println(renderHangman(bad));
            System.out.print("\nFelgissningar: " + bad + "\t");
            System.out.println(hideSecret(secret, found));
            char guess = makeGuess();
            if (secret.indexOf(guess) >= 0) {
                found.add(guess);
            } else {
              bad++;
            }
            won = foundAll(secret, found);
        }
        if (won) {
            System.out.println("BRA! :)");
        } else {
            System.out.println("Hängd! :(");
        }
        System.out.println("Rätt svar: " + secret);
        System.out.println("Antal felgissningar: " + bad);
    }

    public static void main(String[] args){
        if (args.length == 0) {
            String runeberg = 
                "http://runeberg.org/words/ord.ortsnamn.posten";
            play(download(runeberg, "ISO-8859-1"));
        } else {
            int rnd = (int) (Math.random() * args.length);
            play(args[rnd]);
        }
    }
}
