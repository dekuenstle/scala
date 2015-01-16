import org.scalatest._
import sext._
import SimpleGrammar._

class SimpleGrammarSpec extends FlatSpec {

  it should "parse and simplify arithmetic expressions" in {

    assert(
      parseAndSimplifyAE("3 + 5 / 2")
      == Branch('add, List( Leaf('num, "3"), Branch('div, List( Leaf('num, "5"), Leaf('num, "2"))) ))
      )

    assert(
      parseAndSimplifyAE("3 - 2 + 4 - 2")
      == Branch('sub, List( Leaf('num, "3"), Branch('add, List( Leaf('num, "2"), Branch('sub, List( Leaf('num, "4"), Leaf('num, "2"))) )) ))
      )

    assert(
      parseAndSimplifyAE("1256 + 25 * 48 / 9")
      == Branch('add, List( Leaf('num, "1256"), Branch('mul, List( Leaf('num, "25"), Branch('div, List( Leaf('num, "48"), Leaf('num, "9"))) )) ))
    )
  }


  it should "parse, simplify and evaluate simple arithmetic expressions" in {
    assert(eval(parseAndSimplifyAE("2 + 2")) == 4 )
    assert(eval(parseAndSimplifyAE("3 * 5")) == 15 )
    assert(eval(parseAndSimplifyAE("1234")) == 1234 )
  }

  it should "parse, simplify and evaluate complex arithmetic expressions" in {
    assert(eval(parseAndSimplifyAE("2 + 3 * 4")) == 14 )
    assert(eval(parseAndSimplifyAE("2 * 3 + 4")) == 10 )
    assert(eval(parseAndSimplifyAE("1 + 2 * 3 + 4")) == 11 )
    assert(eval(parseAndSimplifyAE("1 * 2 + 3 * 4 ")) == 14 )

    assert(parseAndEval("5 - 2 - 1")  == 2)
    assert(parseAndEval("36 / 6 / 2") == 3)
  }

  it should "parse and simplify equality" in {
    assert(parseAndSimplifyAE("2 + 2 == 4") == Branch('equ, List( Branch('add, List( Leaf('num, "2"), Leaf('num, "2"))), Leaf('num, "4" ))) )
    assert(parseAndSimplifyAE("25 * 8 == 500 / 2 - 50")
      == Branch('equ, List(
           Branch('mul, List( Leaf('num, "25"), Leaf('num, "8"))),
           Branch('sub, List( Branch('div, List( Leaf('num, "500"), Leaf('num, "2"))), Leaf('num, "50"))))))
  }

  it should "parse and simplify If-then-else" in {
    assert(parseAndSimplifyAE("if 1 == 1 then 2 else 3") == Branch('ifThenElse, List( Branch('equ, List( Leaf('num, "1"), Leaf('num, "1"))), Leaf('num, "2" ), Leaf('num, "3" ))))
    assert(parseAndSimplifyAE("if 2 + 2 == 5 then 1900 + 84 else 5 * 403")
      == Branch('ifThenElse, List( Branch('equ, List( Branch('add,List(Leaf('num, "2"),Leaf('num, "2"))), Leaf('num, "5"))), Branch('add,List(Leaf('num, "1900"),Leaf('num, "84"))), Branch('mul,List(Leaf('num, "5"),Leaf('num, "403"))))))
  }

  it should "parse and simplify and evaluate If-then-else with equality" in {
    assert(parseAndEval("if 1 == 1 then 2 else 3") == 2)
    assert(parseAndEval("if 2 + 2 == 5 then 1900 + 84 else 5 * 403") == 2015)
  }
}































