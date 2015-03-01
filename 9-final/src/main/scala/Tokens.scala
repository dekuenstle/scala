object Tokens {
	trait Token
	case class Whitespace(code:String) extends Token
	case class IntLiteral(code:String) extends Token
	case class Error(message:String) extends Token
	case class Operator(sym:Symbol) extends Token
	case class LeftParenthesis(sym:Symbol) extends Token
	case class RightParenthesis(sym:Symbol) extends Token
}