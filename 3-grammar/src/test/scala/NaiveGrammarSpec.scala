/** Tests for exercise 3.2 */

import org.scalatest._

import NaiveGrammar._

class NaiveGrammarSpec extends FlatSpec {

  def parseAE = parseGrammar(ae)

  val n1234: Tree =
    Branch('exp, List(Leaf('num, "1234")))

  val sumOf1And2: Tree =
    Branch('exp, List(
      Branch('add, List(
        Leaf('keyword, "sum of "),
        Branch('exp, List(Leaf('num, "1"))),
        Leaf('keyword, " and "),
        Branch('exp, List(Leaf('num, "2")))))))

  "ae" should "parse arithmetic expressions" in {
    assert(parseAE("1234") == n1234)

    assert(parseAE("sum of 1 and 2") == sumOf1And2)

    assert(parseAE("product of sum of 1 and 2 and 1234") ==
      Branch('exp, List(
        Branch('mul, List(
          Leaf('keyword, "product of "),
          sumOf1And2,
          Leaf('keyword, " and "),
          n1234)))))
  }

  it should "parse and simplify arithmetic expressions" in {
    assert(parseAndSimplifyAE("1234") == Leaf('num, "1234"))

    assert(parseAndSimplifyAE("sum of 1 and 2") ==
      Branch('add, List(
        Leaf('num, "1"),
        Leaf('num, "2"))))

    assert(parseAndSimplifyAE("product of sum of 1 and 2 and 1234") ==
      Branch('mul, List(
        Branch('add, List(
          Leaf('num, "1"),
          Leaf('num, "2"))),
        Leaf('num, "1234"))))
  }

  "You" should "write more tests" in fail("not finished yet")
}
