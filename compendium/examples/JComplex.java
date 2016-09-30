public class JComplex {            // man kan ej deklarera klassparametrar i Java
    private double re;             // initialiseras i konstruktorn nedan 
    private double im;             // initialiseras i konstruktorn nedan
    public  char   imSymbol = 'i'; // publikt attribut (inte vanligt i Java) 
    
    public JComplex(double real, double imag){  // konstruktor, anropas vid new
        re = real;
        im = imag;
    }
    
    public double getRe(){  // en så kallad "getter" som ger attributvärdet, förhindra förändring av re
        return re;
    }

    public double getIm(){ return im; }  // ej bruklig formattering i Java

    public double getR(){
        return Math.hypot(re, im);
    }

    public double getFi(){ 
        return Math.atan2(re, im);
    }
    
    public JComplex add(JComplex other){
        return new JComplex(re + other.getRe(), im + other.getIm());
    }

    @Override public String toString(){
        return re + " + " + im + imSymbol;
    } 
}
