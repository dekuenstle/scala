import org.scalatest._
import PostfixEvaluators._
import Tokens._


class PostfixEvaluatorsSpec extends FlatSpec {

  it should "evaluate simple expression" in {
      val evaluator = new PostfixEvaluator( Map(
        plus -> ((a:Int, b:Int) => a+b),
        minus -> ((a:Int, b:Int) => a-b)
      ) )

      val inputPlus = List(IntLiteral("1"),IntLiteral("2"), plus)
      val inputMinus = List(IntLiteral("1"),IntLiteral("2"), minus)
      val inputComb = List(IntLiteral("1"),IntLiteral("2"), minus, IntLiteral("2"), plus)
      val inputError = List(IntLiteral("1"),IntLiteral("2"), minus,IntLiteral("2"))
      val inputError2 = List(IntLiteral("1"),IntLiteral("2"), minus, plus)

      assert( IntLiteral("3") == evaluator.evaluate(inputPlus) )
      assert( IntLiteral("-1") == evaluator.evaluate(inputMinus)  )
      assert( IntLiteral("1")  == evaluator.evaluate(inputComb) )
      assert( evaluator.evaluate(inputError).isInstanceOf[Error] )
      assert( evaluator.evaluate(inputError2).isInstanceOf[Error] )


  }



}


