public class SeqCopyForJava {

    public static int[] arrayCopy(int[] xs){
        int[] result = new int[xs.length];
        for (int i = 0; i < xs.length; i++){
            result[i] = xs[i];
        }
        return result;
    }

    public static String test(){
        int[] xs = {1, 2, 3, 4, 42};
        int[] ys = arrayCopy(xs);
        for (int i = 0; i < xs.length; i++){
            if (xs[i] != ys[i]) {
                return "FAILED!";
            }
        }
        return "OK!";
    }

    public static void main(String[] args) {
        System.out.println(test());
    }
}