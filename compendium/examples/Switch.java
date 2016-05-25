public class Switch {
    public static void main(String[] args) {
        String favorite = "selleri";
        if (args.length > 0) {
            favorite = args[0];
        }
        System.out.println("Din favoritgrönsak: " + favorite);
        char firstChar = Character.toLowerCase(favorite.charAt(0));
        System.out.print("Jag tycker ");
        switch (firstChar) {
        case 'g': 
            System.out.println("gurka är gott!");
            break;
        case 't': 
            System.out.println("tomat är gott!");
            break;
        case 'b': 
            System.out.println("brocolli är gott!");
            break;
        default:
            System.out.println(favorite + " är äckligt!");
            break;
        }
    }
}
