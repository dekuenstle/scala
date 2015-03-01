import Tokens._
import util.Combinators._


object Parsers {

	val whitespaceParser = parseString(" ")^^( Whitespace(_) )

	val intParser = parseRegex("[0-9]+")^^( IntLiteral(_) )

	val plusParser = parseString("+")^^( s => plus )
	val minusParser = parseString("-")^^( s => minus )
}