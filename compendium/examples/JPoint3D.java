// Översätt:  class Point3D(val x: Int, val y: Int, val z: Int = 0)

public class JPoint3D {
  private int x;       // Attributen måste vara privata eftersom
  private int y;       //   val-attribut ej finns i Java.
  private int z;       // Typen skrivs före namnet i deklarationer.

  public JPoint3D(int x, int y, int z){// Konstruktor heter som klassen.
    this.x = x;                        // Satser måste sluta med ;
    this.y = y;                        // this behövs p.g.a. skuggning.
    this.z = z;
  }

  public JPoint3D(int x, int y){  // I stället för default-argument.
    this(x, y, 0);                // Anropa konstruktor i konstruktor.
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
}
