package rules

import models._

// Implements the Cyclic cellular automaton.
// https://en.wikipedia.org/wiki/Cyclic_cellular_automaton
object CyclicRule extends Rule {
  def apply(m: Matrix2D, i: Int, j: Int): Int = ???