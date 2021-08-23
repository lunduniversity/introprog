echo "*** scp pdfs to web.cs.lth.se"
scp compendium/*.pdf slides/*.pdf quickref/quickref*.pdf plan/courseplan/courseplan.pdf $LUCATID@fileadmin.cs.lth.se:/Websites/Fileadmin/pgk/.
