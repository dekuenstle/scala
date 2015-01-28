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
  val noEquExp    = Nonterminal('noEquExp, true)
  val noIfExp    = Nonterminal('noIfExp, true)
  val dotExp    = Nonterminal('dotExp, true)
  val dashExp   = Nonterminal('dashExp, true)
  val dot       = Nonterminal('dot, true)
  val dash      = Nonterminal('dash, true)

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

  def parse(code: String): Tree = parseAndSimplifyAE(code)

  def unparse(tree: Tree): String = tree match {
    case Branch('sub, List(lhs,rhs)) => unparse(lhs)+" - "+unparse(rhs)
    case Branch('add, List(lhs,rhs)) => unparse(lhs)+" + "+unparse(rhs)
    case Branch('div, List(lhs,rhs)) => unparse(lhs)+" / "+unparse(rhs)
    case Branch('mul, List(lhs,rhs)) => unparse(lhs)+" * "+unparse(rhs)
    case Branch('equ, List(lhs,rhs)) => unparse(lhs)+" == "+unparse(rhs)
    case Branch('ifThenElse, List(pred,lhs,rhs)) => "if "+unparse(pred)+" then "+unparse(lhs)+" else "+unparse(rhs)
    case Leaf(symbol, code) => code
  }

/* ************************************************************** */

  type Layout = List[(Int, String)]
  type Doc = List[Layout]

  implicit class LayoutOps(self:Layout){
    def widest:(Int, String) = self.maxBy( lineWidth(_) )
    def width:Int = lineWidth( self.widest )
    private def lineWidth( line:(Int, String) ):Int = line match { case (intendLevel, code) => (intendLevel + code.trim().length) }
  }

  def enumerateHelper(tree: Tree, intendLevel: Int, shouldIntend:Boolean): Doc = tree match {
    case Branch(sym, List(lhs,rhs)) => {
      val lhsDoc = enumerateHelper(lhs, intendLevel, true)
      val rhsDoc = enumerateHelper(rhs, if(shouldIntend) intendLevel else (intendLevel + 2) , true)

      val infix = symbolToInfix(sym)
      val beginDoc = lhsDoc.map(mergeLayout(_,infix))

      val horDoc:Doc = for{ lhsLay <- beginDoc; rhsLay <- rhsDoc}
                       yield mergeLayout(lhsLay,rhsLay)
      val vertDoc:Doc = for{ lhsLay <- beginDoc; rhsLay <- rhsDoc}
                        yield lhsLay ::: rhsLay

      horDoc ::: vertDoc
    }


    case Branch('ifThenElse, List(pred,lhs,rhs)) => {
      val predDoc = enumerateHelper(pred, intendLevel+3, false)
      val lhsDoc = enumerateHelper(lhs, intendLevel+2, false)
      val rhsDoc = enumerateHelper(rhs, intendLevel+2, false)

      val ifDoc = predDoc.map(mergeLayout((intendLevel, "if "),_))

      val horIfThenDoc = ifDoc.map(mergeLayout(_," then "))
      val horIfThenLhsDoc = for{ lhsLay <- horIfThenDoc; rhsLay <- lhsDoc}
                            yield mergeLayout(lhsLay,rhsLay)
      val horIfThenLhsElseDoc = horIfThenLhsDoc.map(mergeLayout(_," else "))
      val horDoc:Doc = for{ lhsLay <- horIfThenLhsElseDoc; rhsLay <- rhsDoc}
                   yield mergeLayout(lhsLay,rhsLay)

      val vertIfThenDoc = ifDoc.map(mergeLayout(_," then"))
      val vertIfThenLhsDoc = for{ lhsLay <- vertIfThenDoc; rhsLay <- lhsDoc}
                             yield lhsLay ::: rhsLay
      val vertIfThenLhsElseDoc = vertIfThenLhsDoc.map( _ :+ (intendLevel,"else"))
      val vertDoc:Doc = for{ lhsLay <- vertIfThenLhsElseDoc; rhsLay <- rhsDoc}
                    yield lhsLay ::: rhsLay
      horDoc ::: vertDoc
    }
    case Leaf(symbol, code) => List( List( (intendLevel, code) ) )
  }

  def symbolToInfix(sym:Symbol):String = sym match {
    case 'sub => " - "
    case 'add => " + "
    case 'div => " / "
    case 'mul => " * "
    case 'equ => " == "
  }


  def mergeLayout(lhs:Layout, rhs:Layout):Layout = (lhs.init :+ (lhs.last._1, (lhs.last._2 + rhs.head._2) )) ::: rhs.tail
  def mergeLayout(lhs:(Int, String), rhs:Layout):Layout = mergeLayout(List(lhs),rhs)
  def mergeLayout(lhs:Layout, rhs:String):Layout = mergeLayout(lhs,List((0,rhs)))



  def enumerate(tree: Tree): Doc = enumerateHelper(tree, 0, false)


  def findBestLayout(doc: Doc, lineWidth: Int): Layout = {
    val fittingLayouts = doc.filter(_.width<=lineWidth)
    if(fittingLayouts.isEmpty){
      doc.minBy( _.width )
    } else {
      fittingLayouts.minBy(_.size)
    }
  }

  def render(layout: Layout): String = layout match {
    case Nil => ""
    case (intendation, code) :: tail => " "*intendation + code.trim() + System.lineSeparator() + render( tail )
  }

  def pretty(tree: Tree, lineWidth: Int): String = {
    val doc = enumerate(tree)
    val bestLayout = findBestLayout(doc, lineWidth)
    render( bestLayout ).trim()
  }

/* ************************************************************** */

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


