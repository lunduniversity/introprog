# Clamp draft: w10-override "Terminologi och nyckelord vid arv" table

Biggest single prose-leak cluster (21 lines) — a 20-row `tabular{r l}` glossary at
`slides/body/lect-w10-override.tex:155-189`. It masks as ONE giant placeholder-dense unit
that qwen2.5:7b mangles (placeholder reorder/drop) -> falls back to Swedish. `--retry-fallbacks`
does NOT recover it (re-falls-back for the same structural reason). Model-independent fix:
wrap the tabular in `\ifswedish <sv table> \else <en table> \fi` (additive; SV bytes between
`\ifswedish` and `\else` stay byte-identical -> SV PDF unchanged; EN build shows the \else table).

Terms in the LEFT column: `\Emph{...}` Swedish terms -> translate; `\code|...|` are Scala
keywords -> VERBATIM. `gurka` is a Swedish example identifier (DEFERRED per guardrails) -> keep.

## Proposed \else (English) table body (verbatim LaTeX; verified against cache line 13758)
```latex
\Emph{subtype}          & a type that inherits from a supertype\\
\Emph{supertype}        & a type that is inherited by a subtype\\
\Emph{base type}        & a type that is the root of an inheritance tree\\
\Emph{abstract member}  & a member that lacks an implementation\\
\Emph{concrete member}  & a member that does not lack an implementation\\
\Emph{abstract type}    & a type that can have abstract members; cannot be instantiated\\
\Emph{concrete type}    & a type that has no abstract members; can be instantiated\\
\Emph{anonymous class}  & a nameless class created directly at instantiation \\
\code|class|            & a concrete type that \Alert{cannot have abstract members}\\
\code|abstract class|   & an abstract type that \Emph{can have abstract members}\\
\code|trait|            & is an abstract type that \Emph{can be mixed in} \\
\code|extends|          & precedes a supertype, entails inheritance of the supertype's members\\
\code|override|         & a member overrides (replaces) a member in a supertype\\
\code|protected|        & makes a member visible in subtypes of this type (cf.\ \code|private|)\\
\code|final def gurka|  & makes the member gurka final: prevents overriding\\
\code|final class|      & makes the class final: prevents further subtyping\\
\code|sealed|           & sealed trait/class: only subtypes in this code file, enables match checking\\
\code|open class|       & signals it is meant to be inherited, \code|open| required for inheritance in another code file\\
\code|super.gurka|      & refers to the supertype's member \code|gurka| (cf.\ \code|this|)\\
```

## Apply
Wrap `\begin{tabular}{r  l} ... \end{tabular}` (lines 158-180) as:
`\ifswedish` + existing SV tabular + `\else` + tabular with the English rows above + `\fi`.
Keep the `{r  l}` column spec and `\SlideFontSize{7.5}{10}` outside/shared. Rebuild w10 SV+EN
slides AND the compendium (the module is \input into it), verify 0 errors + prose-leaks drops.

## Generalizes
This table pattern (term & definition glossary) recurs; the same clamp technique applies to any
giant tabular the model can't translate as one unit. Alternative structural fix: extend
Latex.segmentMasked to split tabular ROWS (at `\\`) into per-row units so each translates alone
— higher leverage (fixes all such tables at once) but higher risk. Clamp first, segment later.
