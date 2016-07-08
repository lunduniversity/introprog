import java.net.URL;
import java.util.ArrayList;
import java.util.{Set => JSet};
import java.util.{HashSet => JHashSet};
import java.util.Scanner;

object Hangman {  // This is Java-like, non-idiomatic Scala code!
    private var hangman: Array[String] = Array[String](
        " ======  ",
        " |/   |  ",
        " |    O  ",
        " |   -|- ",
        " |   / \\ ",
        " |       ",
        " |       ",
        " ==========================   RIP  :(");
        
    private def renderHangman(n: Int): Unit = {
        var result: StringBuilder = new StringBuilder();
        for (i: Int <- 0 until n){
            result.append(hangman(i));
        }
        return result.toString;
    }
    
    private def hideSecret(secret: String, 
                           found: JSet[Character]): String = {
        var result: String = "";
        for (i: Int <- 0 until secret.length()) {
            if (found.contains(secret.charAt(i))) {
                result += secret.charAt(i);
            } else { 
                result += '_';
            }
        }
        return result;
    }
    
    private def foundAll(secret: String, 
                         found: JSet[Character]): Boolean = {
        var foundMissing: Boolean = false;
        var i: Int = 0;
        while (i < secret.length() && !foundMissing) {
            foundMissing = !found.contains(secret.charAt(i));
            i += 1;
        } 
        return !foundMissing;
    }
    
    private def makeGuess(): Char = {
        var scan: Scanner = new Scanner(System.in);
        var guess: String = "";
        do {
           System.out.println("Gissa ett tecken: ");
           guess = scan.next();
        } while (guess.length() > 1);
        return Character.toLowerCase(guess.charAt(0));
    }

    def download(address: String, coding: String): String = {
        var result: String = "lackalänga";
        try {   
            var url: URL = new URL(address);
            var words: ArrayList[String] = new ArrayList[String]();
            var scan: Scanner = new Scanner(url.openStream(), coding);
            while (scan.hasNext()) {
                words.add(scan.next());    
            }
            var rnd: Int = (Math.random() * words.size()).asInstanceOf[Int];
            result = words.get(rnd);
        } catch { case e: Exception =>  
            System.out.println("Error: " + e);
        }
        return result;
    }

    def play(secret: String): Unit = {
        var found: JSet[Character] = new JHashSet[Character]();
        var bad: Int = 0;
        var won: Boolean = false;
        while (bad < hangman.length && !won){
            System.out.println(renderHangman(bad));
            System.out.print("Felgissningar: " + bad + "\t");
            System.out.println(hideSecret(secret, found));
            var guess: Char = makeGuess();
            if (secret.indexOf(guess) >= 0) {
                found.add(guess);
            } else {
              bad += 1;
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

    def main(args: Array[String] ): Unit = {
        if (args.length == 0) {
            var runeberg: String = 
                "http://runeberg.org/words/ord.ortsnamn.posten";
            play(download(runeberg, "ISO-8859-1"));
        } else {
            var rnd: Int = (Math.random() * args.length).asInstanceOf[Int];
            play(args(rnd));
        }
    }
}
