import Tokens._
import util.Combinators._

class Tokenizer(
	in:String,
	tokenParser: Parser[Token],
	garbageParser: Parser[Token]) {

	private val (tok, oth) = {
		val garbageParser = removeAllLeadingGarbage(in)
		parseToken(garbageParser)
	}

	private def parseToken(code:String):(Token,String) =
		tokenParser(code) match {
			case Some((token, rest)) => (token, rest)
			case None => (Error("No token found"), "")
	}
	private def removeLeadingGarbage(code:String) =
		garbageParser(code) match {
			case Some((_,garbageless)) => garbageless
			case None => code
	}
	private def removeAllLeadingGarbage(code:String):String = {
		val garbageless = removeLeadingGarbage(code)
		if(garbageless.length==code.length)
			garbageless
		else
			removeAllLeadingGarbage(garbageless)
	}


	def first = tok
	def rest = new Tokenizer(oth, tokenParser, garbageParser)
	def atEnd = removeAllLeadingGarbage(oth).length==0

	def all: List[Token] = if (this.atEnd) List(this.first) else this.first :: this.rest.all
}


