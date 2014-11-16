/** Exercise 3.2: A naive interpreter of grammars
  *
  * In this exercise, we will write a recursive descent parser
  * for all possible grammars.
  *
  * Tasks
  *
  * 1. Replace ??? by real implementations so that the test
  *    NaiveGrammarSpec succeeds.
  *
  * 2. Add more test cases in NaiveGrammarSpec.
  */

import util.Combinators

object NaiveGrammar extends Combinators {
  // Recall from 2.1 that we define the syntax tree of arithmetic
  // expressions as an algebraic datatype `Exp`. An example is
  // Add(Num(1), Num(2)).
  //
  // But now we want to write a parser for all possible grammars.
  // Since we don't know the grammar before hand, we cannot
  // define the syntax tree as a fixed datatype. Instead, we
  // use a tree data structure for syntax trees of all kinds of
  // grammars.

  sealed trait Tree
  case class Leaf(symbol: Symbol, code: String) extends Tree
  case class Branch(symbol: Symbol, children: List[Tree]) extends Tree

  // Identifier names following a single quotation mark create
  // Symbol objects:

  val s1: Symbol = 'first_symbol
  val s2: Symbol = 'anotherSymbol

  // Instead of Add(Num(1), Num(1)), we would now write

  val onePlusOne = Branch('add, List(Leaf('num, "1"), Leaf('num, "1")))

  // Recall the arithmetic expression grammar from 2.1.
  //
  //   Exp := Add | Mul | Num
  //
  //   Add := sum of Exp and Exp
  //
  //   Mul := product of Exp and Exp
  //
  //   Num := [0-9]+
  //
  // We will represent this grammar as a Scala object.
  // We call Exp, Add and Mul "nonterminals", because
  // they are composed of other expressions. A nonterminal
  // has a name.

  case class Nonterminal(symbol: Symbol) extends RuleRHS

  //   Num := [0-9]+
  //
  // We call Num a "terminal", because it does not depend on
  // other expressions. Let's save within each terminal the
  // knowledge of how to parse it.

  case class Terminal(parse: Parser[Tree]) extends RuleRHS

  //   Exp := Add | Mul | Num
  //
  // Exp is defined to be an addition, a multiplication or a
  // number. Let's represent the vertical bar by a case class
  // `Choice`.

  case class Choice(lhs: RuleRHS, rhs: RuleRHS) extends RuleRHS

  //   Add := sum of Exp and Exp
  //
  //   Mul := product of Exp and Exp
  //
  // Additions and multiplications are defined to be keywords and
  // expressions following each other. We have used the
  // combinator ~ for sequencing; let's represent ~ by a case
  // class as well.

  case class Sequence(lhs: RuleRHS, rhs: RuleRHS) extends RuleRHS

  // Things like
  //
  //   Exp := Add | Mul | Num
  //
  // are called "production rules". A grammar is a collection of
  // production rules with a starting symbol. Nonterminals,
  // terminals, choices and sequences can all appear on the right
  // hand side of a production rule, therefore they are
  // subclasses of the trait RuleRHS. We define methods | and ~
  // inside RuleRHS so that choices and sequences are easier to
  // write.

  sealed trait RuleRHS {
    def | (rhs: RuleRHS) = Choice(this, rhs)
    def ~ (rhs: RuleRHS) = Sequence(this, rhs)
  }

  // Let's define the terminals and nonterminals.

  val exp       = Nonterminal('exp)
  val add       = Nonterminal('add)
  val mul       = Nonterminal('mul)

  val num       = Terminal(digitsParser('num))
  val sumOf     = Terminal(keywordParser("sum of "))
  val productOf = Terminal(keywordParser("product of "))
  val and       = Terminal(keywordParser(" and "))

  def digitsParser(symbol: Symbol): Parser[Tree] =
    parseRegex("[0-9]+") ^^ { x => Leaf(symbol, x) }

  def keywordParser(keyword: String): Parser[Tree] =
    parseString(keyword) ^^ { x => Leaf('keyword, keyword) }

  // A grammar is a set of production rules together with a
  // starting symbol. We will use the built-in data structure
  //
  //   Map[Nonterminal, RuleRHS]
  //
  // for the collection of rules. This data structure is similar
  // to java.util.HashMap. Read more about it here:
  //
  // http://docs.scala-lang.org/overviews/collections/maps.html
  //
  // We define a method `lookup` inside the case class to look
  // up the right hand side of the production rule about a
  // nonterminal.

  case class Grammar(start: Nonterminal, rules: Map[Nonterminal, RuleRHS]) {
    def lookup(nonterminal: Nonterminal): RuleRHS = rules(nonterminal)
  }

  // Here is a Grammar object for our arithmetic expressions.
  // To create a Map object, we can write
  //
  //   Map(key1 -> value1, key2 -> value2, ...)

  val ae: Grammar =
    Grammar(
      start = exp,
      rules = Map(
        exp -> (add | mul | num),
        add -> (sumOf ~ exp ~ and ~ exp),
        mul -> (productOf ~ exp ~ and ~ exp)
      )
    )

  // Given a grammar, we want to convert a string to a syntax
  // tree described by that grammar. Given `ae`, we want to
  // produce a function that behaves just like `parse` in
  // exercise 2.1.
  //
  // It will rely on `parseNonterminal`, a generalization of
  // `parseExp`, `parseAdd`, and `parseMul` of 2.1.

  def parseGrammar(grammar: Grammar): String => Tree =
    ???

  // `parseNonterminal` generalizes `parseExp`, `parseAdd` and
  // `parseMul` from 2.1. Given a grammar and a nonterminal,
  // it produces a parser for that nonterminal in 3 steps.
  //
  // 1. Lookup the RHS of the production rule for the nonterminal
  //    in the grammar.
  //
  // 2. Call the method `parseRHS` to obtain a list of syntax
  //    trees of all parts mentioned in the production rule.
  //
  // 3. Create a syntax tree node with the nonterminal's symbol
  //    and the result of `parseRHS` as children.

  def parseNonterminal(nonterminal: Nonterminal, grammar: Grammar): Parser[Tree] =
    parseRHS(grammar lookup nonterminal, grammar) ^^ {
      children => Branch(nonterminal.symbol, children)
    }

  // `parseRHS` creates a list of syntax tree nodes for all parts
  // mentioned in the right hand side of a production rule.
  //
  // Examples:
  //
  // parseRHS(num ~ num ~ num, ae)("1 2 3") ==
  //   Some((
  //     List(Leaf('num, "1"), Leaf('num, "2"), Leaf('num, "3")),
  //     ""
  //   ))
  //
  // parseRHS(sumOf ~ exp ~ and ~ exp, ae)("sum of 1 and 1") ==
  //   Some((
  //     List(
  //       Leaf('keyword, "sum of "),           // keyword "sum of "
  //       Branch('exp, List(Leaf('num, "1"))), // left operand
  //       Leaf('keyword, " and ")    ,         // keyword " and "
  //       Branch('exp, List(Leaf('num, "1")))  // right operand
  //     ),
  //     ""
  //   ))
  //
  // val threeNums = num | (num ~ and ~ num) | (num ~ and ~ num ~ and ~ num)
  //
  // parseRHS(threeNums, ae)("1") ==
  //   Some((
  //     List(Leaf('num, "1")),
  //     ""
  //   ))
  //
  // parseRHS(threeNums, ae)("1 and 2") ==
  //   Some((
  //     List(
  //       Leaf('num, "1"),
  //       Leaf('keyword, " and "),
  //       Leaf('num, "2")
  //     ),
  //     ""
  //   ))
  //
  // parseRHS(threeNums, ae)("1 and 2 and 3") ==
  //   Some((
  //     List(
  //       Leaf('num, "1"),
  //       Leaf('keyword, " and "),
  //       Leaf('num, "2")
  //       Leaf('keyword, " and "),
  //       Leaf('num, "3")
  //     ),
  //     ""
  //   ))
  //
  // Please implement the following behavior.
  //
  // 1. If `ruleRHS` is a nonterminal or a terminal, then
  //    create a syntax tree from that and put it in a list of
  //    one element.
  //
  // 2. If `ruleRHS` is a sequence of two parts, then concatenate
  //    the list produced from the first part with the list
  //    produced from the second part.
  //
  // 3. If `ruleRHS` is a choice, then return the list
  //    corresponding to the matching case.

  def parseRHS(ruleRHS: RuleRHS, grammar: Grammar): Parser[List[Tree]] =
    ???

  // We should now be able to parse arithmetic expressions.
  //
  // assert(parseGrammar(ae)("1234") ==
  //   Branch('exp, List(Leaf('num, "1234"))))
  //
  // assert(parseGrammar(ae)("sum of 1 and 2") ==
  //   Branch('exp, List(
  //     Branch('add, List(
  //       Leaf('keyword, "sum of "),
  //       Branch('exp, List(Leaf('num, "1"))),
  //       Leaf('keyword, " and "),
  //       Branch('exp, List(Leaf('num, "2"))))))))

  // `parseGrammar(ae)` is not user-friendly because it produce
  // too complicated trees. The 'exp and 'keyword nodes bring
  // no new information; it would be good to leave them out.
  //
  // Implement `simplifyAE` so that:
  //
  //   simplifyAE(parseGrammar(ae)("1234")) == Leaf('num, "1234")
  //
  //   simplifyAE(parseGrammar(ae)("sum of 1 and 2") ==
  //     Branch('add, List(Leaf('num, "1"), Leaf('num, "2")))

  def simplifyAE(syntaxTree: Tree): Tree =
    ???

  /** parse an arithmetic expression and simplify it */
  def parseAndSimplifyAE(code: String): Tree =
    simplifyAE(parseGrammar(ae)(code))
}
