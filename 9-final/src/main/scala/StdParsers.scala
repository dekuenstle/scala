import util.Combinators._
import Tokens._
import StdTokens._


object StdParsers {

	val whitespaceParser = parseString(" ")^^( Whitespace(_) )

	val intParser:Parser[Token] = parseRegex("[0-9]+")^^( IntLiteral(_) )

	val plusParser:Parser[Token] = parseString("+")^^( s => plusToken )
	val minusParser:Parser[Token] = parseString("-")^^( s => minusToken )
	val starParser:Parser[Token] = parseString("*")^^( s => starToken )
	val slashParser:Parser[Token] = parseString("/")^^( s => slashToken )

	val leftRoundParenthesisParser:Parser[Token] = parseString("(")^^( s => leftRoundParenthesisToken )
	val rightRoundParenthesisParser:Parser[Token] = parseString(")")^^( s => rightRoundParenthesisToken )

	val ifParser:Parser[Token] = parseString("if")^^( s => ifToken )
	val thenParser:Parser[Token] = parseString("then")^^( s => thenToken )
	val elseParser:Parser[Token] = parseString("else")^^( s => elseToken )

	val equalParser:Parser[Token] = parseString("==")^^( s => equalToken )

	val stdOperatorParser = plusParser | minusParser | starParser |  slashParser
	val stdParenthesisParser = leftRoundParenthesisParser | rightRoundParenthesisParser
	val stdIfThenElseParser = ifParser | thenParser | elseParser
	val stdCompareParser = equalParser

	val stdTokenParser = intParser | stdOperatorParser | stdParenthesisParser | stdIfThenElseParser | stdCompareParser
	val stdGarbageParser = whitespaceParser
}