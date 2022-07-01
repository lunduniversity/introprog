%https://sv.sharelatex.com/blog/2011/03/27/how-to-write-a-latex-class-file-and-design-your-own-cv.html

\NeedsTeXFormat{LaTeX2e}
\ProvidesClass{compendium}[2016/01/20 Document class lecturenotes based on book]
\LoadClass[11pt]{book}

\usepackage{geometry} % to change margins:
\geometry{hmargin={4.5cm,2.5cm}}
\geometry{vmargin={3cm,3cm}}
\usepackage{marginnote}
\usepackage{float}

\usepackage[utf8]{inputenc}
\usepackage[T1]{fontenc}

%%% FONT PACKAGES
\usepackage[scaled=0.85]{beramono} % inconsolata or beramono ???
\usepackage{fouriernc} % serif: new century schoolbook
\usepackage{avant}     % sans serif: Avant Garde

%\usepackage{bera}
%\usepackage{lmodern} %latin modern
%\usepackage{tgheros} % clone of Helvetica
%\usepackage{tgtermes}% clone of times
%\usepackage{bookman}
%\usepackage[sc]{mathpazo} %palatino
%\usepackage{tgheros} %helvetica
%\usepackage{libris} %Libris adf
%\usepackage[scaled]{uarial} %Arial
%\usepackage{epigrafica}
%\usepackage{PTSans}

\usepackage{microtype} % Slightly tweak font spacing for aesthetics
\usepackage{changepage}   % for the adjustwidth environment to make narrow paragraphs

\usepackage{sectsty} %change font on headings
\allsectionsfont{\sffamily}

\usepackage{marvosym,dingbat,amssymb}

\usepackage{xcolor}

\definecolor{mygreen}{rgb}{0,0.4,0}
\definecolor{mylinkcolor}{rgb}{0,0.1,0.5}
\definecolor{myemphcolor}{rgb}{0,0.4,0.1}
\definecolor{myalertcolor}{rgb}{0.4,0.1,0}
\definecolor{eclipsepurple}{rgb}{0.5,0,0.25}
\definecolor{eclipseblue}{rgb}{0.16,0,1.0}
\definecolor{eclipsegreen}{rgb}{0,0.5,0}

\usepackage{enumitem}

\linespread{0.98}


\usepackage{lipsum} % to create dummy text with \lipsum
\usepackage{amsmath,scalerel} %http://tex.stackexchange.com/questions/125523/is-there-a-bigcdot-symbol-in-latex

\usepackage[linesnumbered,lined,boxed]{algorithm2e} % for writing pseudo-code

\usepackage{array}
\usepackage{longtable}
\usepackage{booktabs}
\usepackage{titlesec}
\usepackage{titletoc}

% increase space due to wide numbers in table of contents:
%https://stackoverflow.com/questions/33093493/adding-space-after-section-numbers-in-toc
\makeatletter
\renewcommand{\l@section}{\@dottedtocline{1}{1.5em}{2.6em}}
\renewcommand{\l@subsection}{\@dottedtocline{2}{4.8em}{3.8em}}
\renewcommand{\l@subsubsection}{\@dottedtocline{3}{7.4em}{4.5em}}
\makeatother

\usepackage[normalem]{ulem}  % for strikethrough: \sout{Hello World}

\usepackage{hyphenat}
\hyphenation{sekvens-algoritmer}

\usepackage{multicol} \setlength{\columnsep}{5mm}
\usepackage{listings}
\usepackage{upquote} %http://tex.stackexchange.com/questions/145416/how-to-have-straight-single-quotes-in-lstlistings
\usepackage{xspace} %http://www.math.tamu.edu/~harold.boas/courses/math696/why-macros.html
\usepackage{xr} %to allow xref btw docs %https://tex.stackexchange.com/questions/248491/cross-reference-with-xr-fails-sharelatex
\usepackage[pdfpagelayout=TwoPageRight]{hyperref}
%http://tex.stackexchange.com/questions/50747/options-for-appearance-of-links-in-hyperref
\hypersetup{colorlinks=true, allcolors=mylinkcolor,
%linkcolor=, linkbordercolor=, urlbordercolor=mylinkcolor,
 urlcolor=mylinkcolor, pdfborderstyle={/S/U/W 1}}

