import org.scalatest._
import scala.util.{Try, Success, Failure}
import Tokens._
import StdTokens._


class ShuntingYardSpec extends FlatSpec {

  it should "shunt simple infix to postfix" in {
    val shunter = new ShuntingYard()

    val plusPostfix = List(IntLiteral("1"),IntLiteral("2"), plusToken)
    val plusInfix = List(IntLiteral("1"),plusToken,IntLiteral("2"))

    assert(shunter.shunt(plusInfix) == Success(plusPostfix))
  }

  it should "shunt with precedence" in {
    val precedence = Map(
      plusToken -> 1,
      starToken -> 2
    )
    val shunter = new ShuntingYard(precedence)

    val postfix1 = List(IntLiteral("1"), IntLiteral("2"),IntLiteral("3"), starToken, plusToken)
    val postfix2 = List(IntLiteral("1"),IntLiteral("2"), starToken, IntLiteral("3"), plusToken)

    val infix1 = List(IntLiteral("1"),plusToken,IntLiteral("2"), starToken, IntLiteral("3"))
    val infix2 = List(IntLiteral("1"),starToken,IntLiteral("2"), plusToken, IntLiteral("3"))

    assert(shunter.shunt(infix1) == Success(postfix1))
    assert(shunter.shunt(infix2) == Success(postfix2))
  }

  it should "shunt with associativity" in {
    val rightAssociative = List( minusToken )
    val shunterLeft = new ShuntingYard()
    val shunterRight = new ShuntingYard(Map(),rightAssociative)

    val postfixLeft = List(IntLiteral("1"), IntLiteral("2"), minusToken, IntLiteral("3"), minusToken)
    val postfixRight = List(IntLiteral("1"),IntLiteral("2"), IntLiteral("3"), minusToken, minusToken)

    val infix = List(IntLiteral("1"),minusToken,IntLiteral("2"), minusToken, IntLiteral("3"))

    assert(shunterLeft.shunt(infix) == Success(postfixLeft))
    assert(shunterRight.shunt(infix) == Success(postfixRight))
  }

  it should "shunt with parenthesis" in {
    val precedence = Map(
      plusToken -> 1,
      starToken -> 2
    )
    val shunter = new ShuntingYard(precedence)

    val postfix = List(IntLiteral("1"), IntLiteral("2"), plusToken, IntLiteral("3"), starToken)
    val infix = List( leftRoundParenthesisToken, IntLiteral("1"),plusToken,IntLiteral("2"), rightRoundParenthesisToken, starToken, IntLiteral("3"))

    assert(shunter.shunt(infix) == Success(postfix))
  }
}


