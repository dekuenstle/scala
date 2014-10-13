/** Exercise 1.4: Algebraic datatypes
  *
  * Copyright 2014 Yufei Cai <cai@mathematik.uni-marburg.de>
  *
  * Adapted from:
  * Miran Lipovača. Learn You a Haskell for Great Good.
  * Available online at http://learnyouahaskell.com/
  *
  * Licensed under:
  * Creative Commons Attribution-Noncommercial-Sharealike 3.0 Unported
  * http://creativecommons.org/licenses/by-nc-sa/3.0/
  *
  *
  * Tasks
  *
  * 1. Read through this file to learn about case classes and
  *    pattern-matching in Scala.
  *
  * 2. Replace ??? in this file by a working implementation of
  *    `circumference` so that the test `ShapesSpec` passes.
  */

// `trait` is like an abstract class in Java
trait Shapes {

  /* Case classes are similar to structs in C. */
  case class Point(x: Double, y: Double)

  /* Create a point */
  val origin: Point = Point(0, 0)

  /* Access the x-coordinate.
   * We may write alternatively
   *
   *   def x_coordinate(p: Point): Double = p.x
   */
  def x_coordinate(p: Point): Double = p match {
    case Point(x, y) =>
      x
  }

  /* Access the y-coordinate.
   * We may write alternatively
   *
   *   def y_coordinate(p: Point): Double = p.y
   */
  def y_coordinate(p: Point): Double = p match {
    case Point(x, y) =>
      y
  }

  /* It is possible to pattern-match on 2 points at the same time. */
  def distance(p1: Point, p2: Point): Double = (p1, p2) match {
    case (Point(x1, y1), Point(x2, y2)) =>
      val dx = x2 - x1
      val dy = y2 - y1
      math.sqrt(dx * dx + dy * dy)
  }


  /* `Circle`, `Rectangle` and `Triangle` extend the trait `Shape`.
   * Given an object of type `Shape`, we can pattern-match on it
   * to figure out whether it's a circle, a rectangle or a triangle.
   */
  sealed trait Shape
  case class Circle(center: Point, radius: Double) extends Shape
  case class Rectangle(bottomLeft: Point, topRight: Point) extends Shape
  case class Triangle(v1: Point, v2: Point, v3: Point) extends Shape

  // compute the area inside a shape
  def area(shape: Shape): Double = shape match {
    case Circle(center, radius) =>
      math.Pi * radius * radius

    case Rectangle(Point(x1, y1), Point(x2, y2)) =>
      math.abs((x2 - x1) * (y2 - y1))

    case Triangle(Point(x1, y1), Point(x2, y2), Point(x3, y3)) =>
      math.abs(
        0.5 * ((x1 - x3) * (y2 - y1) - (x1 - x2) * (y3 - y1))
      )
  }


  /* compute the circumference (i. e., length of enclosing curve)
   * of a shape
   *
   * In Scala, one can put ??? as a place holder for something
   * they have yet to implement. Beware that ??? may not work
   * correctly if the type of the expected expression is unknown.
   *
   * Here and elsewhere, the student should replace ??? by
   * a real implementation.
   *
   * The method `distance` may be useful here.
   */
  def circumference(shape: Shape): Double = ???
}
