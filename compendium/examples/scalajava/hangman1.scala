import java.net.URL;
import java.util.ArrayList;
import java.util.{Set => JSet};
import java.util.{HashSet => JHashSet};
import java.util.Scanner;

object hangman {  // This is Java-like, non-idiomatic Scala code!
     var hangman: Array[String] = Array[String](
        " ======  ",
        " |/   |  ",
        " |    O  ",
        " |   -|- ",
        " |   / \\ ",
        " |       ",
        " |       ",
        " ==========================   RIP  :(");
        
    var runeberg: String = 
      "http://runeberg.org/words/ord.ortsnamn.posten";
    
    var latin1: String = "ISO-8859-1"; 
    
    def printHangman(n: Int): Unit = {
        for (i: Int <- 0 until n){
            System.out.println(hangman(i));
        }
    }
    
    def hideSecret(secret: String, 
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
    
    def foundAll(secret: String, 
                 found: JSet[Character]): Boolean = {
        var foundMissing: Boolean = false;
        var i: Int = 0;
        while (i < secret.length() && !foundMissing) {
            foundMissing = !found.contains(secret.charAt(i));
            i += 1;
        } 
        return !foundMissing;
    }
    
    def makeGuess(): Char = {
        var scan: Scanner = new Scanner(System.in);
        var guess: String = "";
        do {
           System.out.println("Gissa ett tecken: ");
           guess = scan.next();
        } while (guess.length() > 1);
        return Character.toLowerCase(guess.charAt(0));
    }

    def play(secret: String): Int = {
        var found: JSet[Character] = new JHashSet[Character]();
        var bad: Int = 0;
        while (bad < hangman.length && !foundAll(secret, found)){
            printHangman(bad);
            System.out.print("\nFelgissningar: " + bad + "\t");
            System.out.println(hideSecret(secret, found));
            var guess: Char = makeGuess();
            if (secret.indexOf(guess) >= 0) {
                found.add(guess);
            } else {
              bad += 1;
            }
        }
        if (foundAll(secret, found)) {
            System.out.println("BRA! :)");
        } else {
            System.out.println("Hängd! :(");
            printHangman(hangman.length);
        }
        System.out.println("Rätt svar: " + secret);
        return bad;           
    }

    def download(address: String,coding: String):  String = {
        var result: String = "lackalänga";
        try {   
            var url: URL = new URL(address);
            var words: ArrayList[String] = 
                new ArrayList[String]();
            var scan: Scanner = 
                new Scanner(url.openStream(), coding);
            while (scan.hasNext()) {
                words.add(scan.next());    
            }
            var rnd: Int = 
                (Math.random() * words.size()).asInstanceOf[Int];
            result = words.get(rnd);
        } catch { case e: Throwable =>  
            System.out.println("Error: " + e);
            System.out.println("Använder nödlösning.");
        }
        return result;
    }
    
    def main(args: Array[String] ): Unit = {
        var badGuesses: Int = 0;
        if (args.length > 0) {
            var rnd: 
                Int = (Math.random() * args.length).asInstanceOf[Int];
            badGuesses = play(args(rnd));
        } else {
            var secret: String = download(runeberg, latin1);
            badGuesses = play(secret);
        }
        System.out.println("Antal felgissningar: " + badGuesses);
    }
}
