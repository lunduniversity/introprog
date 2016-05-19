# How to scrap your fork and replace it with upstream

## 1. Make a copy of your changes to some dir outside the forked repo

## 2. run this in terminal
This will **kill all your changes** and replace your fork with upstream

    git remote add upstream https://github.com/lunduniversity/introprog.git
    git fetch upstream
    git checkout master
    git reset --hard upstream/master  
    git push origin master —force
    
## 3. copy back your changes, compile/test, commit and push

   git add your-changed-file.tex
   git commit -m "informative message"
   git push
   
## 4. make a clean pull request to upstream

