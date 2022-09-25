public class JPerson {
    private String name;
    private int age = 0;
    
    public JPerson(String name){
        //namnkrock fixas med this
        this.name = name;  
    }
    
    public String getName(){
        return name;
    }
    
    public int getAge(){
        return age;
    }
    
    public void setAge(int age){
        this.age = age;   
    }
}
