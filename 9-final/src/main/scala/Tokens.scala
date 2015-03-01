object Tokens {
	trait Token
	case class Whitespace(code:String) extends Token
	case class IntLiteral(code:String) extends Token
	case class Error(message:String) extends Token
	case class Operator(sym:Symbol) extends Token

	val noTokenFoundError = Error("No token found")

	val plus = Operator('plus)
	val minus = Operator('minus)
	val star = Operator('star)
	val slash = Operator('slash)

}