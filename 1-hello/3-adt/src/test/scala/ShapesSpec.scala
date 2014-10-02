import org.scalatest._

class ShapesSpec extends FlatSpec with Shapes {
  // Floating point numbers are not good for exact comparision.
  // We test a floating point number by whether it is "close-enough"
  // to the expected value.

  val epsilon   = 0.0000001 // 1 in ten million
  val tolerance = 0.00001   // 1 in a hundred thousand

  def assertCloseEnough(actual: Double, expected: Double): Unit = {
    import scala.math.abs
    val closeEnough =
      if (abs(expected) < epsilon)
        sys error s"expected number too small: $expected"
      else
        abs((expected - actual) / expected) < tolerance
    assert(closeEnough, s": $actual did not equal $expected")
  }

  val unitSquare     = Rectangle(origin, Point(1, 1))
  val unitCircle     = Circle(origin, 1)
  val rightIsosceles = Triangle(origin, Point(0, 1), Point(1, 0))

  "The Shapes trait" should "compute area of shapes correctly" in {
    assertCloseEnough(area(unitSquare), 1)
    assertCloseEnough(area(unitCircle), math.Pi)
    assertCloseEnough(area(rightIsosceles), 0.5)
  }

  it should "compute circumference of shapes correctly" in {
    assertCloseEnough(circumference(unitSquare), 4)
    assertCloseEnough(circumference(unitCircle), 2 * math.Pi)
    assertCloseEnough(circumference(rightIsosceles), 2 + math.sqrt(2))
  }
}
