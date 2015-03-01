import org.scalatest._
import scala.util.{Try, Success, Failure}
import PostfixEvaluators._
import Tokens._
import StdTokens._

class PostfixEvaluatorsSpec extends FlatSpec {

  it should "evaluate simple expression" in {
      val evaluator = new PostfixEvaluator( StdOperators.implementations )

      val inputPlus = List(IntLiteral("1"),IntLiteral("2"), plusToken)
      val inputMinus = List(IntLiteral("1"),IntLiteral("2"), minusToken)
      val inputComb = List(IntLiteral("1"),IntLiteral("2"), minusToken, IntLiteral("2"), plusToken)
      val inputError = List(IntLiteral("1"),IntLiteral("2"), minusToken,IntLiteral("2"))
      val inputError2 = List(IntLiteral("1"),IntLiteral("2"), minusToken, plusToken)

      assert( Success(3) == evaluator.evaluate(inputPlus) )
      assert( Success(-1) == evaluator.evaluate(inputMinus)  )
      assert( Success(1)  == evaluator.evaluate(inputComb) )
      assert( evaluator.evaluate(inputError).isFailure )
      assert( evaluator.evaluate(inputError2).isFailure )


  }



}


