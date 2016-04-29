// DiceReg2.java
import java.util.Random;

public class DiceReg2 {
    public static int[] diceReg = new int[6];  
    private static Random rnd = new Random();

    public static int parseArguments(String[] args) {
        int n = 100;
        if (args.length > 0) {
            n = Integer.parseInt(args[0]);
        } 
        System.out.print("Rolling the dice " + n + " times");
        if (args.length > 1) {
            int seed = Integer.parseInt(args[1]);
            rnd.setSeed(seed);
            System.out.print(" with seed " + seed);
        } 
        System.out.println(".");
        return n;        
    }
    
    public static void registerPips(int n){
        for (int i = 0; i < n; i++) {
            int pips = rnd.nextInt(6);
            diceReg[pips]++; 
        }
    }
    
    public static void printReg() {
        for (int i = 1; i <= 6; i++) {
            System.out.println("Number of " + i + "'s: " + 
                diceReg[i-1]); 
        }
    }

    public static void main(String[] args) {
        int n = parseArguments(args);
        registerPips(n);
        printReg();
    }
}
