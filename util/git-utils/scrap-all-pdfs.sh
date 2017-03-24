git filter-branch --force --index-filter 'git rm --cached --ignore-unmatch compendium/*.pdf' --prune-empty --tag-name-filter cat -- --all
git filter-branch --force --index-filter 'git rm --cached --ignore-unmatch slides/*.pdf' --prune-empty --tag-name-filter cat -- --all
git filter-branch --force --index-filter 'git rm --cached --ignore-unmatch quickref/*.pdf' --prune-empty --tag-name-filter cat -- --all
git push origin --force --all
git push origin --force --tags
git pull
git for-each-ref --format='delete %(refname)' refs/original | git update-ref --stdin
git reflog expire --expire=now --all
git gc --prune=now --aggressive
git repack -a -d --depth=250 --window=250
