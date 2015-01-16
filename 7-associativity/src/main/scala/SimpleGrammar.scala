import sext._


object SimpleGrammar extends util.Combinators {
  sealed trait Tree
  case class Leaf(symbol: Symbol, code: String) extends Tree
  case class Branch(symbol: Symbol, children: List[Tree]) extends Tree

  case class Nonterminal(symbol: Symbol, isComment:Boolean = false) extends RuleRHS
  case class Terminal(parse: Parser[Tree], isComment:Boolean = false) extends RuleRHS
  case class Choice(lhs: RuleRHS, rhs: RuleRHS) extends RuleRHS
  case class Sequence(lhs: RuleRHS, rhs: RuleRHS) extends RuleRHS
  case class OneOrMore(exp: RuleRHS) extends RuleRHS


  sealed trait RuleRHS {
    def | (rhs: RuleRHS) = Choice(this, rhs)
    def ~ (rhs: RuleRHS) = Sequence(this, rhs)
  }

  val exp       = Nonterminal('exp, true)
  var noEquExp    = Nonterminal('noEquExp, true)
  var noIfExp    = Nonterminal('noIfExp, true)
  var dotExp    = Nonterminal('dotExp, true)
  var dashExp   = Nonterminal('dashExp, true)
  var dot       = Nonterminal('dot, true)
  var dash      = Nonterminal('dash, true)

  val add       = Nonterminal('add)
  val mul       = Nonterminal('mul)
  val sub       = Nonterminal('sub)
  val div       = Nonterminal('div)
  val equ       = Nonterminal('equ)
  val ifThenElse= Nonterminal('ifThenElse)

  val num       = Terminal(digitsParser('num))
  val plus      = Terminal(keywordParser(" + "), isComment=true)
  val star      = Terminal(keywordParser(" * "), isComment=true)
  val minus     = Terminal(keywordParser(" - "), isComment=true)
  val slash     = Terminal(keywordParser(" / "), isComment=true)
  val equality  = Terminal(keywordParser(" == "), isComment=true)
  val iff       = Terminal(keywordParser("if "), isComment=true)
  val then      = Terminal(keywordParser(" then "), isComment=true)
  val elsee     = Terminal(keywordParser(" else "), isComment=true)


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
        exp -> ( ifThenElse | equ | dash | dot | num ),
        noIfExp -> ( equ | dash | dot | num ),
        noEquExp -> ( dash | dot | num ),
        dotExp -> (mul | div | num ),
        dashExp -> (add | sub | num ),
        dot -> (mul | div),
        dash -> (add | sub),

        add  -> ( dotExp ~ plus ~ noEquExp ),
        mul  -> ( num ~ star ~ dotExp),
        sub  -> ( dotExp ~ OneOrMore(minus ~ (dot | add | num))),
        div  -> ( num ~ OneOrMore(slash ~ (mul | num))),

        equ  -> ( noEquExp ~ equality ~ noIfExp),

        ifThenElse -> (iff ~ noIfExp ~ then ~ exp ~ elsee ~ exp )
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
      if (nonterminal.isComment)
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
      case OneOrMore(parser) => ((oneOrMore(parseRHS(parser,grammar)))^^{result => result.flatten})(code)
    }



  def parseAE(code: String): Tree = parseGrammar(ae)(code)

  def simplifyAE(node:Tree): Tree = removeEmptyBranches(node) match {
    case Some(n)=> n
    case None => Leaf('error, node.treeString)
  }

  def removeEmptyBranches(node:Tree): Option[Tree] = node match {
    case Branch(sym,children) =>
                                if(children.size>2 && sym != 'ifThenElse)
                                  removeEmptyBranches(
                                    Branch(
                                      sym,
                                       Branch(sym,List(children(0), children(1))) :: children.drop(2)
                                    )
                                  )
                                else if(children.size>0)
                                  Some( Branch(sym, children.flatMap(removeEmptyBranches)) )
                                else
                                  None

    case Leaf(_,_) => Some(node)
  }


  def parseAndSimplifyAE(code: String): Tree = simplifyAE(parseAE(code))

  def parseAndEval(code: String): Int = eval(parseAndSimplifyAE(code))


  def eval(t: Tree): Int = t match {

    case Branch('sub, List(lhs, rhs)) =>
      eval(lhs) - eval(rhs)

    case Branch('div, List(lhs, rhs)) =>
      eval(lhs) / eval(rhs)

    case Branch('add, List(lhs, rhs)) =>
      eval(lhs) + eval(rhs)

    case Branch('mul, List(lhs, rhs)) =>
      eval(lhs) * eval(rhs)

    case Branch('equ, List(lhs, rhs)) =>
      if(eval(lhs) == eval(rhs)) 1 else 0

    case Branch('ifThenElse, List(pred, then, elsee)) =>
      if(eval(pred)!=0) eval(then) else  eval(elsee)

    case Leaf('num, code) =>
      code.toInt
  }
}



/*
ex2:
Failed evaluating 5 - 2 - 1 correctly at first, because it got parsed right associative,
but Subtraction is left associative. Same with division.
We dont have this problem with addition and multiplikation because they are right and left associative.

*/


