# Instructions and utilities for dod-integration

## Background

In 2026 Mattias Nordahl and Björn Regnell decided to integrate the course material of "Datorer och datoranvändning" into the introprog compendium. This includes:

* labs: https://github.com/lunduniversity/introprog-computer-intro/tree/main/lab-instructions/modules 
* lectures: https://github.com/lunduniversity/introprog-computer-intro/tree/main/lectures

The goal is to deprecate this repo: https://github.com/lunduniversity/introprog-computer-intro


## Patches to lectures

There are many special and memoir-specific latex stuff in the beamer slides from dod. The goal is to simplify and remove them and replace by plain latex things or latex envs and commands available in introprog's lecturenotes.cls

Things that needs to patched when importing lectures (manually or perhaps TODO: write an automatic patcher?):

* \ti is used for some inline sub-titles: replace with a simple \textbf

* reduce indent as we don't use globalcode?

* All these are replaced by a plain \item
  - TODO

