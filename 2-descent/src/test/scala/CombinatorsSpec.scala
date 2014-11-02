/** Tests for Exercise 2.2
  *
  * Uncomment `CombinatorsSpec` after Exercise 2.1 is done.
  *
  * Beware: This file contains occurrences of ???. If those are
  * not replaced, then tests will fail.
  */

import org.scalatest._

class CombinatorsSpec extends FlatSpec with Combinators {

  /* Recall that by writing combinators taking functions as
   * arguments, we avoid code duplication in parsers. We can
   * avoid code duplication in tests in the same way. This is a
   * method that checks whether any given parser agrees with the
   * first parser of Exercise 2.1.
   */
  def testExp(parseExp: Parser[Exp]): Unit = {
    val e1code = "sum of 6 and product of 6 and 6"
    val e2code = "product of 6 and sum of 4 and 3"
    assert(parseExp3(e1code) == Some((e1, "")))
    assert(parseExp3(e2code) == Some((e2, "")))
  }

  "The `choice` combinator" should "work for `parseExp3`" in testExp(parseExp3)

  /* Check whether any given parser agrees with the first parser
   * of products in Exercise 2.1.
   */
  def testMul(parseMul: Parser[Exp]): Unit = {
    val e1code = "product of 6 and sum of 4 and 3"
    assert( parseMul(e1code) == parseMul(e1code) )
  }

  "The `sequence` combinator" should "work for `parseMul3`" in testMul(parseMul3)

  "The `postprocess` combinator" should "work for `parseMul4`" in testMul(parseMul4)

  "One" should "be able to implement the AE parser with combinators" in testExp(exp)

  "The `zeroOrMore` combinator" should "build parsers of lists" in {
    val nums = zeroOrMore(exp)
    val nums2 = exp*

    assert(nums("abcdefg") == Some((List.empty, "abcdefg")))

    assert(nums("1234 abcd") == Some((List(Num(1234)), " abcd")))

    assert(nums("sum of 1 and 1sum of 2 and 2sum of 3 and 3rd power of 2") ==
      Some(
        ( List(
            Add(Num(1), Num(1)),
            Add(Num(2), Num(2)),
            Add(Num(3), Num(3))
          ),
          "rd power of 2" )
      )
    )
    assert(nums("sum of 1 and 1sum of 2 and 2sum of 3 and 3rd power of 2") ==
     nums2("sum of 1 and 1sum of 2 and 2sum of 3 and 3rd power of 2"))
  }

  /* Task 2.2.4: Replace `pending` by a real test for `oneOrMore`. */
  "The `oneOrMore` combinator" should "build parsers of nonempty lists" in {
    val nums = oneOrMore(exp)

    assert(nums("abcdefg") == None)

    assert(nums("1234 abcd") == Some((List(Num(1234)), " abcd")))


    assert(nums("sum of 1 and 1sum of 2 and 2sum of 3 and 3rd power of 2") ==
      Some(
        ( List(
            Add(Num(1), Num(1)),
            Add(Num(2), Num(2)),
            Add(Num(3), Num(3))
          ),
          "rd power of 2" )
      )
    )
  }


  /* Task 2.2.5: Replace `pending` by a real test for `parse3`. */
  "`parse3`" should "parse expressions with spaces between words" in {
    assert( parse3("1") == Num(1) )
    assert( parse3("sum of 1 and 1") == Add(Num(1), Num(1)) )

    assert( parse3("  1    ") == Num(1) )
    assert( parse3(" sum    of     1   and 1 ") == Add(Num(1), Num(1)) )
  }
}










