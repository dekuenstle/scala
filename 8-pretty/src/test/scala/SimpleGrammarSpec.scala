import org.scalatest._
import sext._
import SimpleGrammar._

class SimpleGrammarSpec extends FlatSpec {

  it should "unparse tree" in {
    val tree = parse("if 2 + 2 == 5 then 1984 else 2015")
    assert( parse(unparse(tree)) == tree )
  }

  it should "pretty print tree" in {

    val tree = parse("if 1 + 1 == 2 then if 2 + 2 == 5 then 1111 + 222 + 33 + 4 else 4444 * 333 * 22 * 1 else if 1 == 2 then 2 + 2 else 4 * 5")

    val pretty80 = pretty(tree, 80)
    val pretty40 = pretty(tree, 40)
    val pretty13 = pretty(tree, 13)

    println("Less than 80:")
    println(pretty80)
    println("")

    println("Less than 40:")
    println(pretty40)
    println("")

    println("Less than 13:")
    println(pretty13)

    assert( pretty80.width <= 80)
    assert( pretty40.width <= 40)
    assert( pretty13.width <= 13)

    assert( pretty40.size > pretty80.size)
    assert( pretty13.size > pretty40.size)

    /*
    assert(
    pretty80 == """if 1 + 1 == 2 then
  if 2 + 2 == 5 then 1111 + 222 + 33 + 4 else 4444 * 333 * 22 * 1
else
  if 1 == 2 then 2 + 2 else 4 * 5""" )

    assert( pretty(tree, 40) == """
if 1 + 1 == 2 then
  if 2 + 2 == 5 then
    1111 + 222 + 33 + 4
  else
    4444 * 333 * 22 * 1
else
  if 1 == 2 then 2 + 2 else 4 * 5""" )

    assert( pretty(tree, 13) == """if 1 + 1 ==
     2 then
  if 2 + 2 ==
       5 then
    1111 +
      222 +
      33 + 4
  else
    4444 *
      333 *
      22 * 1
else
  if 1 ==
       2 then
    2 + 2
  else
    4 * 5""" )
*/
  }

  implicit class StringOps(self:String){
    def width:Int = self.lines.map(_.length).min
  }
}































