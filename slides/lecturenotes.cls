%https://sv.sharelatex.com/blog/2011/03/27/how-to-write-a-latex-class-file-and-design-your-own-cv.html

\NeedsTeXFormat{LaTeX2e}
\ProvidesClass{lecturenotes}[2015/08/14 Document class lecturenotes based on beamer]
%\LoadClass[handout]{beamer}
\LoadClass{beamer}

\beamertemplatenavigationsymbolsempty
\usetheme{Antibes} \usecolortheme{default}

\usepackage{xcolor}

\usepackage[utf8]{inputenc}
\usepackage[T1]{fontenc}
%\usepackage{bera}
\usepackage[scaled=0.95]{beramono} % inconsolata or beramono ???
\usepackage{tgheros}

\usepackage{amsmath,scalerel} %http://tex.stackexchange.com/questions/125523/is-there-a-bigcdot-symbol-in-latex
\usepackage{longtable}
\usepackage{booktabs}

\usepackage[normalem]{ulem}  % for strikethrough: \sout{Hello World}

\usepackage{multicol} \setlength{\columnsep}{5mm}
\usepackage{listings}
\usepackage{xspace} %http://www.math.tamu.edu/~harold.boas/courses/math696/why-macros.html
\usepackage{hyperref}
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

\newenvironment{Slide}[2][]
  {\begin{frame}[fragile,environment=Slide,#1]{#2}}
  {\end{frame}}
  
\newenvironment{SlideSimple}[2][]
  {\begin{frame}[fragile,plain, environment=SlideSimple,#1]{#2}}
  {\end{frame}}  

%  http://tex.stackexchange.com/questions/16654/newenvironment-newcommand-for-existing-environment-in-latex-beamer
\lstnewenvironment{ClassSpec}[2][]{%
    \lstset{basicstyle=\ttfamily\fontsize{6}{7}\selectfont,%
            backgroundcolor=,rulecolor=\color{black}, title={\texttt{\textcolor{blue}{\textbf{#2}}}}, frameround=tttt,language=,#1}%
}{}

\lstnewenvironment{Code}[1][]{%
    \lstset{basicstyle=\ttfamily\fontsize{7}{9}\selectfont,#1}%
}{}

  
\newcommand{\Subsection}[1]{\subsection{#1}\frame{\centering\huge\bfseries\textcolor{blue}{#1}}}

\newlength\slidefontsize
\newcommand{\Size}[1]{\setlength\slidefontsize{#1}\fontsize{\slidefontsize}{1.25\slidefontsize}\selectfont}  

\newcommand{\SlideImg}[2]{\frame[plain]{\frametitle{#1}
\centering\includegraphics[width=1.0\textwidth]{#2}
}}

\newcommand{\vecka}{1}

%http://tex.stackexchange.com/questions/104220/beamer-tableofcontents-does-not-display-the-correct-section-number-after-setco
\makeatletter 
\newcommand{\setnextsection}[1]{%
  \setcounter{section}{\numexpr#1-1\relax}%
  \beamer@tocsectionnumber=\numexpr#1-1\relax\space}
\makeatother


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


\newif\ifkompendium
\kompendiumfalse


