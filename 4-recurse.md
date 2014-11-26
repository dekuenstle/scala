[1]: https://github.com/nikita-volkov/sext
[2]: https://github.com/yfcai/scala/blob/gh-pages/3-grammar/src/main/scala/SimpleGrammar.scala
[3]: https://www.youtube.com/watch?v=_Yg3fp6cs7U
[4]: http://yfcai.github.io/scala/
[5]: https://github.com/yfcai/scala/blob/gh-pages/3-grammar/src/main/scala/NaiveGrammar.scala#L273

## Adventure Guide to Left Recursion



Yufei Cai

### 1. Introduction

This exercise is a step-by-step guide for developing a parser of your own.
As long as the parser behaves as expected, you can structure it however you like.
I went through the steps and ended up writing about 300 lines of code.
Can you manage with fewer?

Here is what you are supposed to do:

1. Go through these steps.
2. Follow instructions and write code as necessary.
3. Answer questions in §5, §7 and §8 with comments in your code.


### 2. Visualizing syntax trees

Our syntax trees are getting more and more complicated. They are hardly readable when printed directly to the console:


    > println(t)
    Branch('add,List(Leaf('num,2),Branch('add,List(Leaf('num,3),Leaf('num,4)))))

Nikita Volkov's [SExt][1] helps us visualize trees. To use it, change Scala version to 2.10.4 and add a library dependency in  `build.sbt`:

    scalaVersion := "2.10.4"

    libraryDependencies += "com.github.nikita-volkov" % "sext" % "0.2.3"

Importing `sext._` adds the method `treeString` to all objects. The method converts the object into a human-readable string. It should improve your debugging experience.

    scala> import sext._
    import sext._
    
    scala> println(t.treeString)
    Branch:
    - 'add
    - List:
    | - Leaf:
    | | - 'num
    | | - 2
    | - Branch:
    | | - 'add
    | | - List:
    | | | - Leaf:
    | | | | - 'num
    | | | | - 3
    | | | - Leaf:
    | | | | - 'num
    | | | | - 4

### 3. Arithmetic expressions with infix operators

Are you tired of writing "sum of 1 and sum of 2 and product of 3 and 4"? Fear no more, for we are about to write "1 + 2 + 3 * 4" instead.

Take your [simple grammar interpreter][2]. Define a grammar object for arithmetic expressions with infix operators.

    Exp := Num | Add | Mul

    Num := <a natural number, just like before>

    Add := Exp + Exp

    Mul := Exp * Exp

### 4. Parse something

Use your [simple grammar interpreter][2] to parse something like "2 + 2" or "1234". What happens? Why?

### 5. What's wrong?

- Watch this [youtube video][3].
- Read section 4.3.3 of _Compilers_ ([2nd reference book][4]).
- Read section 6.4 of _Parsing Techniques_ ([3rd reference book][4]).
- Research "left recursion elimination" on the internet.

What do you think is wrong with the simple grammar of arithmetic expressions with infix operators? How to fix it?

### 6. Fix it

Write another grammar for the language of arithmetic expressions with operators. Parsing "2 + 2" with that grammar should terminate and produce some syntax tree.

### 7. The price of left-recursion elimination

Hopefully your code can parse "2 + 2" by now. What syntax tree does it produce? One would expect the following syntax tree from "2 + 2". How is the result of your parser different?

    Branch:
    - 'add
    - List:
    | - Leaf:
    | | - 'num
    | | - 2
    | - Leaf:
    | | - 'num
    | | - 2

Here is a straightforward evaluator of arithmetic expressions.

    def eval(t: Tree): Int = t match {
      case Branch('add, List(lhs, rhs)) =>
        eval(lhs) + eval(rhs)
  
      case Branch('mul, List(lhs, rhs)) =>
        eval(lhs) * eval(rhs)
  
      case Leaf('num, code) =>
        code.toInt
    }

Write a function to convert the result of your parser so that `eval` can process it. [NaiveGrammar.simplifyAE][5] is somewhat similar. "2 + 2" should evaluate to 4. "1234" should evaluate to 1234. "3 * 5" should evaluate to 15.

### 8. Loose ends

The parser is not quite perfect yet. What does "2 + 3 * 4" evaluate to? What about "2 * 3 + 4"? Is the result what you expect? If not, why?

If there is an error, you do not have to fix it in this exercise. We will investigate the problem in detail in the future. Do spend some thoughts on it and tell me your solution ideas.
