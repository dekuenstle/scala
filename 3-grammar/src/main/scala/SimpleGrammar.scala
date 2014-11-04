/** Exercise 3.3: A more sophisticated interpreter of grammars
  *
  * In 3.2, you implemented the method `simplifyAE` so that
  * the result of parsing the grammar `ae` is easier to use.
  * We will now make the simplicification work for all grammars.
  *
  * Tasks:
  *
  * 1. Design a data structure for grammars and implement
  *    a grammar interpreter so that users need not write
  *    a simplifying method like `simplifyAE` for every grammar
  *    they define. Instead, the parser of the grammar should
  *    always produce simplified syntax trees.
  *
  * 2. Create a grammar object. You may choose to either write
  *    something equivalent to `NaiveGrammar.ae` in 3.2, or
  *    describe arithmetic expressions with arbitrary spacing
  *    between words (ex. 2.2).
  *
  * 3. Test that your grammar interpreter works as expected.
  *
  *
  * ===================== SPOILER BEGINS =====================
  * You may want to have grammar objects contain information
  * about how to simplify their syntax trees.
  *
  * 1. Instead of `Terminal`, keywords could have their own case
  *    class (say, `Comment`), and the grammar interpreter could
  *    discard all syntax tree nodes created from them.
  *
  * 2. Instead of `Choice`, exp could be defined in terms of a
  *    new case class (say, `Select`). The grammar interpreter
  *    never creates new syntax tree nodes from `Select`.
  * ====================== SPOILER ENDS ======================
  */

object SimpleGrammar extends util.Combinators {
  sealed trait Tree
  case class Leaf(symbol: Symbol, code: String) extends Tree
  case class Branch(symbol: Symbol, children: List[Tree]) extends Tree


  /** Parsing the grammar of your choice.
    * Always produce simplified syntax trees.
    * Should not be hard-coded for arithmetic expressions.
    */
  def parseAE(code: String): Tree = ???
}
