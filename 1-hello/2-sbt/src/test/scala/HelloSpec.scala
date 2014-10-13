/** Exercise 1.3: Testing with scalatest
  *
  * The following directory structure should be in place:
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
  *
  * Tasks
  *
  * 1. In the directory `project-root`, create a file called
  *    `build.sbt` with the following lines in it. Beware that an
  *    empty line is REQUIRED between the two configuration lines.
  *
  *    scalaVersion := "2.11.2"
  *
  *    libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.2.1" % "test"
  *
  * 2. Start `sbt` in the directory `project-root`. If `sbt` is
  *    running already, then type `reload` into sbt console.
  *
  * 3. Save this file in the directory `project-root/src/test/scala`.
  *
  * 4. Type `test` in sbt console to run the tests here.
  */

import org.scalatest._

class HelloSpec extends FlatSpec {
  "Hello.add" should "add two integers" in {
    assert(Hello.add(1, 1) == 2)
    assert(Hello.add(1, 2) == 3)
    assert(Hello.add(8, 5) == 13)
    assert(Hello.add(1245, 2368) == 3613)
    assert(Hello.add(35, -60) == -25)

    // uncomment to make the test fail
    // assert(Hello.add(2, 2) == 5)
  }
}
