/** Exercise 2.1: Recursive descent parser
  *
  * Tasks
  *
  * 1. Replace each occurrence of ??? by a correct implementation
  *    so that all tests except one in AESpec.scala pass.
  *
  * 2. Write an alternative parser `parse2` for the language
  *    `Exp2` described at the end of this file, so that
  *    the last test in AESpec.scala passes. Feel free to
  *    write more tests: They make your code more robust
  *    and can be helpful in the development process.
  */

trait AE {
  // Suppose we want to implement a calculator for arithmetic
  // expressions with addition and multiplication.

  // An arithmetic expression is...
  sealed trait Exp

  // a number literal, or...
  case class Num(toInt: Int) extends Exp

  // an addition, or...
  case class Add(lhs: Exp, rhs: Exp) extends Exp

  // a multiplication.
  case class Mul(lhs: Exp, rhs: Exp) extends Exp

  // the expression 6 + 6 * 6
  val e1 = Add( Num(6), Mul( Num(6), Num(6) ) )

  // evaluating an expression to an integer
  def eval(exp: Exp): Int = exp match {
    case Num(n) => n
    case Add(lhs, rhs) => eval(lhs) + eval(rhs)
    case Mul(lhs, rhs) => eval(lhs) * eval(rhs)
  }

  // Assertions in the body of an object are executed when the
  // object is initialized. It is better to put assertions in a
  // test, so that they are executed no matter whether objects of
  // type AE gets created or not.

  // assert(eval(e1) == 42)

  // 6 * (4 + 3)
  val e2 = Mul(Num(6), Add(Num(4), Num(3)))
  // assert(eval(e2) == 42)

  // Writing expressions by hand is annoying. Instead of `e1` and
  // `e2`, we'd much rather write "6 + 6 * 6" and "6 * (4 + 3)".
  // However, the usual arithmetic language is not easy for the
  // computer to understand. We have to teach it all our school
  // teachers taught us about the order of operations and
  // parentheses. Let us start with a much simpler language:
  //
  //   Add(X, Y)   as   "sum of X and Y"
  //
  //   Mul(X, Y)   as   "product of X and Y"
  //
  // In this way, we write
  //
  //   e1   as   "sum of 6 and product of 6 and 6"
  //   e2   as   "product of 6 and sum of 4 and 3"
  //
  // A "parser" converts a text in our language into an
  // expression tree.
  //
  //   def parse(code: String): Exp = ...
  //
  // The parser allows us to define e1 and e2 thus:
  //
  //   val e1 = parse("sum of 6 and product of 6 and 6")
  //   val e2 = parse("product of 6 and sum of 4 and 3")
  //
  // Our first parser is called "recursive descent". You can read
  // all about it on Wikipedia [1] and in the reference books
  // listed  on the course homepage [2].
  //
  // [1] http://en.wikipedia.org/wiki/Recursive_descent_parser
  // [2] http://yfcai.github.io/scala/
  //
  // Writing the parser amounts to teaching the computer about
  // our arithmetic language: Expressions are sums, products or
  // number literals. Numbers are series of digits. Sums are
  // written "sum of X and Y", and products are written
  // "product of X and Y". Such ideas are described succintly
  // with a grammar:
  //
  //   Exp := Add | Mul | Num
  //
  //   Num := [0-9]+
  //
  //   Add := sum of Exp and Exp
  //
  //   Mul := product of Exp and Exp
  //
  // To construct an Exp object, a recursive descent parser
  // attempts to construct a Num object first. If it fails, then
  // the parser tries to construct an Add object. If it fails
  // again, then a Mul object. We use the Scala type Option[T] to
  // express the possibility of failure. If we succeeded in
  // parsing a subexpression such as the number 6 in e1, then we
  // should also return the left-over code "+6*6", so that the
  // parent parser can continue.
  //
  // http://www.scala-lang.org/api/current/index.html#scala.Option