\usepackage{pdfpages} % To include quickref booklet in appendix

\usepackage[labelfont=bf]{caption}  % to get bold faced table and figure captions
%https://tex.stackexchange.com/questions/55337/how-to-use-figure-inside-a-minipage

\usepackage{footmisc} % to reference footnotes: 
%https://tex.stackexchange.com/questions/167380/how-to-refer-to-a-footnote

\usepackage{tikz}
\usetikzlibrary{shapes.geometric, shapes.symbols, arrows, matrix, shapes, positioning, calc}
%https://www.sharelatex.com/blog/2013/08/29/tikz-series-pt3.html
\usepackage{tkz-euclide}
%\usetkzobj{all} %%% AAAARGH Version clash, broken api :(
  % Work-around: dont't use \tkzLabel \tkzMarkAngle, 
  %  prev. used in w05-classes and lect-w05 but now replaced by a hack 
%https://tex.stackexchange.com/questions/529550/latex-cant-find-file-tkz-obj-angles-tex
%https://tex.stackexchange.com/questions/96459/automatically-draw-and-labels-angles-of-a-triangle-in-tikz
% https://tex.stackexchange.com/questions/165877/latex-complains-that-tkzdrawarc-is-not-defined

\tikzstyle{startstop} = [rectangle, rounded corners, minimum width=3cm, minimum height=1cm,text centered, draw=black, fill=red!30]
\tikzstyle{io} = [trapezium, trapezium left angle=70, trapezium right angle=110, minimum width=1cm, minimum height=1cm, text=white, text centered, draw=black, fill=blue!50!violet]
\tikzstyle{process} = [rectangle, minimum width=3cm, minimum height=1cm, text=white, text centered, draw=black, fill=red!50!black]
\tikzstyle{decision} = [diamond, minimum width=3cm, minimum height=1cm, text centered, draw=black, fill=green!30]
\tikzstyle{arrow} = [thick,->,>=stealth]
%UML definitions
\tikzstyle{umlclass}=[rectangle, draw=black,  thick, anchor=north, text width=3cm, rectangle split, rectangle split parts = 3]
\tikzstyle{umlarrow}=[->, >=open triangle 90, thick]

\newcommand{\TODO}{\colorbox{red!70}{TODO!!!}~}
\newcommand{\StudyTheory}[1]{Studera begreppen i kapitel \ref{chapter:W#1}}
\newcommand{\DoExercise}[2]{Gör övning {\tt #1} i avsnitt \ref{exe:W#2}}
\newcommand{\ReadTheLab}{Läs igenom och begrunda hela laborationsinstruktionen.}

\newcommand{\Emph}[1]{\textcolor{myemphcolor}{\textbf{#1}}}
\newcommand{\Alert}[1]{\textcolor{myalertcolor}{\textbf{#1}}}
\newcommand{\Eng}[1]{(eng. {\textit{#1}})}
\newcommand{\Key}[1]{\textcolor{eclipsepurple}{\textbf{\texttt{#1}}}}
\newcommand{\code}{\lstinline[basicstyle=\ttfamily]}
\newcommand{\jcode}{\lstinline[basicstyle=\ttfamily,language=Java]}

\newcommand{\ChapterUnnum}[1]{\chapter*{#1}\addcontentsline{toc}{chapter}{#1}}
\newcommand{\SectionUnnum}[1]{\section*{#1}\addcontentsline{toc}{section}{#1}}
\newcommand{\Exercise}[1]{\newpage\section{Övning {\tt #1}}\label{section:exe:#1}}
\newcommand{\ExerciseSolution}[1]{\newpage\section{Lösning {\tt #1}}}
\newcommand{\Lab}[1]{\newpage\section{Laboration: {\tt #1}}\label{section:lab:#1}}
\newcommand{\Teamlab}[1]{\newpage\section{Grupplaboration: {\tt #1}}\label{section:lab:#1}}
\newcommand{\Assignment}[1]{\newpage\section{Projektuppgift: {\tt #1}}\label{section:proj:#1}}
\newcommand{\BasicTasks}{\subsection{Grunduppgifter; förberedelse inför laboration}}
\newcommand{\BasicTasksNoLab}{\subsection{Grunduppgifter}}
\newcommand{\ExtraTasks}{\subsection{Extrauppgifter; träna mer}}
\newcommand{\AdvancedTasks}{\subsection{Fördjupningsuppgifter; utmaningar}}
\newcommand{\TaskSection}[1]{\subsubsection*{#1}}

\newcommand\Menu[1]{\textit{#1}}
\newcommand\MenuArrow[1]{\Menu{#1}~$\rightarrow$~}
\newcommand\FramedCheckmark[1]{~\frame{\checkmark}~~\textbf{#1}}
\newcommand\FramedUnchecked[1]{$\Box$~\textbf{#1}}
\newcommand\Button[1]{\fbox{\textbf{#1}}}


\newcommand\WHAT[1]{\def\what{\emph{#1}}}


\newcounter{TaskCounter}[section]

\newcommand{\Task}{%
\refstepcounter{TaskCounter}%
\ifnum\value{TaskCounter}>1\vspace{1em}\else\vspace{0.25em}\fi%
\pagebreak[2]
\noindent{\Emph{Uppgift~\arabic{TaskCounter}.}~}%
}

\newcounter{SolutionTaskCounter}[section]

\newcommand{\TaskSolved}{%
\refstepcounter{SolutionTaskCounter}%
\ifnum\value{SolutionTaskCounter}>1\vspace{1em}\else\vspace{0.25em}\fi%
\pagebreak[2]\noindent{\Emph{Lösn. uppg.~\arabic{SolutionTaskCounter}.}~}%
}


\newcounter{SubtaskCounter}[TaskCounter]
\newcommand{\Subtask}{\refstepcounter{SubtaskCounter}%
%\vspace{1em}%
\ifnum\value{SubtaskCounter}>1\vspace{0.25em}\else\vspace{0.5em}\fi%
{\pagebreak[1]\noindent%%
\makebox[2em][l]{\alph{SubtaskCounter})}}%
}

\renewcommand{\theSubtaskCounter}{\alph{SubtaskCounter}}

\newcounter{SolutionSubtaskCounter}[SolutionTaskCounter]
\newcommand{\SubtaskSolved}{\refstepcounter{SolutionSubtaskCounter}%
%\vspace{1em}%
\ifnum\value{SolutionSubtaskCounter}>1\vspace{0.25em}\else\vspace{0.5em}\fi%
{\pagebreak[1]\noindent%%
\makebox[2em][l]{\alph{SolutionSubtaskCounter})}}%
}

\renewcommand{\theSolutionSubtaskCounter}{\alph{SolutionSubtaskCounter}}

\newcounter{ConceptCounter}[TaskCounter]
\newcommand{\ConceptCount}{\refstepcounter{ConceptCounter}\arabic{ConceptCounter}}

\newcounter{DescrCounter}[TaskCounter]
\newcommand{\DescrCount}{\refstepcounter{DescrCounter}\Alph{DescrCounter}}

\newenvironment{ConceptConnections}[1][0.5cm]%
{\begingroup\renewcommand{\arraystretch}{1.2}\vspace{1em}\raggedright\begin{tabular}{@{}l |c p{#1} c| p{0.75\textwidth}}}%
{\end{tabular}\vspace{1em}\endgroup}

\newcommand{\Connect}[2]      {#1  &  \ConceptCount  &   &   \DescrCount  &  #2 \\}
\newcommand{\ConnectSolved}[2]{#1  &        &  {\Large$\leadsto$~~} &     &  #2 \\}

%\newcommand\TaskPen{\Task{\reversemarginpar\marginnote{\footnotesize\smallpencil}[0cm]}}
\newcommand\Pen{\reversemarginpar\marginnote{\footnotesize\Emph{\smallpencil}}[0cm]}
\newcommand\Checkpoint{\reversemarginpar\marginnote{\small\Alert{\checkmark \eye}}[0cm]}
%\newcommand\Uberkurs{}
%\newcommand\Uberkurs{\reversemarginpar\marginnote{\Large\Alert{$\notin$}}[0cm]}
\newcommand\Uberkurs{\reversemarginpar\marginnote{\Large\Alert{$\star$}}[0cm]}


\newcommand{\MEM}[3]{\vspace{0.5em}
\begin{tikzpicture}[font=\ttfamily]
\matrix [matrix of nodes, row sep=0, column 2/.style={nodes={rectangle,draw,minimum width=4em}}] (mat)
{
#1: #2 \pgfmatrixnextcell \makebox(16,12){#3}\\
};
\end{tikzpicture}
%https://tex.stackexchange.com/questions/1111/problem-with-defining-shortcuts-for-tikz-matrices
}


\newenvironment{Goals}%
{\subsubsection{Mål}\begin{itemize}[nosep, label={$\square$},]}%
{\end{itemize}}

\newenvironment{Preparations}%
{\subsubsection{Förberedelser}\begin{itemize}[nosep, label={$\square$},]}%
{\end{itemize}}

\newenvironment{Background}%
{\begin{adjustwidth}{\parindent}{}\small \emph{Bakgrund:}}%
{\end{adjustwidth}}

\newcommand{\vecka}{1}%renew this command in chapter.tex files

\newcommand{\Scaladoc}{\url{http://www.scala-lang.org/api/current/}}

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%% changes to make slide stuff look good in compendium

\newcommand{\pause}{}% do nothing
%%% SLIDE SIZES with no affect in compedium
\newcommand{\SlideFontSize}[2]{}
\newcommand{\SlideFontSmall}{}
\newcommand{\SlideFontTiny}{}


%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%% lingstings specifics:

\lstdefinelanguage{Scala}{
  morekeywords={abstract,case,catch,class,def,%
    do,else,enum,export,extends,false,final,finally,%
    for,given,if,implicit,import,lazy,match,%
    new,null,object,override,package,%
    private,protected,return,sealed,%
    super,then,throw,trait,true,try,%
    type,val,var,while,with,yield,%
    as, derives, end, extension, infix, inline, opaque, open, transparent, using}, % soft keywords
  otherkeywords={=>,<-,<:,>:,@,=>>,?=>},
  sensitive=true,
  morecomment=[l]{//},
  morecomment=[n]{/*}{*/},
  morestring=[b]",
  morestring=[b]',
  morestring=[b]"""
}

\lstset{
    language=Scala,
    tabsize=2,
    basicstyle=\ttfamily\selectfont,
    keywordstyle=\bfseries\color{eclipsepurple},
    commentstyle=\color{mygreen},
    numberstyle={\footnotesize},
    numbers=none,
    backgroundcolor=\color{gray!5},
    frame=single,
    rulecolor=\color{black!25},
    %title={\footnotesize\lstname},
    breaklines=false,
    breakatwhitespace=false,
    framextopmargin=2pt,
    framexbottommargin=2pt,
    showstringspaces=false,
    columns=fullflexible,keepspaces
}

\lstset{literate=%
{Å}{{\AA}}1
{Ä}{{\"A}}1
{Ö}{{\"O}}1
{Ü}{{\"U}}1
{ß}{{\ss}}1
{ü}{{\"u}}1
{å}{{\aa}}1
{ä}{{\"a}}1
{ö}{{\"o}}1
{æ}{{\ae}}1
{ø}{{\o}}1
{Æ}{{\AE}}1
{Ø}{{\O}}1
{`}{{\`{}}}1
{─}{{\textemdash}}1
{└}{{|}}1
{├}{{|}}1
{│}{{|}}1
{♠}{{$\spadesuit$}}1
{♥}{{$\heartsuit$}}1
{♣}{{$\clubsuit$}}1
{♦}{{$\diamondsuit$}}1
}

\lstnewenvironment{JavaSpec}[2][]{%
    \lstset{basicstyle=\ttfamily\fontsize{10}{12}\selectfont,%
            backgroundcolor=\color{white},rulecolor=\color{black},title={\fontsize{9}{11}\selectfont\texttt{\textcolor{black}{\textbf{#2}}}}, frameround=tttt,language=,otherkeywords={},#1}%
}{}

%\lstnewenvironment{ScalaSpec}[2][]{%
%    \lstset{basicstyle=\ttfamily\fontsize{9}{11}\selectfont,%
%            backgroundcolor=,rulecolor=\color{black}, title={\sffamily{\textcolor{purple}{\textbf{#2}}}}, frame=single,language=Scala,tabsize=2,otherkeywords={},#1}%
%}{}

\lstnewenvironment{ScalaSpec}[2][]{%
    \lstset{basicstyle=\ttfamily\fontsize{10}{12}\selectfont,%
            backgroundcolor=\color{white},,
            title={\hskip-0.38em\colorbox{black!70}{\parbox{\dimexpr\textwidth+1\fboxsep-2\fboxrule\relax}{\fontsize{10}{12}\selectfont\color{white}{\textit{Specification} \texttt{\textbf{#2}}}}}},%
            identifierstyle=\color{black},%
            rulecolor=\color{black!60},frame=tRbL,framerule=1.2pt,%
            language=Scala,#1}%
}{}

\newcommand{\ScalaSpecInputListing}[3][]{\lstinputlisting[%
basicstyle=\ttfamily\fontsize{10}{12}\selectfont,%
            backgroundcolor=\color{white},
            title={\hskip-0.38em\colorbox{black!70}{\parbox{\dimexpr\textwidth+1\fboxsep-2\fboxrule\relax}{\fontsize{10}{12}\selectfont\color{white}{\textit{Specification} \texttt{\textbf{#2}}}}}},%
            identifierstyle=\color{black},%
            rulecolor=\color{black!60},frame=tRbL,framerule=1.2pt,%
            language=Scala,#1]{#3}}

\lstnewenvironment{Code}[1][]{%
    \lstset{language=Scala,#1}%
}{}

\lstnewenvironment{CodeSmall}[1][]{%
    \lstset{language=Scala,basicstyle=\ttfamily\fontsize{10}{12}\selectfont,#1}%
}{}

\lstnewenvironment{REPL}[1][]{%
    \lstset{
      basicstyle=\color{white}\ttfamily\fontsize{10}{12}\selectfont,
      language=,otherkeywords={},%
      tabsize=2,
      backgroundcolor=\color{black!80},
      frame=single,
      numbers=left,
      numberstyle=\color{black}\ttfamily\scriptsize\selectfont,
      #1}%
}{}

\lstnewenvironment{REPLnonum}[1][]{%
    \lstset{
      basicstyle=\color{white}\ttfamily\fontsize{10}{12}\selectfont,
      language=,otherkeywords={},%
      tabsize=2,
      backgroundcolor=\color{black!80},
      frame=single,
      #1}%
}{}


\lstnewenvironment{REPLsmall}[1][]{%
    \lstset{
      basicstyle=\color{white}\ttfamily\fontsize{9}{11}\selectfont,      
      language=,otherkeywords={},%
      tabsize=2,
      backgroundcolor=\color{black!80},
      frame=single,
      #1}%
}{}


\newcommand{\scalainputlisting}[2][]{\lstinputlisting[%
language=Scala,%
tabsize=2,#1]{#2}}

\newcommand{\javainputlisting}[2][]{\lstinputlisting[%
language=Java,%
tabsize=4,#1]{#2}}
