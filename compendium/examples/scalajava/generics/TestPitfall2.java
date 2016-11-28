package week10.generics;

import java.util.ArrayList;

public class TestPitfall2 {

	public static void main(String[] args) {
		ArrayList<Integer> list = new ArrayList<Integer>();
		for (int i=0; i<100; i++){
			list.add(42); 
		}
		int nbr = 99;
		int index = 0;
		list.add(nbr, index);  // WRONG PARAM ORDER! Autoboxing in action.
		for (int elem: list){
			System.out.println(elem);
		}
	}

}
