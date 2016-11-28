package week10.generics;

public class TestPitfall1 {

	public static void main(String[] args) {
		Integer i1 = new Integer("42");
		Integer i2 = new Integer("42");
		Integer i3 = new Integer("43");
		Integer i4 = i1;
		
		if (i1 == i4) System.out.println("i1 == i4");
		if (i1 != i4) System.out.println("i1 != i4");
		if (i1 == i2) System.out.println("i1 == i2");
		if (i1 != i2) System.out.println("i1 != i2");
		if (i1 == i3) System.out.println("i1 == i3");
		if (i1 != i3) System.out.println("i1 != i3");
		
		if (i1.equals(i4))  System.out.println("i1.equals(i4)");
		if (!i1.equals(i4)) System.out.println("!i1.equals(i4)");
		if (i1.equals(i2))  System.out.println("i1.equals(i2)");
		if (!i1.equals(i2)) System.out.println("!i1.equals(i2)");
		if (i1.equals(i3))  System.out.println("i1.equals(i3)");
		if (!i1.equals(i3)) System.out.println("!i1.equals(i3)");
	}

}
