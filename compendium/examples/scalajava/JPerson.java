public class JPerson {
    private final String name;
    private final int age;
  
    public JPerson(final String name, final int age){
        this.name = name;
        this.age = age;
    }

    public JPerson(final String name){
        this(name, 0);
    }

    public String getName() {
        return name;
    }
  
    public int getAge() {
        return age;
    }

    public boolean canEqual(Object other) {
        return (other instanceof JPerson);
    }
    
    @Override public boolean equals(Object other){
        boolean result = false;
        if (other instanceof JPerson) {
            JPerson that = (JPerson) other;
            result = that.canEqual(this) && 
              this.getName() == that.getName() && 
              this.getAge()  == that.getAge();
        }
        return result;
    }
    
    @Override public int hashCode() {
      return name.hashCode() * 41 + age;
    }

    @Override public String toString() {
      return "JPerson(" + name + ", " + age + ")";
    }
}
