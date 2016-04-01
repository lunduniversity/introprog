# lunduniversity/introprog

This is the repo of a course given by Lund University called "Introduction to Programming" using Scala and Java. The repo contains course material in Swedish and some English along with code examples and libraries used in exercises and labs.

## Contents of this repo

The main directories are:
* `compendium` with the course teaching material including lecture notes, exercises, labs, etc.
  * `modules` with lectures exercises and labs for each week
  * `generated` with output from execution of `plan/Main.scala` included in the compendium
* `slides` with lecture notes in project friendly format
* `workspace` with student workspace including lab code skeletons, examples, code libs etc. 
* `plan` with module contents and concepts per week
* `img` images used in compendium and slides
* `refs` extra readings, background material
* `teachers` information for teachers

The directories are organized in this hierarchy: 

        $ tree -d -L 2
        .
        ├── compendium
        │   ├── examples
        │   ├── generated
        │   ├── modules
        │   ├── postchapters
        │   └── prechapters
        ├── img
        │   └── kojo
        ├── plan
        │   ├── concepts
        │   ├── courseplan
        │   └── ideas
        ├── refs
        ├── slides
        │   └── body
        ├── teachers
        │   └── course-dev-2016
        └── workspace
            ├── cslib
            ├── week01
            └── week03


## How to use this repo

* Download the [compedium](https://github.com/lunduniversity/introprog/raw/master/compendium/compendium.pdf).

* Download the [workspace](zipfile to be uploaded). <- TODO ->

## How to build this repo

* Install sbt: http://www.scala-sbt.org/release/docs/Setup.html

* Clone this repo: https://help.github.com/articles/cloning-a-repository/

* run these sbt commands in a terminal window in the root directory of the clone on your local machine:
  * `sbt compile` to compile all sources 
  * `sbt eclipse` to make eclipse project files 
  * `sbt plan/run` to generate the planning files 
  * `sbt pdf` to make slides and compendium using pdflatex


## How to contribute to this repo

### Using git and GitHub

#### Getting started

* Learn the basics about git, especially the "Getting Started" and "Git Basics" sections in this book: https://git-scm.com/book/en/v2 

* Get an account at github if you don't have one already. Recommended user name if in doubt: `firstnamefamilyname` with no capital letters and no hyphens.

* Install git: https://git-scm.com/book/en/v2/Getting-Started-Installing-Git

* Make a **fork** of lunduniverity/introprog (this repo is from now on called "**upstream**") in GitHub to your own GitHub account: https://help.github.com/articles/fork-a-repo/ 

* **Clone** your fork to your local computer: https://help.github.com/articles/cloning-a-repository/ 

* Synching your fork: If you install the GitHib client (avaliable for Win and Mac but not Linux) called "GitHub desktop" https://desktop.github.com/ you can keep your fork in synch with the upstream repo by a single click in the gui. Otherwise, this is how to pull changes from upstream to your fork with git commands: https://help.github.com/articles/syncing-a-fork/ 

* Before you change locally, do `git pull` or press the synch button in the GitHub desktop gui. Then when you are ready do `git commit -am "msg"` or commit in the gui press the synch button.

* When you are ready with a change that is good enough to be incorporated in upstream, then create a pull request: https://help.github.com/articles/creating-a-pull-request/

#### Guidelines

* Write concise and informative [commit messages](http://chris.beams.io/posts/git-commit/) that explains why the commit was made. 
* Start each commit message with a direct verb, preferably one of the following:
  * `add` when you have created new stuff that was not there before
  * `update` when you have changed existing stuff
  * `fix` when you have corrected a bug or fixed a typo etc.
  * `remove` when you have removed stuff
  * `rename` when you have renamed files or other stuff without changing appearance/meaning
  * `refactor` when you have changed things structurally but not changed actual appearance/meaning
* Example of commit messages
  * `git commit -am "update exercise w03 to improve explanation"`
  * `git commit -am "add task in exercises w05 vector copy"`
* Make small commits and commit often. Try to keep commits atomic and only within one file if meaningful.
* Make sure your change compiles before committing. Do *not* push code that does not compile!


### Coding style 

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

* Make sure you have your tex editor set to UTF-8 encoding. If you get strange errors in relation to Swedish characters, this is likely due to problems relating to non-UTF-8 encodings on mac or windows. Linux usually works out-of-the-box.

* Install texlive-full to get all extra latex stuff that is needed to compile the tex code in this repo. If you don't know which tex editor to use, try textworks.

* Check out the `.cls` files in `compedium/` and `slides/` that provide many useful latex commands.

* Check out some similar, already written `.tex` document and compare with the compiled `.pdf` to see the commands and conventions we use. 

* Examples: 
  * `\begin{Code}` ... `\end{Code}` and `\scalainputlisting{examples/hello-app.scala}` is used for Scala code 
  * `\begin{Code}[language=Java]` ... `\end{Code}` and `\javainputlisting{examples/Hi.java}` is used for Java code
  * `\begin{Slide}` and `\end{Slide}` defined in `slides/lecture-notes.cls` and in `compendium/compendium.cls` is used to generate beamer slides and to generate framed text in compendium chapters together with lecture notes that appear after each slide.


 
