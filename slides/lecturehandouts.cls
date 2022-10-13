%https://sv.sharelatex.com/blog/2011/03/27/how-to-write-a-latex-class-file-and-design-your-own-cv.html

\NeedsTeXFormat{LaTeX2e}
\ProvidesClass{lecturenotes}[2015/08/14 Document class lecturenotes based on beamer]
%\LoadClass[handout]{beamer}
\LoadClass[handout]{beamer}
\usepackage{etex} % after adding algorithm2e paga then strange error otherwise if not this package etex is included: http://tex.stackexchange.com/questions/38607/no-room-for-a-new-dimen
\beamertemplatenavigationsymbolsempty
%https://tex.stackexchange.com/questions/432054/missing-frame-numbers-with-paloalto-theme
\setbeamertemplate{footline}[frame number] % fix for ubuntu 18.04 see link above
\setbeamercolor{page number in head/foot}{fg=gray}  % fix for ubuntu 18.04 see link above

\usetheme{Antibes} 
%\usecolortheme{default}
% Scala RED is #DC332D 
\setbeamercolor{palette primary}{bg=mygreen}
\setbeamercolor{palette secondary}{bg=mygreen!70!black}
\setbeamercolor{palette tertiary}{bg=mygreen!60!black}
\setbeamercolor{palette quaternary}{bg=mygreen!50!black}

\usepackage{xcolor}

\definecolor{mygreen}{rgb}{0,0.5,0}
\definecolor{mylinkcolor}{rgb}{0,0,0.6}
\definecolor{myemphcolor}{rgb}{0,0.5,0.1}
\definecolor{myalertcolor}{rgb}{0.7,0,0.1}
\definecolor{eclipsepurple}{rgb}{0.5,0,0.25}
\definecolor{eclipseblue}{rgb}{0.16,0,1.0}
\definecolor{eclipsegreen}{rgb}{0,0.5,0}

% \setbeamercolor{palette primary}{bg=mygreen}
% \setbeamercolor{palette secondary}{bg=mygreen!70!black}
% \setbeamercolor{palette tertiary}{bg=mygreen!60!black}
% \setbeamercolor{palette quaternary}{bg=mygreen!50!black}


\usepackage[utf8]{inputenc}
\usepackage[T1]{fontenc}
%\usepackage{bera}
\usepackage[scaled=0.95]{beramono} % inconsolata or beramono ???
\usepackage{tgheros}

\usepackage{amsmath,scalerel} %http://tex.stackexchange.com/questions/125523/is-there-a-bigcdot-symbol-in-latex
\usepackage{longtable}
\usepackage{booktabs}

\usepackage[normalem]{ulem}  % for strikethrough: \sout{Hello World}

\usepackage{hyphenat}
\hyphenation{intro-duktion}

\usepackage{multicol} \setlength{\columnsep}{5mm}
\usepackage{listings}
\usepackage{upquote} %http://tex.stackexchange.com/questions/145416/how-to-have-straight-single-quotes-in-lstlistings
\usepackage{xspace} %http://www.math.tamu.edu/~harold.boas/courses/math696/why-macros.html
\usepackage{hyperref}
%http://tex.stackexchange.com/questions/50747/options-for-appearance-of-links-in-hyperref
\hypersetup{pdfborderstyle={/S/U/W 1}, %colorlinks=true,
linkcolor=, linkbordercolor=mylinkcolor, urlbordercolor=mylinkcolor, urlcolor=mylinkcolor, }

\usepackage[%linesnumbered,%lined, %boxed
]{algorithm2e} % for writing pseudo-code

