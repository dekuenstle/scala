[1]: https://github.com/yfcai/scala/blob/gh-pages/7-associativity.md
[2]: https://github.com/yfcai/scala/blob/gh-pages/4-recurse.md
[3]: http://citeseerx.ist.psu.edu/viewdoc/download;jsessionid=CACEC714AEEB790C0A161C1AE6819AE0?doi=10.1.1.38.8777&rep=rep1&type=pdf

## Pretty Printing


Converting syntax trees back to strings is an important task.
Some of you wanted to read the trees to help debug their
parsers.
Calling the `toString` method produces something unreadable
for large trees.
In [exercise 4][2], we used the library `sext` to visualize trees.

A real compiler wants to show program code in error messages,
not syntax trees. *Pretty-printing* is the task of converting
syntax trees to human-readable code. It is parsing in the backward
direction.


#### Task 1: From tree to code back to tree

Take the parser of the [previous exercise][1]. Write a method
`unparse` to produce code from each syntax tree, so that the parser
produces the same syntax tree from the code.


    def unparse(tree: Tree): String

    // example tree
    val tree = parse("if 2 + 2 == 5 then 1984 else 2015")

    // `parse` should be the left-inverse of `unparse`.
    assert(parse(unparse(tree)) == tree)

#### Task 2: Make it pretty

Is the result of your `unparse` pleasing to the eye? Does it always
put everything on one single line? If so, then long lines will be hard to read.

    if 1 + 1 == 2 then if 2 + 2 == 5 then 1111 + 222 + 33 + 4 else 4444 * 333 * 22 * 1 else if 1 == 2 then 2 + 2 else 4 * 5

It's much easier to read if no line is longer than 80 characters,
and there are judicious line breaks and indentations.


    if 1 + 1 == 2 then
      if 2 + 2 == 5 then 1111 + 222 + 33 + 4 else 4444 * 333 * 22 * 1
    else
      if 1 == 2 then 2 + 2 else 4 * 5

Some may find it better to render the code in 40 columns.

    if 1 + 1 == 2 then
      if 2 + 2 == 5 then
        1111 + 222 + 33 + 4
      else
        4444 * 333 * 22 * 1
    else
      if 1 == 2 then 2 + 2 else 4 * 5

Some people need a super big font for their terminal, which can only fit
13 characters per line. It becomes necessary to insert line breaks after
binary operators.

    if 1 + 1 ==
         2 then
      if 2 + 2 ==
           5 then
        1111 +
          222 +
          33 + 4
      else
        4444 *
          333 *
          22 * 1
    else
      if 1 ==
           2 then
        2 + 2
      else
        4 * 5

Write a method `pretty` that does its best to produce code that's
pleasant-looking and fits within a certain number of columns.

    def pretty(tree: Tree, lineWidth: Int): String

*Warning*: Breaking the line at every possible opportunity does
not count as *pretty.*

#### Guide

Task 2 is not easy. Here's an idea.

There are two ways to render a binary operation: horizontally or vertically.

    // horizontal
    left_hand_side + right_hand_side

    // vertical
    left_hand_side +
      right_hand_side

There are two ways to render a condition statement: horizontally or vertically.

    // horizontal
    if condition then something else something_else

    // vertical
    if condition then
      something
    else
      something_else

Thus, there are two ways to print `1 + 1`, four ways to print
`1 + 1 == 2`, and 32768 ways to print the example program of
task 2. Enumerate all those possibilities, and choose the best one
according to the given line width. My implementation likes to print as few
lines as possible without causing lines to overflow.

You may recognize that the pretty printer described above is very slow:
It runs in time exponential in the size of the syntax tree. You need
not worry about performance in this exercise.

There is also the question of how to keep track of the current indentation
level. One possibility is to use the intermediate representation `Layout` for
the code to be printed: As a list of lines together with their
indentation level.

    // intermediate representation of code to be printed
    type Layout = List[(Int, String)]

    // convert layouts to strings.
    // convert List((0, "hello"), (2, "world")) to:
    //
    // hello
    //   world
    //
    def render(layout: Layout): String

Define `Doc` to be lists of all possible layouts of syntax trees.
The pretty printer converts each tree to `Doc`, then finds the
best layout in the list according to some layout-ranking function.

    // all possible layouts of a syntax tree
    type Doc = List[Layout]

    // step 1: enumerate all possible ways to print a syntax tree
    def enumerate(tree: Tree): Doc

    // step 2: find the best layout according to some line width
    def findBestLayout(doc: Doc, lineWidth: Int): Layout

    // step 3: render the layout as a string
    def render(layout: Layout): String


#### Reference

John Hughes: [The Design of a Pretty-printing Library][3].
