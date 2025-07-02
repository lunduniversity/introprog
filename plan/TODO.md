# TODO

**This old TODO is abandoned. Use github issues instead.** 
Kept for archeological interest :)

## Started

* Make week plan and module plan with module contents fro each week
* Compendium
  * Theory slides and notes
  * Labs
  * Exercises
  * Tool chapters
  * Solutions to exercises
  * etc

## Pending 


* Make lecture slides
* Develop self study and training support for teaching assistants (TA) in Scala
* Interview candidates for teaching assistants (TA)
* Analyze and draw conclusions from CEQ
* Develop team activities for collaboration teams for the first week
* Distribute responsibilities among Maj/Gustav/TA
  * Dev lead
  * Test lead
* Create closed repo with lab solutions 
* Talk to Mark C. Lewis at Trinity Univ about experiences with diverse student pre knowledge
* Trigger install of tools on student computers
  * Make zip with scalaide to sysadm 
  * http://wiki.eclipse.org/FAQ_How_do_I_increase_the_heap_size_available_to_Eclipse%3F 
  * ammonite?
* Follow up schedule requests implementation by schema@lth
* Find guest lecturers for the first weeks to stimulate student identification as becoming professional software engineers

## Done

* Make response to schedule inquiry to head of teaching
* Create open repo at github
* Present course plan to course leaders for feedback
* Make budget proposal for course dev to head of department
* Create basic structure for compendium
* Create infrastructure to generate plans and compendium files
* Create latex hacks to slurp slides from lectures into compendium
* Make google-forms-invitation to teaching assistants

# Decisions 

## To Be Decided

* Decide on licensing for derivative material from previous course
* Använda MCL:s bok?
* Use Ammonite?
* CS lab lib dir structure in workspace: cslib or se.lth.cs.pgk.
* Src dir structure in workspace: src/paket eller src/main/scala/paket
* How many team labs?
* When to place the first team lab?
* Keep collaboration bonus to the exam?
* Should collaboration bonus not affect approval and only higher grades?
* Scala 2.12?
* Investigate if we can/should apply these tutorials in the course:
  * http://www.tutorialspoint.com/scala/
  * http://scala-ide.org/docs/current-user-doc/features/scaladebugger/index.html 

## Decided

* Language English or Swedish?
  * **Swedish** for the body of the compendium and for lecture slides. *RATIONALE:* All students in the current course know Swedish; native speaking improves learning. Translation can come later.
  * **English** for all other stuff in the repo and its documentation. *RATIONALE:* All contributors are assumed to know English. Prepare for translation contributions to English and other languages.

* Structure of compendium? Integrated Modules Theme{Theory+Exercises+Labs} or separate parts for labs and exercises separated from the theory?
  * **Integrated modules** *RATIONALE:*  Easier to follow each module as a trail of weeks.

* Use sbt?
  * **NO** for students on student machines; unclear how to best centralize ivy cache 
  * **YES** for the repo building process - teaching assistants use sbt to build the repo 

* Decide on licensing for new material
  * **CC BY-SA** *RATIONALE:* Copy left license increase the chance of this repo to be useful by others and create most value out of tax payers money.
 
 
* Which collection should be our favorite beginner collection? Vector || List || Array 
  * **Vector**  *RATIONALE:*  Array is mutable and == does not work as expected and generic parameters need a context bound: [T : reflect.ClassTag]. Introduce Array later as a performance optimization when we are ready to discuss the difference between reference and structural equality. List has the "strange" cons syntax, which makes more sense after pattern matching and recursion have been introduced.

```
    scala> def f[A : reflect.ClassTag](n: Int) = new Array[A](n)
    f: [A](n: Int)(implicit evidence$1: scala.reflect.ClassTag[A])Array[A]

    scala> f(42)
    res0: Array[Nothing] = Array(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null)

    scala> f[Int](42)
    res1: Array[Int] = Array(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
```