\usepackage{tikz}
\usetikzlibrary{shapes.geometric, shapes.symbols, arrows, matrix, shapes, positioning, calc}
\usepackage{tkz-euclide}
%\usetkzobj{all} %%% AAAARGH Version clash, broken api :(
  % Work-around: dont't use \tkzLabel \tkzMarkAngle, 
  %  prev. used in w05-classes and lect-w05 but now replaced by a hack 
%https://tex.stackexchange.com/questions/529550/latex-cant-find-file-tkz-obj-angles-tex

\usepackage{pgfplots}
%%%%%%%%%%%%% compatibility setting
%https://tex.stackexchange.com/questions/81899/what-does-running-in-backwards-compatibility-mode-mean-and-what-should-i-fix-t
%\pgfplotsset{compat=newest} 
%\pgfplotsset{compat=1.13} 
\pgfplotsset{compat=1.14} 


%https://www.sharelatex.com/blog/2013/08/29/tikz-series-pt3.html
\tikzstyle{startstop} = [rectangle, rounded corners, minimum width=3cm, minimum height=1cm,text centered, draw=black, fill=red!30]
\tikzstyle{io} = [trapezium, trapezium left angle=70, trapezium right angle=110, minimum width=1cm, minimum height=1cm, text=white, text centered, draw=black, fill=blue!50!violet]
\tikzstyle{process} = [rectangle, minimum width=3cm, minimum height=1cm, text=white, text centered, draw=black, fill=red!50!black]
\tikzstyle{decision} = [diamond, minimum width=3cm, minimum height=1cm, text centered, draw=black, fill=green!30]
\tikzstyle{arrow} = [thick,->,>=stealth]
%UML definitions
\tikzstyle{umlclass}=[rectangle, draw=black,  thick, anchor=north, text width=3cm, rectangle split, rectangle split parts = 3]
\tikzstyle{umlarrow}=[->, >=open triangle 90, thick]

\newenvironment{Slide}[2][]
  {\begin{frame}[fragile,environment=Slide,#1]{#2}}
  {\end{frame}}

\newcommand{\SlideExtraColor}{red!15}

\newenvironment{SlideExtra}[2][]
{\setbeamercolor{background canvas}{bg=\SlideExtraColor}\begin{frame}[fragile,environment=SlideExtra,#1]{#2}}
{\end{frame}}
  

\newenvironment{SlideSimple}[2][]
  {\begin{frame}[fragile,plain, environment=SlideSimple,#1]{#2}}
  {\end{frame}}

%\usepackage[framemethod=default]{mdframed}
%\mdfdefinestyle{exampledefault}{%
%rightline=true,innerleftmargin=10,innerrightmargin=10,
%frametitlerule=true,frametitlerulecolor=green,
%frametitlebackgroundcolor=yellow,
%frametitlerulewidth=2pt}
% USAGE: \begin{mdframed}[style=exampledefault,frametitle={My Title}]

\newcommand{\Subsection}[1]{\subsection{#1}\frame{\centering\huge\bfseries\textcolor{blue}{#1}}}

\newlength\slidefontsize
\newcommand{\Size}[1]{\setlength\slidefontsize{#1}\fontsize{\slidefontsize}{1.25\slidefontsize}\selectfont}

\newcommand{\SlideImg}[2]{\frame[plain]{\frametitle{#1}
\centering\includegraphics[width=1.0\textwidth]{#2}
}}

\newcommand{\SlideOnly}[1]{#1}

\newcommand{\vecka}{1}

%%% SLIDE SIZES with no affect in compedium
\newcommand{\SlideFontSize}[2]{\fontsize{#1}{#2}\selectfont}
\newcommand{\SlideFontSmall}{\SlideFontSize{9}{11}}
\newcommand{\SlideFontTiny}{\SlideFontSize{8}{10}}

%http://tex.stackexchange.com/questions/104220/beamer-tableofcontents-does-not-display-the-correct-section-number-after-setco
\makeatletter
\newcommand{\setnextsection}[1]{%
  \setcounter{section}{\numexpr#1-1\relax}%
  \beamer@tocsectionnumber=\numexpr#1-1\relax\space}
\makeatother


\newcommand{\TODO}{\colorbox{red!70}{TODO!!!}~}

\newcommand{\Emph}[1]{\textcolor{myemphcolor}{\textbf{#1}}}
\newcommand{\Alert}[1]{\textcolor{myalertcolor}{\textbf{#1}}}
\newcommand{\Eng}[1]{(eng. {\textit{#1}})}
\newcommand{\Key}[1]{\textcolor{eclipsepurple}{\textbf{\texttt{#1}}}}
\newcommand{\code}{\lstinline[basicstyle=\ttfamily]}
\newcommand{\jcode}{\lstinline[basicstyle=\ttfamily,language=Java]}

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
    basicstyle=\ttfamily\scriptsize\selectfont,
    keywordstyle=\bfseries\color{eclipsepurple},
    commentstyle=\color{eclipsegreen},
    numberstyle={\footnotesize},
    stringstyle=\color{eclipseblue},
    numbers=none,
    backgroundcolor=\color{gray!10},
    frame=single,
    tabsize=4,
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
%swedish and german letters
{Å}{{\AA}}1
{Ä}{{\"A}}1
{Ö}{{\"O}}1
{Ü}{{\"U}}1
{ß}{{\ss}}1
{ü}{{\"u}}1
{å}{{\aa}}1
{ä}{{\"a}}1
{ö}{{\"o}}1
%danish letters
{æ}{{\ae}}1
{ø}{{\o}}1
{Æ}{{\AE}}1
{Ø}{{\O}}1
}

%http://tex.stackexchange.com/questions/203105/short-section-name-in-toc
%\usepackage{etoolbox}
%\makeatletter
% Insert [short title] for \section in ToC
%\patchcmd{\beamer@section}{{#2}{\the\c@page}}{{#1}{\the\c@page}}{}{}
% Insert [short title] for \section in Navigation
%\patchcmd{\beamer@section}{{\the\c@section}{\secname}}{{\the\c@section}{#1}}{}{}
% Insert [short title] for \subsection in ToC
%\patchcmd{\beamer@subsection}{{#2}{\the\c@page}}{{#1}{\the\c@page}}{}{}
% Insert [short title] for \subsection  in Navigation
%\patchcmd{\beamer@subsection}{{\the\c@subsection}{#2}}{{\the\c@subsection}{#1}}{}{}
%\makeatother


%  http://tex.stackexchange.com/questions/16654/newenvironment-newcommand-for-existing-environment-in-latex-beamer
\lstnewenvironment{JavaSpec}[2][]{%
    \lstset{basicstyle=\ttfamily\fontsize{6}{7}\selectfont,%
            backgroundcolor=,rulecolor=\color{black}, title={\texttt{\textcolor{blue}{\textbf{#2}}}}, frameround=tttt,language=,otherkeywords={},#1}%
}{}

\lstnewenvironment{ScalaSpec}[2][]{%
    \lstset{basicstyle=\ttfamily\fontsize{6}{7}\selectfont,%
            backgroundcolor=,,%\parbox{\dimexpr\textwidth-2\fboxsep-2\fboxrule\relax}{#1#2\ttfamily#3}
            title={\hskip-0.38em\colorbox{black!70}{\parbox{\dimexpr\textwidth+1\fboxsep-2\fboxrule\relax}{\fontsize{7}{8}\selectfont\color{white}{\textit{Specification} \textbf{#2}}}}},%
            identifierstyle=\color{black!80},%
            rulecolor=\color{black!60},frame=tRbL,framerule=1.2pt,%
            language=Scala,#1}%
}{}


\lstnewenvironment{Code}[1][]{%
    \lstset{basicstyle=\ttfamily\fontsize{7}{9}\selectfont,#1}%
}{}

\lstnewenvironment{CodeSmall}[1][]{%
    \lstset{basicstyle=\ttfamily\fontsize{6}{7}\selectfont,#1}%
}{}

\lstnewenvironment{REPL}[1][]{%
    \lstset{
      numberstyle=\color{black!80}\ttfamily\fontsize{6}{8}\selectfont,
      numbers=left,
      basicstyle=\color{white}\ttfamily\fontsize{7}{9}\selectfont,
      language=,otherkeywords={},%
      tabsize=2,
      backgroundcolor=\color{black!80},
      frame=single,
      #1}%
}{}


\lstnewenvironment{REPLnonum}[1][]{%
    \lstset{
      basicstyle=\color{white}\ttfamily\fontsize{9}{11}\selectfont,
      language=,otherkeywords={},%
      tabsize=2,
      backgroundcolor=\color{black!80},
      frame=single,
      #1}%
}{}

\lstnewenvironment{REPLsmall}[1][]{%
    \lstset{
      basicstyle=\color{white}\ttfamily\fontsize{6}{8}\selectfont,      
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

\newif\ifkompendium
\kompendiumfalse
