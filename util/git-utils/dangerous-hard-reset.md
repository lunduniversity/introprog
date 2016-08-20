# How to scrap your fork and replace it with upstream

## 1. Make a copy of your changed files to some other dir outside of the forked repo
This is **really important** or you will loose your work! Don't forget to backup any of the files you have changed. You can see all your changes as described here: http://stackoverflow.com/questions/1587846/how-do-i-show-the-changes-which-have-been-staged

## 2. run this in terminal
This will **kill all your changes** and replace your fork with upstream

    git remote add upstream https://github.com/lunduniversity/introprog.git
    git fetch upstream
    git checkout master
    git reset --hard upstream/master  
    git push origin master —force
    
## 3. copy back your changed files, compile/test, commit and push

   git add your-changed-file.tex
   git commit -m "informative message"
   git push
   
## 4. make a clean pull request to upstream

