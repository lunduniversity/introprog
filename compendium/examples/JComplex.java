public class JComplex {            // man kan ej deklarera klassparametrar i Java
    private double re;             // initialiseras i konstruktorn nedan 
    private double im;             // initialiseras i konstruktorn nedan
    public  char   imSymbol = 'i'; // publikt attribut (inte vanligt i Java) 
    
    public JComplex(double real, double imag){  // konstruktor, anropas vid new
        re = real;
        im = imag;
    }
    
// en så kallad "getter" som ger attributvärdet, förhindra förändring av re
    public double getRe(){  
        return re;
    }

// ej bruklig formattering i Java, så metoder blir minst 3 rader
    public double getIm(){ return im; }  

    public double getR(){
        return Math.hypot(re, im);       // Math med stort M i Java
    }

    public double getFi(){ 
        return Math.atan2(re, im);
    }
    
// Javametodnamn får ej ha operatortecken t.ex. +, därav namnet add     
    public JComplex add(JComplex other){ 
        return new JComplex(re + other.getRe(), im + other.getIm());
    }

    @Override public String toString(){
        return re + " + " + im + imSymbol;
    } 
}
