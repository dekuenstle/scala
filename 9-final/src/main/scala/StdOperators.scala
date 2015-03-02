import PostfixEvaluators.OperatorImpl
import scala.util.{Try, Success, Failure}
import StdTokens._
import Tokens._


object StdOperations {

	private val ifImplementation: OperatorImpl =
		(tokens, stack) => {
			if(stack.size < 1) Failure(new Exception("Not enough arguments"))
			else stack.head match {
				case IntLiteral(code0) =>
					code0.toInt match {
						case 0 =>
							val thenIndex = tokens.indexOf(thenToken)
							val drop = if(thenIndex > -1) thenIndex+1 else 0
							Success((stack.tail, drop))
						case otherwise =>
							Success((stack.tail, 0))
					}
				case other => Failure(new Exception("Integer expected. Found "+other) )
			}
		}

	private val thenImplementation: OperatorImpl =
		(tokens, stack) => {
			val elseIndex = tokens.indexOf(elseToken)
			val ifIndex = tokens.indexOf(ifToken)
			val drop = if(elseIndex > -1 ) elseIndex+1 else 0
			Success((stack, drop))
		}


	private val elseImplementation: OperatorImpl =
		(tokens, stack) => {
			Success((stack, 0))
		}


	private implicit def binaryIntOperationToOperatorImpl(binOp:Function2[Int,Int,Int]):OperatorImpl =
		(tokens,stack) =>
			if(stack.size < 2) Failure(new Exception("Not enough arguments"))
			else stack.head match {
				case IntLiteral(code0) =>
					val right = code0.toInt
					stack.tail.head match {
						case IntLiteral(code1) =>
							val left = code1.toInt
							val result = binOp(left,right).toString
							Success((IntLiteral(result) :: stack.tail.tail, 0))
						case _ => Failure(new Exception("Operation requires integer argument") )
					}
				case _ => Failure(new Exception("Operation requires integer argument") )
			}


	val precedence = Map(
			plusToken -> 3,
			minusToken -> 3,
			starToken -> 4,
			slashToken -> 4,

			ifToken -> 1,
			thenToken -> 1,
			elseToken -> 1,

			equalToken -> 2
		)

	val implementations: Map[Operator, OperatorImpl] =
		Map(
			plusToken -> binaryIntOperationToOperatorImpl((a:Int, b:Int) => a+b),
			minusToken -> binaryIntOperationToOperatorImpl((a:Int, b:Int) => a-b),
			starToken -> binaryIntOperationToOperatorImpl((a:Int, b:Int) => a*b),
			slashToken -> binaryIntOperationToOperatorImpl((a:Int, b:Int) => a/b),

			ifToken -> ifImplementation,
			thenToken -> thenImplementation,
			elseToken -> elseImplementation,

			equalToken -> binaryIntOperationToOperatorImpl((a:Int, b:Int) => if(a==b) 1 else 0)
		)

}