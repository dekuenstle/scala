import Tokens._

object PostfixEvaluators {
	type OperatorImpl = List[Token] => List[Token]

	implicit def binaryIntOperationToOperatorImpl(binOp:Function2[Int,Int,Int]):OperatorImpl =
		stack =>
			if(stack.size < 2) List(Error("Not enough arguments"))
			else stack.head match {
				case IntLiteral(code0) =>
					val right = code0.toInt
					stack.tail.head match {
						case IntLiteral(code1) =>
							val left = code1.toInt
							val result = binOp(left,right).toString
							IntLiteral(result) :: stack.tail.tail
						case _ => Error("Operation requires integer argument") :: stack
					}
				case _ => Error("Operation requires integer argument") :: stack
			}

	class PostfixEvaluator(operations:Map[Operator, OperatorImpl]) {

		def evaluate(tokens:List[Token]):Token = {
			var stack = List[Token]()
			for (token <- tokens) {
				stack = handleToken(token, stack)
				if(stack.head.isInstanceOf[Error]) return stack.head
			}
			stack match{
				case List(IntLiteral(code)) =>
					stack.head
				case _ =>
					Error("Couldnt evaluate everything. " + stack.toString)
			}
		}

		private def handleToken(token:Token, stack:List[Token]):List[Token] =
			token match {
				case integer:IntLiteral => integer :: stack
				case operator:Operator => operations(operator)(stack)
			}

	}
}