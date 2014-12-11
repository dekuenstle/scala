import org.scalatest._
import sext._
import SimpleGrammar._

class SimpleGrammarSpec extends FlatSpec {

  it should "parse arithmetic expressions" in {
    println( parseAE("2 + 2").treeString )
  }

  it should "parse and simplify arithmetic expressions" in {
    println( parseAndSimplifyAE("2 + 2").treeString )
    println( parseAndSimplifyAE("3 * 5").treeString )
    println( parseAndSimplifyAE("1234").treeString )

    assert(parseAndSimplifyAE("2 + 2") ==
      Branch('add, List( Leaf('num, "2"), Leaf('num, "2"))) )
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

  }
}
