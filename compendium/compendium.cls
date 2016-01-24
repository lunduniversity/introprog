%https://sv.sharelatex.com/blog/2011/03/27/how-to-write-a-latex-class-file-and-design-your-own-cv.html

\NeedsTeXFormat{LaTeX2e}
\ProvidesClass{compendium}[2016/01/20 Document class lecturenotes based on book]
\LoadClass[12pt]{book}

\usepackage{geometry} % to change margins:
\geometry{hmargin={4.5cm,2.5cm}}
\geometry{vmargin={3cm,3cm}}

\usepackage[utf8]{inputenc}
\usepackage[T1]{fontenc}

%%% FONT PACKAGES
\usepackage[scaled=0.95]{beramono} % inconsolata or beramono ???
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

\usepackage{marvosym,dingbat}

\newcounter{TaskCounter}[section]
\newcommand{\Task}{%
\refstepcounter{TaskCounter}% 
\ifnum\value{TaskCounter}>1\vspace{1em}\else\vspace{0.25em}\fi%
\noindent{\bf Uppgift~\arabic{TaskCounter}.~}%
}

\newcommand\TaskComputer{\Task{\footnotesize\smallpencil}~}
\newcommand\TaskPencil{\Task{\hskip0.5mm\Large\Keyboard\hskip1.5mm}}

\usepackage{xcolor}

\usepackage{enumitem}

\linespread{1.0} 


\usepackage{lipsum} % to create dummy text with \lipsum
\usepackage{amsmath,scalerel} %http://tex.stackexchange.com/questions/125523/is-there-a-bigcdot-symbol-in-latex
\usepackage{longtable}
\usepackage{booktabs}

\usepackage[normalem]{ulem}  % for strikethrough: \sout{Hello World}

\usepackage{multicol} \setlength{\columnsep}{5mm}
\usepackage{listings}
\usepackage{xspace} %http://www.math.tamu.edu/~harold.boas/courses/math696/why-macros.html
\usepackage[pdfpagelayout=TwoPageRight]{hyperref}
%http://tex.stackexchange.com/questions/50747/options-for-appearance-of-links-in-hyperref
\hypersetup{colorlinks=true,linkcolor=, linkbordercolor=, urlbordercolor=green!70!black, urlcolor=green!70!black, pdfborderstyle={/S/U/W 1}}

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

\definecolor{mygreen}{rgb}{0,0.6,0}
\definecolor{eclipsepurple}{rgb}{0.5,0,0.25}

\newcommand{\Emph}[1]{\textcolor{blue}{\textbf{#1}}}
\newcommand{\Alert}[1]{\textcolor{red}{\textbf{#1}}}
\newcommand{\Eng}[1]{(eng. {\textit{#1}})}
\newcommand{\Key}[1]{\textcolor{eclipsepurple}{\textbf{\texttt{#1}}}}
\newcommand{\code}{\lstinline[basicstyle=\ttfamily]}


\lstset{
    language=Java, 
    basicstyle=\ttfamily\scriptsize\selectfont,
    keywordstyle=\bfseries\color{eclipsepurple},
    commentstyle=\color{mygreen},
    numberstyle={\footnotesize},
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
