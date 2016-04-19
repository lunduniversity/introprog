// this is Java

public class JPerson { 
    private String name;
    private int age;

    public JPerson(String n, int a) {
      name = n;
      age = a;
    }

    public JPerson(String n) {
      name = n;
      age = 42;
    }

    public String getName() {
      return name;
    }

    public int getAge() {
      return age;
    }

    public void setAge(int a) {
      age = a;
    }
} 
