import org.scalatest._
import StdExpressionEvaluators._


class StdExpressionEvaluatorsSpec extends FlatSpec {

  it should "evaluate simple expressions" in {
    assert("1234".eval == 1234 )
    assert( "1+1".eval == 2 )

    assert("2 + 3 * 4".eval == 14 )
    assert("2 * 3 + 4".eval == 10 )

    assert("5 - 2 - 1".eval == 2)
    assert("36 / 6 / 2".eval == 3)

    assert( "(1+1)*2".eval == 4 )
  }

  it should "evaluate longer expressions" in {
    assert("1 + 2 * 3 + 4".eval == 11 )
    assert("1 * 2 + 3 * 4 ".eval == 14 )
    assert("36 / 6 / 2 - 3 - 4".eval == -4)
  }

  it should "evaluate complex expressions" in {
    assert("if 1 == 1 then 2 else 3".eval == 2)
    assert("if 2 + 2 == 5 then 1900 + 84 else 5 * 403".eval == 2015)
    assert("if 2 + 2 == 4 then 1900 + 84 else 5 * 403".eval == 1984)
  }

}


