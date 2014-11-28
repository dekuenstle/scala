[1]: https://raw.githubusercontent.com/yfcai/scala/gh-pages/4-recurse.md
[2]: http://www.engr.mun.ca/~theo/Misc/exp_parsing.htm#classic
[3]: http://yfcai.github.io/scala/
[4]: http://www.ethoberon.ethz.ch/WirthPubl/CBEAll.pdf
[5]: https://github.com/yfcai/scala/blob/gh-pages/4-recurse.md#7-the-price-of-left-recursion-elimination
[6]: http://www.engr.mun.ca/~theo/Misc/exp_parsing.htm#shunting_yard
[7]: http://www.engr.mun.ca/~theo/Misc/exp_parsing.htm

## Operator precedence

Let us tie up the loose ends from [last week][1]'s parser for arithmetic expressions in infix notation.

When we evaluated `2 + 3 * 4` and `2 * 3 + 4`, one of the results is wrong. That is because multiplications bind tighter than additions from a human's perspective, and we did not pay special attention to it in last week's parser. _Operator precedence_ is the idea that some operators bind tighter than others.

### Tasks

Choose one as appropriate.

1. If your evaluator from last week evaluates both `2 + 3 * 4` and `2 * 3 + 4` correctly, then you may skip #2 and only write a few paragraphs explaining how you implemented operator precedence.

2. If one result of `2 + 3 * 4` and `2 * 3 + 4` is wrong, then please adjust your parser until both are evaluated correctly.

- [Optional] Add parentheses to the language, so that users may group expressions by hand.

  Examples: `(2 + 3) * 4` should evaluate to `20`, and `(1 + 2) * (3 + 4)` should evaluate to `21`.


Some test cases:

        2 + 3 * 4 ==  9
        2 * 3 + 4 == 10
    1 + 2 * 3 + 4 == 11
    1 * 2 + 3 * 4 == 14

### Guide

There are multiple ways to implement operator precedence; feel free to adopt whatever ideas you like. The following steps will lead you through what T. Norvell calls the [_classic solution_][2].

- Read §2 of _Compiler Construction_ by [Niklaus Wirth][4]. Notice how the arithmetic expression grammar (fourth and last example) guarantees that multiplication always comes before addition.

- Read §2.2.6 of _Compilers_ ([2nd reference book][3]).

- Read what [T. Norvell][7] has to say on the subject.


- Modify the starting grammar of [last week][1] to handle operator precedence.

  ```
  Exp := Num | Add | Mul

  Num := <a natural number>

  Add := Exp + Exp

  Mul := Exp * Exp
  ```

- Simplify the parse result for the evaluator in §5 of [last week's exercise][5].


  ```
  def eval(t: Tree): Int = t match {
    case Branch('add, List(lhs, rhs)) =>
      eval(lhs) + eval(rhs)
  
    case Branch('mul, List(lhs, rhs)) =>
      eval(lhs) * eval(rhs)
  
    case Leaf('num, code) =>
      code.toInt
  }
  ```


Finally, I'd like to mention a completely different and potentially harder approach: Dijkstra's [shunting-yard algorithm][6]. One could conceive a grammar of binary operators with precedences, so that its interpreter `parseGrammar` is exactly the shunting-yard algorithm.
