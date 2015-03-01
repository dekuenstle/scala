import scala.util.{Try, Success, Failure}
import scala.collection.immutable.{List, Queue}
import Tokens._

class ShuntingYard() {

	def shunt(inputTokens:List[Token]):Try[List[Token]] = {
		handleTokens(inputTokens, Queue[Token](), List[Token]()) match {
			case Success((outQueue, opStack)) => finishShunting(outQueue,opStack)
			case Failure(throwable) => Failure(throwable)
		}


	}

	private def handleTokens(tokens:List[Token], output:Queue[Token], operations:List[Token]):
		Try[(Queue[Token],List[Token])] =
		{
			if (tokens.isEmpty)
				Success((output, operations))
			else handleToken(tokens.head, output, operations) match {
				case Success((out, stack:List[Token])) => handleTokens(tokens.tail, out, stack)
				case fail => fail
			}

		}


	private def finishShunting(output:Queue[Token], opStack:List[Token]):
	Try[List[Token]] = {
		if( opStack.isEmpty )
			Success(output.toList)
		else
			finishShunting(output.enqueue(opStack.head), opStack.tail)
	}



	private def handleToken(token:Token, output:Queue[Token], operations:List[Token]): Try[(Queue[Token],List[Token])] =
		token match {
			case Whitespace(code:String) => Failure(new Exception("Whitespace unsupported"))
			case Error(message:String) =>  Failure(new Exception(message))
			case IntLiteral(code:String) => Success((output.enqueue(token), operations))
			case Operator(sym:Symbol) => Success((output, token :: operations))
	}




}