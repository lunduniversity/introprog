# introprog

This is the repo of a course given by Lund University called "Introduction to Programming" using Scala and Java. The repo contains course material in Swedish and some English along with code examples and libraries used in exercises and labs.

## Contents of this repo

The directories are organized in this hierarchy: 

    ├── compendium
    │   ├── examples
    │   ├── generated
    │   ├── modules
    │   ├── postchapters
    │   └── prechapters
    ├── plan
    │   ├── concepts
    │   ├── courseplan
    ├── slides
    │   └── body
    └── workspace

## How to use this repo

## How to build this repo

## How to contribute to this repo

### Coding style guides

When learning how to program it is more important to write *something* and start experimenting in a playful way, than to forcefully adhere to a particular coding standard; but students should also (eventually) understand the benefits of having a coding standard. 

In this course we pragmatically follow these style guides: 

* Scala style:
  * The Scala style guide: http://docs.scala-lang.org/style/ 
  * A Scala best practice guide: https://github.com/alexandru/scala-best-practices
* Java style:
  * http://www.oracle.com/technetwork/java/codeconventions-150003.pdf

When you make contributions to code in this repo and when you review pull-requests, check that the contributions follow the above guidlines pragmatically. In particular, **lab assigments stubs** and **answers to exercises** should, if there are no special reasons not to, follow the above style guides.

Here are some other inspiring style guides that illustrate the variety in what different organisations impose:
* Scala:
  * http://twitter.github.io/effectivescala/
  * https://github.com/databricks/scala-style-guide
  * https://github.com/paypal/scala-style-guide
* Java:
  * https://google.github.io/styleguide/javaguide.html
  * https://wiki.eclipse.org/Coding_Conventions 
  * https://www.securecoding.cert.org/confluence/display/java/Java+Coding+Guidelines

### Latex guide 

* Check out the `.cls` files in `compedium/` and `slides/` that provide many useful latex commands.

* Check out already written `.tex` and compare with the compiled `.pdf` to see the conventions we use.

* Examples: 
  * `begin{Code}` ... `\end{Code}` and `\scalainputlisting{examples/hello-app.scala}` is used for Scala code 
  * `\begin{Code}[language=Java]` ... `\end{Code}` and `\javainputlisting{examples/Hi.java}` is used for Java code
  * `\begin{Slide}` and `\end{Slide}` defined in `slides/lecture-notes.cls` and in `compendium/compendium.cls` is used to generate beamer slides and to generate framed text in compendium chapters together with lecture notes that appear after each slide.


 
