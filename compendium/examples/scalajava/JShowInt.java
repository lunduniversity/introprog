import java.util.Scanner;

public class JShowInt {
    private static Scanner scan = new Scanner(System.in);
    
    public static void show(Object obj) {
        System.out.println(obj);
    }

    public static void show(Object obj, String msg) {
        System.out.println(msg + obj);
    }
    
    public static String repeatChar(char ch, int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            sb.append(ch);
        }
        return sb.toString();
    }

    public static String readLine(String prompt) {
        System.out.print(prompt);
        return scan.nextLine();
    }
    
    public static void intReport(int i) {
        int leading = Integer.numberOfLeadingZeros(i);
        String binaryString = 
            repeatChar('0', leading) + Integer.toBinaryString(i);
        show((char) i,                 "Tecken: ");
        show(binaryString,             "Binärt: ");
        show(Integer.toHexString(i),   "Hex   : ");
        show(Integer.toOctalString(i), "Oktalt: ");
    }
    
    public static void loopReport() {
        boolean hasExploded = false;
        while (!hasExploded) {
            try {
                String s = readLine("Heltal annars pang: ");
                intReport(Integer.parseInt(s));
            } catch (Throwable e){
                show(e);
                hasExploded = true;
            }
        }
        show("PANG!");
    }
    
    public static void main(String[] args){
        if (args.size == 0) {
            loopReport();
        } else {
            for (arg: args) {
                intReport(Integre.parseInt(arg))
            }
        }
    }
}
