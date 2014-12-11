import sext._


object SimpleGrammar extends util.Combinators {
  sealed trait Tree
  case class Leaf(symbol: Symbol, code: String) extends Tree
  case class Branch(symbol: Symbol, children: List[Tree]) extends Tree

  case class Nonterminal(symbol: Symbol, isComment:Boolean = false) extends RuleRHS
  case class Terminal(parse: Parser[Tree], isComment:Boolean = false) extends RuleRHS
  case class Choice(lhs: RuleRHS, rhs: RuleRHS) extends RuleRHS
  case class Sequence(lhs: RuleRHS, rhs: RuleRHS) extends RuleRHS

  sealed trait RuleRHS {
    def | (rhs: RuleRHS) = Choice(this, rhs)
    def ~ (rhs: RuleRHS) = Sequence(this, rhs)
  }

  val exp       = Nonterminal('exp, true)
  val add       = Nonterminal('add)
  val mul       = Nonterminal('mul)

  val num       = Terminal(digitsParser('num))
  val eps       = Terminal(keywordParser(""), isComment=true)
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
        exp -> ( add | mul | num ),
        add  -> ( (mul | num) ~ plus ~ exp ),
        mul  -> ( num ~ star ~ (mul | num))
      )
    )

  def parseGrammar(grammar: Grammar): String => Tree =
    code => parseNonterminal(grammar.start, grammar)(code) match {
      case Some((tree, rest)) => tree
      case None => Leaf('error, "Error")
    }

  def parseNonterminal(nonterminal: Nonterminal, grammar: Grammar): Parser[Tree] =
    parseRHS(grammar lookup nonterminal, grammar) ^^ {
      children =>
      if (nonterminal.isComment && children.size>0)
        children.head
      else
        Branch(nonterminal.symbol, children)
    }

  def parseRHS(ruleRHS: RuleRHS, grammar: Grammar): Parser[List[Tree]] =
    code =>
    ruleRHS match {
      case Terminal(parser, isComment) => (parser^^{ result => if(isComment) List() else List(result)  })(code)
      case Nonterminal(symbol, isComment) => (parseNonterminal(Nonterminal(symbol, isComment), grammar)^^{ result => List(result)})(code)
      case Sequence(first, second) => ((parseRHS(first, grammar) ~ parseRHS(second, grammar))^^{ case (first, second) => first ::: second })(code)
      case Choice(first, second) => ((parseRHS(first, grammar) | parseRHS(second, grammar))^^{result => result })(code)
    }



  def parseAE(code: String): Tree = parseGrammar(ae)(code)

  def simplifyAE(node:Tree): Tree = simplifyAEHelper(node) match {
    case Some(n)=> n
    case None => Leaf('error, node.treeString)
  }

  def simplifyAEHelper(node:Tree): Option[Tree] = node match {
    case Branch(sym,children) =>
                                if(children.size>0)
                                  Some( Branch(sym, children.flatMap(simplifyAEHelper)) )
                                else
                                  None
    case Leaf(_,_) => Some(node)
  }

  def parseAndSimplifyAE(code: String): Tree = simplifyAE(parseAE(code))

  def eval(t: Tree): Int = t match {
    case Branch('add, List(lhs, rhs)) =>
      eval(lhs) + eval(rhs)

    case Branch('mul, List(lhs, rhs)) =>
      eval(lhs) * eval(rhs)

    case Leaf('num, code) =>
      code.toInt
  }
}




/*
4.5:
The parser keeps deducing exp -> add -> exp + exp -> add + exp -> exp + ... -> and + ... -> ... until 'forever' caused by the left recursion.
We force a num first in add,mul and add a null terminal to end the recursion.

4.7:
After rewriting the removal of exp (had bad implementation in Ex3), some exp Branches without children where left.
Simplifier removes these.

4.8:
Term '2 * 3 + 4' evaluates to 14, because it evaluates - like always - from right to left. I do expect * before + and left before right.
*/
