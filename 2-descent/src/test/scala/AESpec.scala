import org.scalatest._

class AESpec extends FlatSpec with AE {
  "`eval`" should "evaluate arithmetic expressions correctly" in {
    // recall that e1 = 6 + 6 * 6
    assert(eval(e1) == 42)

    // e2 = 6 * (4 + 3)
    assert(eval(e2) == 42)
  }

  "`parseNum`" should "parse a series of digits" in {
    assert(parseNum("1234") == Some((Num(1234), "")))
    assert(parseNum("1234+5678") == Some((Num(1234), "+5678")))

    // it should not parse negative numbers, or anything else
    // not starting with a digit.
    assert(parseNum("-123") == None)
    assert(parseNum("abc123") == None)
    assert(parseNum(" 123") == None)
  }

  "`parseAdd`" should "parse sums" in {
    assert(parseAdd("sum of 1 and 1") ==
      Some((Add(Num(1), Num(1)), "")))

    assert(parseAdd("sum of 1234 and 5678") ==
      Some((Add(Num(1234), Num(5678)), "")))

    assert(parseAdd("sum of 1 and 1 and 1 and 1") ==
      Some((Add(Num(1), Num(1)), " and 1 and 1")))

    assert(parseAdd("product of 1 and 1") == None)
    assert(parseAdd("1 + 1") == None)
    assert(parseAdd("sum of x and y") == None)
  }

  "`parseMul`" should "parse products" in {
    assert(parseMul("product of 1 and 1") ==
      Some((Mul(Num(1), Num(1)), "")))

    assert(parseMul("product of 1234 and 5678") ==
      Some((Mul(Num(1234), Num(5678)), "")))

    assert(parseMul("product of 1 and 2 and 3 and 4") ==
      Some((Mul(Num(1), Num(2)), " and 3 and 4")))

    assert(parseMul("sum of 1 and 1") == None)
    assert(parseMul("1 * 1") == None)
    assert(parseMul("product of x and y") == None)
  }

  "`parse`" should "parse e1 and e2" in {
    assert(parse("sum of 6 and product of 6 and 6") == e1)
    assert(parse("product of 6 and sum of 4 and 3") == e2)
  }

  "`parse2`" should "parse e1 and e2" in {
    assert(parse2("add 6 to multiply 6 by 6") == e1)
    assert(parse2("multiply 6 by add 4 to 3") == e2)
  }

  // Your tests here
}
