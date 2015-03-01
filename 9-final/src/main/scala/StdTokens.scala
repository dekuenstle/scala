import Tokens._

object StdTokens {
	val plusToken = Operator('plus)
	val minusToken = Operator('minus)
	val starToken = Operator('star)
	val slashToken = Operator('slash)

	val leftRoundParenthesisToken = LeftParenthesis('roundParenthesis)
	val rightRoundParenthesisToken = RightParenthesis('roundParenthesis)

	val ifToken = Operator('if)
	val thenToken = Operator('then)
	val elseToken = Operator('else)

	val equalToken = Operator('equal)

}