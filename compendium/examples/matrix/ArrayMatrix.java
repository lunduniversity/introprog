public class ArrayMatrix {

    public static void showMatrix(int[][] m){
        System.out.println("\n--- showMatrix ---");
        for (int row = 0; row < m.length; row++){
            for (int col = 0; col < m[row].length; col++) {
                System.out.print("[" + row + "]");
                System.out.print("[" + col + "] = ");
                System.out.print(m[row][col] + "; ");
            }
            System.out.println();
        }
    }
    
    public static void fillRnd(int[][] m, int n){
        for (int row = 0; row < m.length; row++){
            for (int col = 0; col < m[row].length; col++) {
              m[row][col] = (int) (java.lang.Math.random() * n + 1);
            }
        }
    }
    
    public static boolean isYatzy(int[] dice){
        int col = 1;
        boolean allSimilar = true;
        while (col < dice.length && allSimilar) {
          allSimilar = dice[0] == dice[col];
          col++;
        }
        return allSimilar;
    }
    
    public static int findFirstYatzyRow(int[][] m){
        int row = 0;
        int result = -1;
        while (row < m.length && result < 0){
            if (isYatzy(m[row])) {
              result = row;
            } else {
              row = row + 1;    
            }
        }
        return result;
    }

    public static void main(String[] args) {
        System.out.println("ArrayMatrix test");
        int[][] xss = new int[10][5];
        showMatrix(xss);
        fillRnd(xss, 6);
        showMatrix(xss);
        int[][] yss = new int[2500][5];
        fillRnd(yss, 6);
        int i = findFirstYatzyRow(yss);
        System.out.println("First Yatzy Index: " + i);
    }
}
