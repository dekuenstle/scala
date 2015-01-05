[1]: https://raw.githubusercontent.com/yfcai/scala/gh-pages/4-recurse.md
[2]: http://www.engr.mun.ca/~theo/Misc/exp_parsing.htm#classic
[3]: http://yfcai.github.io/scala/
[4]: http://www.ethoberon.ethz.ch/WirthPubl/CBEAll.pdf
[5]: https://github.com/yfcai/scala/blob/gh-pages/4-recurse.md#7-the-price-of-left-recursion-elimination
[6]: http://www.engr.mun.ca/~theo/Misc/exp_parsing.htm#shunting_yard
[7]: http://www.engr.mun.ca/~theo/Misc/exp_parsing.htm

## Associativity

#### Task 1: Subtraction and division

You wrote a parser for arithmetic expressions with addition and multiplication. Let us introduce subtraction and division into the language. Subtractions should bind as tightly as addition, and division should bind as tightly as multiplication. The parser should succeed on the following expressions:

    3 + 5 / 2
    3 - 2 + 4 - 2
    1256 + 25 * 48 / 9

#### Task 2: Handle associativity

Let us extend the evaluator of the previous exercise to subtractions and divisions. Replace the symbols `'sub` and `'div` by your own symbols for subtractions and divisions. Test the evaluator.


  ```
  def eval(t: Tree): Int = t match {

    case Branch('sub, List(lhs, rhs)) =>
      eval(lhs) - eval(rhs)

    case Branch('div, List(lhs, rhs)) =>
      eval(lhs) / eval(rhs)

    case Branch('add, List(lhs, rhs)) =>
      eval(lhs) + eval(rhs)
  
    case Branch('mul, List(lhs, rhs)) =>
      eval(lhs) * eval(rhs)
  
    case Leaf('num, code) =>
      code.toInt
  }
  ```

Does the evaluator return correct results on `5 - 2 - 1`? What about `32 / 4 / 2`? Why?

Modify your parser until the evaluator works as expected.

    parseAndEval("5 - 2 - 1")  == 2
    parseAndEval("36 / 6 / 2") == 3


#### Task 3: A bigger language

Extend the parser so that it supports the following features. You need not extend the evaluator.

* Arithmetic expressions with addition, multiplication, subtraction and division.

* Integer comparison. Support at least the equality operator `==`. The parser should succeed on strings such as `2 + 2 == 4` and `25 * 8 == 500 / 2 - 50`

* If-then-else expressions. Users of the language should be able to write:

  * `if 1 == 1 then 2 else 3`
  * `if 2 + 2 == 5 then 1900 + 84 else 5 * 403`

Other language features, like loops, are optional. Feel free to add them to your grammar.