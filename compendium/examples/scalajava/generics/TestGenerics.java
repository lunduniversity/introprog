package week10.generics;

import java.util.ArrayList;

public class TestGenerics {

    public static void main(String[] args) {
        ArrayList<String> words = new ArrayList<String>();
        words.add("hej");
        words.add("på");
        words.add("dej");
       System.out.println("Vanlig for-sats:");
        for (int i = 0; i < words.size(); i++) {
            System.out.println(i + ": " + words.get(i));
        }
        
        System.out.println("\nfor-each-sats:");
                
        for (String s: words) {
            System.out.println(s);
        }
        
        System.out.println(
                "\nUtökad for-sats fungerar även med primitiva vektorer:");
        
        String[] stringArray = {"hej", "på", "dej"};
        for (String s: stringArray){
            System.out.println(s);
        } 
    } 

}
