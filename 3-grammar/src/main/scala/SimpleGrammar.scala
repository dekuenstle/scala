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

  case class Nonterminal(symbol: Symbol) extends RuleRHS
  case class Terminal(parse: Parser[Tree], isComment:Boolean = false) extends RuleRHS
  case class Choice(lhs: RuleRHS, rhs: RuleRHS) extends RuleRHS
  case class Sequence(lhs: RuleRHS, rhs: RuleRHS) extends RuleRHS

  sealed trait RuleRHS {
    def | (rhs: RuleRHS) = Choice(this, rhs)
    def ~ (rhs: RuleRHS) = Sequence(this, rhs)
  }


  val exp       = Nonterminal('exp)
  val add       = Nonterminal('add)
  val mul       = Nonterminal('mul)

  val num       = Terminal(digitsParser('num))
  val sumOf     = Terminal(keywordParser("sum of "), true)
  val productOf = Terminal(keywordParser("product of "), isComment=true)
  val and       = Terminal(keywordParser(" and "), isComment=true)


  def digitsParser(symbol: Symbol): Parser[Tree] =
    parseRegex("[0-9]+") ^^ { x => Leaf(symbol, x) }

  def keywordParser(keyword: String): Parser[Tree] =
    parseString(keyword) ^^ { x => Leaf('keyword, keyword) }


  case class Grammar(start: Nonterminal, rules: Map[Nonterminal, RuleRHS]) {
    def lookup(nonterminal: Nonterminal): RuleRHS = rules(nonterminal)
  }

  val ae: Grammar =
    Grammar(
      start = exp,
      rules = Map(
        exp -> (add | mul | num),
        add -> (sumOf ~ exp ~ and ~ exp),
        mul -> (productOf ~ exp ~ and ~ exp)
      )
    )

  def parseGrammar(grammar: Grammar): String => Tree =
    code => parseNonterminal(grammar.start, grammar)(code) match {
      case Some((tree, rest)) => tree
    }

  def parseNonterminal(nonterminal: Nonterminal, grammar: Grammar): Parser[Tree] =
    parseRHS(grammar lookup nonterminal, grammar) ^^ {
      children => children.size match {
        case 1 => children.head
        case _ => Branch(nonterminal.symbol, children)
      }
    }

  def parseRHS(ruleRHS: RuleRHS, grammar: Grammar): Parser[List[Tree]] =
    code =>
    ruleRHS match {
      case Terminal(parser, isComment) => (parser^^{ result => if(isComment) List() else List(result)  })(code)
      case Nonterminal(symbol) => (parseNonterminal(Nonterminal(symbol), grammar)^^{ result => List(result)})(code)
      case Sequence(first, second) => ((parseRHS(first, grammar) ~ parseRHS(second, grammar))^^{ case (first, second) => first ::: second })(code)
      case Choice(first, second) => ((parseRHS(first, grammar) | parseRHS(second, grammar))^^{result => result })(code)
    }



  def parseAE(code: String): Tree = parseGrammar(ae)(code)

}
