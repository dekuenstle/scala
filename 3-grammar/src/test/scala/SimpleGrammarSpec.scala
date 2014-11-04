import org.scalatest._

import SimpleGrammar._

class SimpleGrammarSpec extends FlatSpec {
  it should "parse and simplify arithmetic expressions" in {
    assert(parseAE("1234") == Leaf('num, "1234"))

    assert(parseAE("sum of 1 and 2") ==
      Branch('add, List(
        Leaf('num, "1"),
        Leaf('num, "2"))))

    assert(parseAE("product of sum of 1 and 2 and 1234") ==
      Branch('mul, List(
        Branch('add, List(
          Leaf('num, "1"),
          Leaf('num, "2"))),
        Leaf('num, "1234"))))
  }

  // Your tests here
  "You" should "write more tests" in fail("not finished yet")
}
