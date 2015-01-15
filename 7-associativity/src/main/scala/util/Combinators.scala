/** Exercise 3.1: Export 2.2
  *
  * Tasks
  *
  * 1. Adapt your parser combinators from exercise 2.2
  *    and put them here.
  *
  * 2. [Optional] Read `parseRegex` and learn about variadic
  *    pattern matching.
  */


package util

trait Combinators {
  type Parser[A] = String => Option[(A, String)]

  implicit class ParserOps[A](self: => Parser[A]) {
    def | (that: => Parser[A]): Parser[A] =
      input => self(input) match {
      case Some(success) =>
        Some(success)

      case None =>
        that(input)
    }

    def ~ [B] (that: => Parser[B]): Parser[(A, B)] =
      input => self(input) match {
      case Some((firstResult, afterFirstPart)) =>
        that(afterFirstPart) match {
          case Some((secondResult, afterSecondPart)) =>
            Some( ((firstResult, secondResult), afterSecondPart) )
          case None =>
            None
        }

      case None =>
        None
    }

    def ^^ [B] (postprocess: A => B): Parser[B] =
      input => self(input) match {
      case Some( (result, rest) ) =>
        Some( (postprocess(result), rest) )
      case None =>
        None
    }
  }

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
    input => parser(input) match {
      // parse failed; return empty none
      case None =>
        None

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


  /** @param regex: a Java regular expression
    * @return a parser for all strings matched by regex
    *
    * For example, we may reimplement `parseNum` from 2.1
    * thus:
    *
    *   val parseNum = parseRegex("[0-9]+") ^^ {
    *     case digits => Num(digits.toInt)
    *   }
    */
  def parseRegex(regex: String): Parser[String] = code => {
    // construct a new regular expression for all strings
    // that match `regex` at the start
    val Pattern = s"($regex)(.*)".r

    code match {
      // If there are 4 groups in the regular expression
      // `Pattern`, then we can write
      //
      //   case Pattern(x1, x2, x3, x4) => ...
      //
      // to extract the 4 groups. However, since part
      // of the regular expression `Pattern` is given by
      // the user, we don't know exactly how many groups
      // there will be. We can write
      //
      //   case Pattern(groups @ _*) => ...
      //
      // instead, so that `groups` stands for the sequence
      // of all matched groups in the regular expression
      // `pattern`:
      //
      //   groups == Seq(x1, x2, x3, x4)
      //
      // The way we construct `Pattern` makes sure that the
      // first group always matches `regex` as a whole,
      // and the last group always matches the rest of the
      // string.
      //
      // Learn more about the language feature @ _* here:
      //
      //   http://stackoverflow.com/a/9229677

      case Pattern(groups @ _*) =>
        Some((groups.head, groups.last))

      case otherwise =>
        None
    }
  }

  def parseString(expected: String): Parser[String] = code => {
    if (code startsWith expected)
      Some((expected, code drop expected.length))
    else
      None
  }
}
