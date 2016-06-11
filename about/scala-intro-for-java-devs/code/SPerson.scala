// same in (non-idiomatic) Scala

class SPerson(n: String, a: Int) {
  private var name: String = n
  private var age: Int = a
  
  def this (n: String): Unit = {
    this(n, 42)
  }

  def getName = name

  def getAge = age

  def setAge(a: Int): Unit = { 
    age = a
  }
} 
