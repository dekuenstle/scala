import org.scalatest._
import Parsers._
import Tokens._


class TokenizersSpec extends FlatSpec {

  it should "recognize end" in {
    val tokEnd = new Tokenizer("  1  ", intParser, whitespaceParser)
    val tokNotEnd = new Tokenizer("   1  23", intParser, whitespaceParser)

    assert(tokEnd.atEnd)
    assert(!tokNotEnd.atEnd)
  }

  it should "tokenize recursive" in {
    val tokenizer1 = new Tokenizer(" 1 12 ", intParser, whitespaceParser)
    assert(!tokenizer1.atEnd)
    assert(tokenizer1.first == IntLiteral("1") )

    val tokenizer2 = tokenizer1.rest
    assert(tokenizer2.atEnd)
    assert(tokenizer2.first == IntLiteral("12") )
  }

}





























