/** Exercise 1.2: sbt
  *
  * sbt is a command-line build tool for Scala projects featuring
  * - automatic download of Scala compilers
  * - automatic download of dependent libraries
  * - compilation, execution, testing
  *
  *
  * Tasks
  *
  * 1. Download and install `sbt`.
  *    http://www.scala-sbt.org/
  *
  * 2. Create the following directories:
  *
  *    project-root (or any other name)
  *     |
  *     +--src
  *         |
  *         +--main
  *         |   |
  *         |   +--scala
  *         |
  *         +--test
  *             |
  *             +--scala
  *
  * 3. Put this file in `project-root/src/main/scala`.
  *
  * 4. In the directory `project-root`, run `sbt`.
  *
  * 5. Type `run`; you should see "1 plus 1 makes 2".
  */

object Hello {
  def add(lhs: Int, rhs: Int): Int = lhs + rhs

  def main(args: Array[String]): Unit = {
    val two = add(1, 1)

    println("1 plus 1 makes " + two)
  }
}
