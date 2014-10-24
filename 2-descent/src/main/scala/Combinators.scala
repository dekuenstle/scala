/** Exercise 2.2: Recursive descent parser with combinators
  *
  * You may have noticed that Exercise 2.1 has a lot of
  * duplicate code. We should avoid duplicating code as much as
  * possible, because copied code contains copies of all the bugs
  * in the original code, making them harder to fix.
  *
  * In this exercise, we will address the modularity problem, as
  * well as make the parser so succinct that its implementation
  * is hardly more verbose than the grammar specifying it. We
  * will learn the following Scala features:
  *
  * - First-class functions
  * - Call-by-name parameters
  * - Open classes
  *
  *
  * Tasks
  *
  * 1. Uncomment the test CombinatorsSpec.scala after
  *    completing Exercise 2.1.
  *
  * 2. Replace enough occurrences of ??? by correct
  *    implementations so that all tests in
  *    CombinatorsSpec.scala pass. Beware that the test
  *    sheet contains ??? as well.
  *
  * 3. [Optional] Read about Scala's Option type:
  *
  *    http://www.scala-lang.org/api/current/index.html#scala.Option
  *
  *    Rewrite the combinators `choice`, `sequence` and
  *    `zeroOrMore` in terms of the methods `getOrElse` and `map`
  *    of the class `Option`.
  *
  * 4. Implement the combinator `oneOrMore` and write tests for
  *    it in CombinatorsSpec.scala.
  *
  * 5. Write a new parser `parse3` that works just like the method
  *    `parse` defined in AE.scala of Exercise 2.1, except that
  *    it accepts one or more whitespace characters before and
  *    after each word. For example:
  *
  *    parse3(" sum    of     1   and 1 ") == Add(Num(1), Num(1))
  *
  *    Write tests for it in CombinatorsSpec.scala.
  */

trait Combinators extends AE {

  /* In Exercise 2.1 (AE.scala), the methods `parseExp`,
   * `parseNum` etc. have similar types. Their arguments are
   * string. Their return a possible result together with the
   * rest of the string. Let's define a "type synonym" for such
   * functions and call it `Parser`. The type variable `A` stands
   * for the type of the result.
   */

  type Parser[A] =
    String => Option[(A, String)]

  /* identity function on integers */
  type FunctionBetweenInts =
    Int => Int

  /* Functions are objects. To define an anonymous function, we
   * write a parameter list followed by the return value.
   */
  val succ: FunctionBetweenInts =
    n => n + 1

  /* A two-argument function with local variable `result` */
  val times: (Int, Int) => Int =
    (base, exponent) => {
      val result = base * exponent
      result
    }

  /* To define a function object, its argument type must be known. */
  // val succ2 = n => n + 1 // does not work
  val succ2 = (n: Int) => n + 1 // works

  /* More about anonymous functions:
   *
   * docs.scala-lang.org/tutorials/tour/anonymous-function-syntax.html
   */

  /* Since Scala functions are objects, we can write functions
   * that take functions as arguments and return functions as
   * result values. Our first example abstracts over the idea of
   * trying parsers one after the other. Let's call it the
   * `choice` combinator, because it combines two parsers into
   * one.
   *
   * Note that the parameter types have an arrow before them; we
   * write `firstTry: => Parser`. These are "call-by-name"
   * parameters. Doing the function call
   *
   *   choice(parser1, parser2)
   *
   * does not evaluate the arguments `parser1` and `parser2`;
   * they are evaluated only when they are used. There is no
   * caching, however, so we will compute the argument twice if
   * it is used twice during an execution.
   *
   * We always use call-by-name parameters in parser combinators,
   * because parsers are often recursive. For example, `parseExp`
   * calls `parseAdd`, which calls `parseExp` again. If parser
   * combinators evaluate their arguments, then recursive parsers
   * will call themselves indefinitely, eventually causing
   * StackOverflowError.
   */
  def choice[A](firstTry: => Parser[A], secondTry: => Parser[A]): Parser[A] =
    input => firstTry(input) match {
      case Some(success) =>
        Some(success)

      case None =>
        secondTry(input)
    }

  /* The `choice` combinator makes `parseExp` very easy to write.
   * Note that we define parsers as methods with the `def`
   * keyword. This is to avoid StackOverflowError in recursive
   * parsers, too. Methods are like call-by-name values: They
   * are evaluated when they are used, not when they are defined.
   */
  def parseExp3: Parser[Exp] = choice(choice(parseAdd, parseMul), parseNum)

  /* The second combinator `sequence` abstracts over the idea of
   * chaining several parsers together, each working on what's
   * left from the previous one.
   */
  def sequence[A, B](parseFirstPart: => Parser[A], parseSecondPart: => Parser[B]): Parser[(A, B)] =
    input => parseFirstPart(input) match {
      case Some((firstResult, afterFirstPart)) =>
        parseSecondPart(afterFirstPart) match {
          case Some((secondResult, afterSecondPart)) =>
            Some( ((firstResult, secondResult), afterSecondPart) )

          case None =>
            None
        }

      case None =>
        None
    }

