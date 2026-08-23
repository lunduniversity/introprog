# Animals cluster - DRAFT glossary for BR ratification (2026-06-30 night)

Used in **w10-inheritance-exercise** (33+ potential clamps, the same clean-compendium shape as vego) and the
slides **lect-w11-context** (Djur/Katt/Hund/Veterinär/Bur). Once ratified, apply with `apply-b0d.scala` exactly
like vego/cards. **Reminder (the `namn` lesson):** a unit is only clamped if its English is FULLY non-Swedish,
so EVERY Swedish identifier in a clamped unit must be in the glossary or the unit stays Swedish (no partial
leak). The animals code is **pun-heavy**, so several names need your intent before they can be covered.

## A. Unambiguous - propose to ratify as-is (low risk)
| sv | proposed en | note |
|----|-------------|------|
| `Djur` | `Animal` | base trait |
| `Ko` | `Cow` | |
| `Gris` | `Pig` | |
| `Häst` | `Horse` | |
| `Katt` | `Cat` | (lect-w11) |
| `Hund` | `Dog` | (lect-w11) |
| `Veterinär` | `Veterinarian` | (lect-w11); or `Vet`? |
| `Bur` | `Cage` | (lect-w11) |
| `väsnas` | `makeSound` | verb/action (`def väsnas: Unit`); pairs with `läte`->`sound` |
| `läte` | `sound` | noun = the animal's sound/call |
| `räknaLäte` | `countSound` | |
| `antalLäten` | `soundCount` | |
| `ärFlygkunnig` | `canFly` | boolean, `is/can`-style like vego `ärRutten`->`isRotten` |
| `ärSimkunnig` | `canSwim` | |
| `ärLiten` | `isSmall` | |
| `ärStor` | `isBig` | or `isLarge`? |

## B. Compounds (follow from A)
| sv | proposed en |
|----|-------------|
| `kattveterinär` | `catVeterinarian` |
| `djurBur` | `animalCage` |
| `kattBur` | `catCage` |

## C. PUNS / uncertain - need BR (do NOT guess; these block full coverage of their units)
| sv | guess | why it needs you |
|----|-------|------------------|
| `Fyle` | `Critter`? `Beast`? | not standard Swedish; looks like a made-up base type (a bird-ish creature?). What did you intend? |
| `Ånka` | `Duck`? | playful spelling of `Anka` (duck) with the ring - is the odd spelling deliberate (keep a pun in en?) or just `Duck`? |
| `Kråga` | `Crow`? | playful variant of `Kråka` (crow); note `krage` also = collar. `Crow`, or keep the pun? |
| `Simkråga` | `SwimCrow`? | `Sim`(swim) + `Kråga`; a swimming-crow pun. English pun, or literal? |
| `antalFlygånkor` | `flyingDuckCount`? | depends on `Ånka` |
| `antalKrax` | `cawCount`? | `Krax` = a crow's caw sound; pun |

## Apply plan once ratified
1. Add A + B (+ your C decisions) to `apply-b0d.scala` `Glossary.id`.
2. Dry-run `apply-b0d.scala compendium/modules/w10-inheritance-exercise.tex` and READ the report - confirm every
   clamped unit's English is fully English (watch for stray diacritic-free identifiers like `namn`, the way the
   slides leaked). Units containing un-ratified puns will correctly SKIP.
3. `--write`, then `pdfCompendium2` (sv must stay byte-identical) -> `autotranslate --all` -> `pdfCompendium2En`.
4. Commit. This is a clean COMPENDIUM pass (unlike slides, which need the allowlist-gate first).
