/** Extra credit exercise: JSon
  *
  * Deadline: End of semester
  *
  * This exercise is optional. Completing it will give you the
  * credit of a 1-week exercise, though your final grade will
  * never exceed 100%.
  *
  * JSon is a popular format for sending lists and objects over
  * the internet.
  *
  * Tasks
  *
  * 1. Design abstract syntax trees for JSon and write a JSon
  *    parser. It is up to you how to structure the parser.
  *    The syntax of JSon is described here:
  *
  *    http://www.json.org/
  *
  * 2. Evaluate JSon expressions to JSonObject.
  *
  * 3. Test your parser. The examples here may be useful:
  *
  *   http://json.org/example
  */

object JSON {

  // Task 1: Parser goes here.

  // Task 2: Evaluate JSon expressions to Scala objects.
  //         - evaluate JSON numbers to Int or Double
  //         - evaluate JSON arrays to List
  //         - evaluate JSON object to Map[String, Any]
  //         - evaluate JSON strings to String
  //
  // Feel free to change the return type of `eval` if you think
  // it is not type-safe. Should you choose to do so, then
  // `JSONSpec.extract` should be modified accordingly.
  //
  def eval(jsonExp: String): Any = ???

  // Task 3: see src/test/scala/JSonSpec.scala.
}
