%https://sv.sharelatex.com/blog/2011/03/27/how-to-write-a-latex-class-file-and-design-your-own-cv.html

\NeedsTeXFormat{LaTeX2e}
\ProvidesClass{compendium}[2016/01/20 Document class lecturenotes based on book]
\LoadClass[12pt]{book}

\usepackage{geometry} % to change margins:
\geometry{hmargin={4.5cm,2.5cm}}
\geometry{vmargin={3cm,3cm}}
\usepackage{marginnote}

\usepackage[utf8]{inputenc}
\usepackage[T1]{fontenc}

%%% FONT PACKAGES
\usepackage[scaled=0.85]{beramono} % inconsolata or beramono ???
\usepackage{fouriernc} % serif: new century schoolbook
\usepackage{avant}     % sans serif: Avant Garde

%\usepackage{bera}
%\usepackage{lmodern} %latin modern
%\usepackage{tgheros} % times
%\usepackage{bookman} 
%\usepackage[sc]{mathpazo} %palatino
%\usepackage{tgheros} %helvetica
%\usepackage{libris} %Libris adf
%\usepackage[scaled]{uarial} %Arial
%\usepackage{epigrafica}
%\usepackage{PTSans}

\usepackage{microtype} % Slightly tweak font spacing for aesthetics

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

\linespread{1.0} 


\usepackage{lipsum} % to create dummy text with \lipsum
\usepackage{amsmath,scalerel} %http://tex.stackexchange.com/questions/125523/is-there-a-bigcdot-symbol-in-latex

\usepackage[linesnumbered,lined,boxed]{algorithm2e} % for writing pseudo-code

\usepackage{longtable}
\usepackage{booktabs}
\usepackage{titlesec}
\usepackage{titletoc}

\usepackage[normalem]{ulem}  % for strikethrough: \sout{Hello World}

\usepackage{multicol} \setlength{\columnsep}{5mm}
\usepackage{listings}
\usepackage{xspace} %http://www.math.tamu.edu/~harold.boas/courses/math696/why-macros.html
\usepackage[pdfpagelayout=TwoPageRight]{hyperref}
%http://tex.stackexchange.com/questions/50747/options-for-appearance-of-links-in-hyperref
\hypersetup{colorlinks=true,linkcolor=, linkbordercolor=, urlbordercolor=mylinkcolor, urlcolor=mylinkcolor, pdfborderstyle={/S/U/W 1}}

\usepackage{tikz}
\usetikzlibrary{shapes.geometric, shapes.symbols, arrows, matrix, shapes, positioning}
%https://www.sharelatex.com/blog/2013/08/29/tikz-series-pt3.html
\tikzstyle{startstop} = [rectangle, rounded corners, minimum width=3cm, minimum height=1cm,text centered, draw=black, fill=red!30]
\tikzstyle{io} = [trapezium, trapezium left angle=70, trapezium right angle=110, minimum width=1cm, minimum height=1cm, text=white, text centered, draw=black, fill=blue!50!violet]
\tikzstyle{process} = [rectangle, minimum width=3cm, minimum height=1cm, text=white, text centered, draw=black, fill=red!50!black]
\tikzstyle{decision} = [diamond, minimum width=3cm, minimum height=1cm, text centered, draw=black, fill=green!30]
\tikzstyle{arrow} = [thick,->,>=stealth]
%UML definitions
\tikzstyle{umlclass}=[rectangle, draw=black,  thick, anchor=north, text width=3cm, rectangle split, rectangle split parts = 3]
\tikzstyle{umlarrow}=[->, >=open triangle 90, thick]