  /* If I chain a parser producing A together with a parser
   * producing B, then I get a parser producing a pair (A, B). If
   * I chain another parser producing C after that, then the parse
   * result becomes nested tuples ((A, B), C). Products and sums
   * are the result of chaining 4 parsers together.
   */
  def parseMul3: Parser[Mul] =
    input => {

      val sequencedParser =
        sequence(sequence(sequence(
          parseProductOf,
          parseExp),
          parseAnd),
          parseExp
        )

      sequencedParser(input) match {

        case Some( ((((productOf, lhs), and), rhs), rest) ) =>
          Some( (Mul(lhs, rhs), rest) )

        case None =>
          None
      }
    }

  /* `parseMul3` is quite a bit shorter than `parseMul`. We can
   * make it even shorter by abstracting over the idea of post-
   * processing the result only if the parse succeeds, and do
   * nothing if it fails.
   */
  def postprocess[A, B](parser: => Parser[A])(postprocessor: A => B): Parser[B] =
    input => parser(input) match {
      case Some( (result, rest) ) =>
        Some( (postprocessor(result), rest) )

      case None =>
        None
    }

  def parseMul4: Parser[Mul] = {

    val sequencedParser =
      sequence(sequence(sequence(
        parseProductOf,
        parseExp),
        parseAnd),
        parseExp)

    postprocess(sequencedParser) {
      case (((productOf, lhs), and), rhs) =>
        Mul(lhs, rhs)
    }
  }

  /* There is one final improvement. Nested function calls are not
   * as convenient as operators; we would rather write
   *
   *   1 - 2 - 3 - 4
   *
   * than
   *
   *   subtract(subtract(subtract(1, 2), 3), 4)
   *
   * Scala operators are simply method calls:
   *
   *   1 - 2 - 3 - 4 == 1.-(2).-(3).-(4)
   *
   * We turn parser combinators `choice`, `sequence` and
   * `postprocess` into binary operators by adding methods to the
   * class of functions.
   */
  implicit class ParserOps[A](self: => Parser[A]) {
    /* Add the method | to parsers.
     *
     *   parser1 | parser2 == parser1.|(parser2)
     *                     == choice(parser1, parser2)
     */
    def | (that: => Parser[A]): Parser[A] =
      choice(self, that)

    /* Sequencing */
    def ~ [B] (that: => Parser[B]): Parser[(A, B)] =
      ???

    /* Post-processing */
    def ^^ [B] (postprocessor: A => B): Parser[B] =
      ???

    /* Sequence two parsers, ignore the second parser's result */
    def <~ [B] (that: => Parser[B]): Parser[A] =
      ???

    /* Sequence two parsers, ignore the first parser's result */
    def ~> [B] (that: => Parser[B]): Parser[B] =
      ???
  }

  /* The operators |, ~, ^^, <~, ~> make the parser's definition
   * almost as short as the grammar specifying it.
   */
  def exp: Parser[Exp] = add | mul | num

  def add: Parser[Exp] =
    (sumOf ~> exp <~ and) ~ exp ^^ {
      case (lhs, rhs) => Add(lhs, rhs)
    }

  def mul: Parser[Exp] =
    (productOf ~> exp <~ and) ~ exp ^^ {
      case (lhs, rhs) => Mul(lhs, rhs)
    }

  def productOf: Parser[String] = parseProductOf

  def sumOf: Parser[String] = parseSumOf

  def and: Parser[String] = parseAnd

  def num: Parser[Exp] = parseNum

  /* Scala has a parser combinator library containing |, ~, ^^,
   * <~, ~> and more.
   *
   * Google "Scala parser combinator" for tutorials and examples.
   *
   * Installation:
   * https://github.com/scala/scala-parser-combinators
   */


  /* Task 2.2.4 demands a parser that allows many space
   * characters between words. To parse many space characters, we
   * can build a parser for a single space character and chain
   * copies of it one after the other until all space characters
   * are consumed. Let's capture the idea of chaining 0 or more
   * copies of the same parser in a combinator.
   *
   * Theoreticians call `zeroOrMore` the Kleene star:
   * http://en.wikipedia.org/wiki/Kleene_star
   *
   * Read about Scala's built-in linked lists:
   * http://www.scala-lang.org/api/current/index.html#scala.collection.immutable.List
   */
  def zeroOrMore[A](parser: => Parser[A]): Parser[List[A]] =
    input => parser(input) match {
      // parse failed; return empty list
      case None =>
        Some((List.empty, input))

      // parse succeeds; put the first result at the head of the
      // list, and work on the rest of the input in the same
      // manner.
      case Some((firstResult, afterFirstResult)) =>
        zeroOrMore(parser)(afterFirstResult) match {
          case Some((otherResults, afterOtherResults)) =>
            Some((firstResult :: otherResults, afterOtherResults))

          case None =>
            None
        }
    }

  /* This combinator chains copies of a parser together just like
   * `zeroOrMore`, except it requires the parser to succeed at
   * least once. In other words, `oneOrMore(parser)` should never
   * produce an empty list as result.
   */
  def oneOrMore[A](parser: => Parser[A]): Parser[List[A]] =
    ???

  /* Parse arithmetic expressions, allowing multiple whitespace
   * characters between words. For example:
   *
   * parse3(" sum    of     1   and 1 ") == Add(Num(1), Num(1))
   */
  def parse3(code: String): Exp = ???
}
