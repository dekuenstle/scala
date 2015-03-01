import org.scalatest._
import scala.util.{Try, Success, Failure}
import Tokens._


class ShuntingYardSpec extends FlatSpec {

  it should "shunt simple infix to postfix" in {
    val shunter = new ShuntingYard()

    val plusPostfix = List(IntLiteral("1"),IntLiteral("2"), plus)
    val plusInfix = List(IntLiteral("1"),plus,IntLiteral("2"))

    assert(shunter.shunt(plusInfix) == Success(plusPostfix))
  }

  it should "shunt with precedence" in {
    val shunter = new ShuntingYard()

    val postfix1 = List(IntLiteral("1"), IntLiteral("2"),IntLiteral("3"), star, plus)
    val postfix2 = List(IntLiteral("1"),IntLiteral("2"), star, IntLiteral("3"), plus)

    val infix1 = List(IntLiteral("1"),plus,IntLiteral("2"), star, IntLiteral("3"))
    val infix2 = List(IntLiteral("1"),star,IntLiteral("2"), plus, IntLiteral("3"))


    assert(shunter.shunt(infix1) == Success(postfix1))
    assert(shunter.shunt(infix2) == Success(postfix2))
  }



}


