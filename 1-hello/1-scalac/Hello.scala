/** Exercise 1.1: Hello-world in Scala
  *
  * Tasks: compination and execution
  *
  * 1. Install Scala
  *    http://www.scala-lang.org/
  *
  * 2. Save this file as `Hello.scala`
  *
  * 3. Run from command-line `scalac Hello.scala` to compile a bunch
  *    of class files
  *
  * 4. Run from command-line `scala Hello`
  *
  *
  * Tasks: interpretation
  *
  * 1. Run from command-line `scala` to start the Scala interpreter
  *
  * 2. It's possible to evaluate Scala expressions in the interpreter;
  *    try `3 + 5`, or `new Tuple2(3, 5)`
  *
  * 3. Type `:load Hello.scala` into the interpreter to load this file
  *
  * 4. Type `Hello.main(Array.empty)` to run the main function
  */


object Hello {
  /** @param args   the command line arguments; `Array[String]` is
    *               like Java's array type `String[]`.
    *
    * @return    something of type `Unit`, which is the Scala
    *            equivalence of Java's `void` type.
    */
  def main(args: Array[String]): Unit = {
    // define a constant variable `two`
    val two = 1 + 1

    // print as in java
    println("1 plus 1 makes " + two)
  }
}
