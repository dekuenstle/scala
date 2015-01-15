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

    println(parseAndSimplifyAE("5 - 2 - 1").treeString)
    assert(parseAndEval("5 - 2 - 1")  == 2)
    assert(parseAndEval("36 / 6 / 2") == 3)

  }



}
