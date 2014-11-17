import org.scalatest._

import SimpleGrammar._
//import NaiveGrammar

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

  val ae2: Grammar =
    Grammar(
      start = exp,
      rules = Map(
        exp -> (add | mul),
        add -> (sumOf ~ num ~ and ~ mul),
        mul -> (productOf ~ num ~ and ~ (add | num))
      )
    )
  // Your tests here
  "You" should "write more tests" in  {
    assert( parseAE("product of 3 and sum of 1 and product of 2 and 5") ==
      Branch('mul,List(Leaf('num,"3"), Branch('add,List(Leaf('num,"1"), Branch('mul,List(Leaf('num,"2"), Leaf('num,"5")))))))  )

    assert( parseAE("product of 3 and sum of 1 and product of 2 and 5") ==
      parseGrammar(ae2)("product of 3 and sum of 1 and product of 2 and 5"))
  }
}
