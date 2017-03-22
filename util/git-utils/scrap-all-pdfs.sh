git filter-branch --force --index-filter 'git rm --cached --ignore-unmatch compendium/compendium.pdf' --prune-empty --tag-name-filter cat -- --all
git filter-branch --force --index-filter 'git rm --cached --ignore-unmatch compendium/exercises.pdf' --prune-empty --tag-name-filter cat -- --all
git filter-branch --force --index-filter 'git rm --cached --ignore-unmatch compendium/solutions.pdf' --prune-empty --tag-name-filter cat -- --all
git filter-branch --force --index-filter 'git rm --cached --ignore-unmatch slides/*.pdf' --prune-empty --tag-name-filter cat -- --all
git push origin --force --all
git push origin --force --tags
git for-each-ref --format='delete %(refname)' refs/original | git update-ref --stdin
git reflog expire --expire=now --all
git gc --prune=now
