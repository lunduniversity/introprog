public class JRotate {
    public static void rotateArrayOfStrings(String[] xs, int fromPos){
        String temp = xs[fromPos];
        for (int i = fromPos; i < xs.length - 1; i++) {
            xs[i] = xs[i + 1];
        }
        xs[xs.length - 1] = temp;
    }
    
    public static void main(String[] args){
        int numberOfSteps = Integer.parseInt(args[0]);
        for (int i = 1; i <= numberOfSteps; i++){
            rotateArrayOfStrings(args, 1);
        }
        for (int i = 1; i < args.length; i++){
            System.out.println(args[i] + " ");
        }
    }
}
