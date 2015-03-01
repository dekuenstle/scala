import scala.util.{Try, Success, Failure}
import Tokens._

object PostfixEvaluators {
	type OperatorImpl = (List[Token],List[Token]) => Try[(List[Token],Int)]

	class PostfixEvaluator(operations:Map[Operator, OperatorImpl]) {
		def evaluate(tokens:List[Token]):Try[Int] = evaluate(tokens, List[Token]())

		private def evaluate(tokens:List[Token], stack:List[Token]):Try[Int] = {
			if(tokens.isEmpty)
				stack match{
				case List(IntLiteral(code)) =>
					Success(code.toInt)
				case List() =>
					Failure(new Exception("No result left on stack." ) )
				case rest =>
					Failure(new Exception("Couldnt evaluate everything. " + rest) )
				}
			else
				tokens.head match {
					case integer:IntLiteral => evaluate( tokens.tail, integer :: stack )
					case opToken:Operator =>
						operations.get(opToken) match {
							case None => Failure(new Exception("No operation found for token "+opToken))
							case Some(operationImpl) =>
								operationImpl(tokens.tail,stack) match {
									case Success((newStack, jump)) => evaluate(tokens.tail.drop(jump), newStack)
									case Failure(exep) => Failure(exep)
								}
						}
					case other => Failure(new Exception("Unsupported token "+other))
				}
		}
	}
}