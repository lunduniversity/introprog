//Översätt class MutablePoint3D(var x: Int, var y: Int, var z: Int = 0)

public class JMutablePoint3D {
  private int x;  // Attributen brukar vara privata även i föränngsbara
  private int y;  //   klasser eftersom enhetlig access och syntax för
  private int z;  //   setter med tilldelning ej finns i Java.

  public JMutablePoint3D(int x, int y, int z){
    this.x = x;          // Satser måste sluta med ;
    this.y = y;          // this behövs p.g.a. skuggning.
    this.z = z;
  }

  public JMutablePoint3D(int x, int y){  // Motsv. default-argument.
    this(x, y, 0);      // Anropa konstruktor i konstruktor.
  }
  public int getX(){    // I Java brukar man ha get i namnet på getter.
    return x;           // Metoder som ej är procedurer måste ha return.
  }

  public int getY(){    // Metoder måste ha parenteser, även getters.
    return y;
  }

  public int getZ(){   // Synlighet public måste anges explicit.
    return z;
  }

  public void setX(int x){ // I Java brukar man ha set i namnet på setter.
    this.x = x;
  }

  public void setY(int y){ // Nyckelordet void liknar Unit i Scala, men
    this.y = y;            //   det finns inget äkta tomt värde som ()
  }

  public void setZ(int zz){ // Om namn ej krockar behövs inte this, men
    z = zz;                 // man brukar ha samma parameternamn som
  }                         // attributnamn i setters trots skuggning så
}                           // att man slipper hitta på nytt namn.
