import scala.util.{Try, Success, Failure}
import scala.collection.immutable.{List, Queue}
import Tokens._

/*
 * ShuntingYard
 * shunt Token from infix to postfix notation
 * Per default an operator has precedence 0 and left associativity.
 */
class ShuntingYard(precedences:Map[Operator,Int] = Map(), rightAssociativeOperators:List[Operator] = List()) {


	def shunt(inputTokens:List[Token]):Try[List[Token]] = {
		handleTokens(inputTokens, Queue[Token](), List[Token]()) match {
			case Success((outQueue, opStack)) => finishShunting(outQueue,opStack)
			case Failure(throwable) => Failure(throwable)
		}
	}

	private def getPrecedence(op:Operator):Int = precedences.get(op) match {
		case Some(precedence) => precedence
		case None => 0
	}

	private def isLeftAssociative(op:Operator):Boolean = !rightAssociativeOperators.contains(op)

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

	private def finishShunting(output:Queue[Token], stack:List[Token]):
	Try[List[Token]] = {
		if( stack.isEmpty )
			Success(output.toList)
		else
			stack.head match {
				case lPar:LeftParenthesis =>
					Failure(new Exception("Missing right parenthesis to "+lPar))
				case op:Operator =>
					finishShunting(output.enqueue(op), stack.tail)
				case other =>
					Failure(new Exception("Unsupported token "+other))
			}
	}

	private def handleToken(token:Token, output:Queue[Token], operations:List[Token]):
		Try[(Queue[Token],List[Token])] =
		token match {
			case Whitespace(code:String) => Failure(new Exception("Whitespace unsupported"))
			case Error(message:String) =>  Failure(new Exception(message))
			case IntLiteral(code:String) => Success((output.enqueue(token), operations))
			case op:Operator => handleOperator(op,output,operations)
			case lPar: LeftParenthesis => Success((output, lPar::operations))
			case rPar: RightParenthesis => handleRightParenthesis(rPar, output,operations)
			case _ => Failure(new Exception("unsupported token: "+token))
	}

	private def handleRightParenthesis(rPar:RightParenthesis, output:Queue[Token], stack:List[Token]):
		Try[(Queue[Token],List[Token])] =
			stack match {
				case head::tail =>
					head match {
						case headOp:Operator =>
							handleRightParenthesis(rPar, output.enqueue(headOp), tail)
						case lPar:LeftParenthesis if lPar.sym == rPar.sym =>
							Success((output,tail))
						case lPar:LeftParenthesis if lPar.sym != rPar.sym =>
							Failure(new Exception("Missing right parenthesis to "+lPar))
						case other => Failure(new Exception("Unsupported token: "+other))
					}
				case _ => Failure(new Exception("Missing opening parenthesis"))
			}

	private def handleOperator(op:Operator, output:Queue[Token], stack:List[Token]):
		Try[(Queue[Token],List[Token])] =
			stack match {
				case head::tail =>
					head match {
						case headOp:Operator if shouldPopHeadOperator(op, headOp) =>
							handleOperator(op, output.enqueue(headOp), tail)
						case _ => Success((output, op::stack))
					}
				case _ => Success((output, op::stack))
			}


	private def shouldPopHeadOperator(op:Operator, head:Operator):
		Boolean = ( (isLeftAssociative(op) && getPrecedence(op) <= getPrecedence(head))
					|| (getPrecedence(op) < getPrecedence(head)) )



}