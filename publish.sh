# this script assumes that you have cloned https://github.com/lunduniversity/lunduniversity.github.io repo 
# into the WEB_LOCAL_GITHUB dir

WEB_LOCAL_GITHUB=../lunduniversity.github.io
WEB_LOCAL_GITHUB_PATH=src/pgk/resources
WEB_LOCAL_GITHUB_DEST=$WEB_LOCAL_GITHUB/$WEB_LOCAL_GITHUB_PATH

WEB_REMOTE_LU=fileadmin.cs.lth.se

HERE=$(pwd)

echo "*** scp pdfs to $WEB_REMOTE_LU"
scp compendium/*.pdf slides/*.pdf quickref/quickref*.pdf plan/courseplan/courseplan.pdf $LUCATID@$WEB_REMOTE_LU:/Websites/Fileadmin/pgk/.

for f in plan/*-generated.html
do
  b=$(basename $f)
  dest=$WEB_LOCAL_GITHUB_DEST/$b
  echo "Checking changes to $f"
  echo "  vs $dest"
  if ! cmp -s $f $dest
  then
    echo "  outdated: $dest"
    echo
    echo "*** copy $b to local git and commit changes ***"
    cp -i $f $dest && \
      cd $WEB_LOCAL_GITHUB && \
        source build.sh && \
          git add $WEB_LOCAL_GITHUB_PATH/$b && \
            git status && \
              git commit -m "update $WEB_LOCAL_GITHUB_PATH/$b" && \
                git push
    cd $HERE
  else
    echo "  no changes: $f"
  fi
done

