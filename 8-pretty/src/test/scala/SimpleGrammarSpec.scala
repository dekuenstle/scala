import org.scalatest._
import sext._
import SimpleGrammar._

class SimpleGrammarSpec extends FlatSpec {

  it should "unparse tree" in {
    val tree = parse("if 2 + 2 == 5 then 1984 else 2015")
    assert( parse(unparse(tree)) == tree )
  }

  it should "pretty print tree" in {
    val tree = parse("if 1 + 1 == 2 then if 2 + 2 == 5 then 1111 + 222 + 33 + 4 else 4444 * 333 * 22 * 1 else if 1 == 2 then 2 + 2 else 4 * 5")
    assert( false )
  }

}