  // `parseNum` parses a series of digits,
  // returns None on failure, and
  // return Some( (number-literal, left-over-code) ) on success.
  //
  // `parseExp`, `parseAdd` and `parseMul` behave similarly.
  def parseNum(code: String): Option[(Num, String)] = {
    // We define a regular expression with two groups, matching a
    // series of digits followed by arbitrary code.
    //
    // See:
    // http://stackoverflow.com/questions/4636610
    val Pattern = "([0-9]+)(.*)".r
    code match {
      case Pattern(digits, rest) =>
        Some((Num(digits.toInt), rest))

      case otherwise =>
        None
    }
  }

  // To parse sums of the form "sum of X and Y",
  // we define helpers `parseSumOf` and `parseAnd`.

  // tries to match "sum of " to a string, and returns it
  // together with what comes after.
  def parseSumOf(code: String): Option[(String, String)] = {
    val sumOf = "sum of "
    if (code.startsWith(sumOf))
      Some((sumOf, code.drop(sumOf.length)))
    else
      None
  }

  // tries to match " and " to a string, and returns it together
  // with what comes after.
  def parseAnd(code: String): Option[(String, String)] = {
    val and = " and "
    if (code.startsWith(and))
      Some((and, code.drop(and.length)))
    else
      None
  }

  def parseAdd(code: String): Option[(Add, String)] =
    // first try to match "sum of "
    parseSumOf(code) match {
      // code does not begin with "sum of "; it's not a sum.
      case None => None

      // code matches "sum of "; try to parse 1st operand
      case Some((sumOf, afterSumOf)) =>
        parseExp(afterSumOf) match {
          // fails to parse first operand
          case None => None

          // succeeds to parse lhs; try to match " and "
          case Some((lhs, afterLhs)) =>
            parseAnd(afterLhs) match {
              case None => None

              // " and " matches; try to parse 2nd operand
              case Some((and, afterAnd)) =>
                parseExp(afterAnd) match {
                  case None => None

                  // succeeds to parse the entire sum expression
                  case Some((rhs, rest)) =>
                    Some((Add(lhs, rhs), rest))
                }
            }
        }
    }


  // To parse products of the form "product of X and Y",
  // we define the helper `parseProductOf` and re-use `parseAnd`.

  // tries to match "product of "
  def parseProductOf(code: String): Option[(String, String)] = ???

  def parseMul(code: String): Option[(Mul, String)] = ???


  // parse an expression by trying sums, products and numbers in
  // that order.
  def parseExp(code: String): Option[(Exp, String)] =
    // try Add
    parseAdd(code) match {
      // Add succeeded
      case Some((add, rest)) =>
        Some((add, rest))

      // Add failed; try Mul
      case None =>
        parseMul(code) match {
          // Mul succeeded
          case Some((mul, rest)) =>
            Some((mul, rest))

          // Mul failed; try Num
          case None =>
            parseNum(code) match {
              // Num succeeded
              case Some((num, rest)) =>
                Some((num, rest))

              // Num failed; there's nothing else to try
              case None =>
                None
            }
        }
    }

  // Finally we can write the `parse` method from strings to
  // arithmetic expressions. We declare victory only if
  // `parseExp` consumes all input. If some characters are
  // left over (say on "sum of 1 and 1 and 1"), then we should
  // raise an error.
  def parse(code: String): Exp = parseExp(code) match {
    case Some((exp, rest)) if rest.isEmpty =>
      exp

    case Some((exp, rest)) if rest.nonEmpty =>
      sys.error("not an expression: " + code)

    case None =>
      sys.error("not an expression: " + code)
  }

  // Task 2
  //
  // Write a parser for the following grammar
  // and test it:
  //
  //   Exp2 := Add2 | Mul2 | Num
  //
  //   Num  := [0-9]+  // just like before
  //
  //   Add2 := add Exp2 to Exp2
  //
  //   Mul2 := multiply Exp2 by Exp2
  //
  // Make sure that
  //
  //   e1 == parse2("add 6 to multiply 6 by 6")
  //   e2 == parse2("multiply 6 by add 4 to 3")

  def parse2(code: String): Exp = ???
}
