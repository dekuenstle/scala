import scala.util.{Try, Success, Failure}

import PostfixEvaluators._


object StdExpressionEvaluators{

	def evaluateExpression(input:String):Try[Int] = {
	    val tokenizer = new Tokenizer(input, StdParsers.stdTokenParser, StdParsers.stdGarbageParser)
	    val tokens = tokenizer.all

	    val shunter = new ShuntingYard(StdOperators.precedence)
	    shunter.shunt(tokens) match {
	    	case Failure(ex) => Failure(ex)
	    	case Success(shuntedTokens) =>
	    		val evaluator = new PostfixEvaluator( StdOperators.implementations )
	    		evaluator.evaluate( shuntedTokens )
	    }
	}

	implicit class StringEval(str:String) {
		def eval:Int = evaluateExpression(str) match {
			case Success(res) => res
			case Failure(e) => println(e); 0
		}
	}

}