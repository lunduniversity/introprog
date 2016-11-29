public class JMutablePerson {
    private String name;
    private int age = 0;
    static final int ADULT_AGE = 18;
      
    public JMutablePerson(String name){
      this.name = name;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public int getAge(){
        return age;
    }
    
    public void setAge(int age){
        if (age >= 0) {
          this.age = age;
        } else {
          this.age = 0;
        }
    }
    
    public boolean isAdult(){
        return age >= ADULT_AGE;
    }
}