\newcommand{\Emph}[1]{\textcolor{myemphcolor}{\textbf{#1}}}
\newcommand{\Alert}[1]{\textcolor{myalertcolor}{\textbf{#1}}}
\newcommand{\Eng}[1]{(eng. {\textit{#1}})}
\newcommand{\key}[1]{\textcolor{eclipsepurple}{\textbf{\texttt{#1}}}}
\newcommand{\code}{\lstinline[basicstyle=\ttfamily]}

\newcommand{\ChapterUnnum}[1]{\chapter*{#1}\addcontentsline{toc}{chapter}{#1}}
\newcommand{\SectionUnnum}[1]{\section*{#1}\addcontentsline{toc}{section}{#1}}
\newcommand{\Exercise}[1]{\newpage\section{Övning: {\tt #1}}}
\newcommand{\ExerciseSolution}[1]{\newpage\section{{\tt #1}}}
\newcommand{\Lab}[1]{\newpage\section{Laboration: {\tt #1}}}
\newcommand{\BasicTasks}{\subsection{Grunduppgifter}}
\newcommand{\ExtraTasks}{\subsection{Extrauppgifter: öva mer på grunderna}}
\newcommand{\AdvancedTasks}{\subsection{Fördjupningsuppgifter: avancerad nivå}}
\newcommand{\TaskSection}[1]{\subsubsection*{#1}}

\newcounter{TaskCounter}[section]
\newcommand{\Task}{%
\refstepcounter{TaskCounter}% 
\ifnum\value{TaskCounter}>1\vspace{1em}\else\vspace{0.25em}\fi%
\pagebreak[1]\noindent{\Emph{Uppgift~\arabic{TaskCounter}.}~}%
}
%\newcommand\TaskPen{\Task{\reversemarginpar\marginnote{\footnotesize\smallpencil}[0cm]}}
\newcommand\Pen{\reversemarginpar\marginnote{\footnotesize\Emph{\smallpencil}}[0cm]}
\newcommand\Checkpoint{\reversemarginpar\marginnote{\small\Alert{\checkmark \eye}}[0cm]}

\newcounter{SubtaskCounter}[TaskCounter]
\newcommand{\Subtask}{\refstepcounter{SubtaskCounter}% 
%\vspace{1em}%
\ifnum\value{SubtaskCounter}>1\vspace{0.25em}\else\vspace{0.5em}\fi%
{\pagebreak[0]\noindent%%
\makebox[2em][l]{\alph{SubtaskCounter})}}%
}

\renewcommand{\theSubtaskCounter}{\alph{SubtaskCounter}}

\newenvironment{Goals}%
{\subsubsection{Mål}\begin{itemize}[nosep, label={$\square$},]}%
{\end{itemize}}

\newenvironment{Preparations}%
{\subsubsection{Förberedelser}\begin{itemize}[nosep, label={$\square$},]}%
{\end{itemize}}


\lstdefinelanguage{Scala}{
  morekeywords={abstract,case,catch,class,def,%
    do,else,extends,false,final,finally,%
    for,forSome,if,implicit,import,lazy,match,%
    new,null,object,override,package,%
    private,protected,return,sealed,%
    super,this,throw,trait,true,try,%
    type,val,var,while,with,yield},
  otherkeywords={=>,<-,<\%,<:,>:,@},
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
    backgroundcolor=\color{gray!10},
    frame=single,
    rulecolor=\color{black!20},
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
{─}{{\textemdash}}1
{└}{{|}}1
{├}{{|}}1
{│}{{|}}1
}

\lstnewenvironment{ClassSpec}[2][]{%
    \lstset{basicstyle=\ttfamily\fontsize{9}{11}\selectfont,%
            backgroundcolor=,rulecolor=\color{black}, title={\texttt{\textcolor{blue}{\textbf{#2}}}}, frameround=tttt,language=,otherkeywords={},#1}%
}{}

\lstnewenvironment{Code}[1][]{%
    \lstset{#1}%
}{}

\lstnewenvironment{REPL}[1][]{%
    \lstset{
      basicstyle=\color{white}\ttfamily\fontsize{10}{12}\selectfont,      
      language=,otherkeywords={},%
      tabsize=2,
      backgroundcolor=\color{black!80},
      frame=single,
      numbers=left, numberstyle=\color{black}\ttfamily\scriptsize\selectfont
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


\newcommand{\scalainputlisting}[2][]{\lstinputlisting[%
language=Scala,%
tabsize=2,#1]{#2}}

\newcommand{\javainputlisting}[2][]{\lstinputlisting[%
language=Java,%
tabsize=4,#1]{#2}}

