[1]: https://github.com/yfcai/scala/blob/gh-pages/7-associativity.md
[2]: https://github.com/yfcai/scala/blob/gh-pages/4-recurse.md

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

