import sext._

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
  val plus       = Terminal(keywordParser(" + "), isComment=true)
  val star       = Terminal(keywordParser(" * "), isComment=true)


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
        add -> (exp ~ plus ~ exp),
        mul -> (exp ~ star ~ exp)
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




/*
A5:
We start searching a exp, for which we first search a add, but for add we again search for a exp and so on. So we get a infinite loop.
We can fix it by sorting our 'choose' series (esp. in exp) to terminals followed by nonterminals (esp. num | add | mul) (right recursion)
*/
