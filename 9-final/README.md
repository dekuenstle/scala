Shunting Yard
=============

Motivation
--------

Evaluating simple mathematic expressions like `1+2*4` to `9` seems easy for our educated mind.
But if expressions become more complex we want automate this job and let our computer work instead of ourselfs.
This scala implementation tries to show you how to write a flexible and easy to extend evaluator working in less than exponential time using the [Shunting Yard algorithm](http://en.wikipedia.org/wiki/Shunting-yard_algorithm).
Scala is a very expressive language and the code tries to be self documenting. Therefore together with this text you should get a speedy overview about the required steps and algorithm.

> Our main goal is an educational one. This code is not trimmed on performance and reimplements some functionality of the scala libraries. Have a look at [Parser and Combinator](http://www.scala-lang.org/api/2.11.5/scala-parser-combinators/#package) of scala-lang.org.


Tokens
------

As educated humans we automatic build semantic groups out of an expression like `12 +  2*4` to `Number(12),Whitespace,Plus,Whitespace,Number(2),Times,Number(4)`.
A semantic group like this is what we call a `token`.
Now have a look at [Tokens.scala](./src/main/scala/Tokens.scala) and [StdTokens.scala](./src/main/scala/StdTokens.scala).

Tokenizer
---------

We give our computer an expression as an String. So the computer has to build tokens out of loose characters - thats the job of the Tokenizer specified in [TokenizerSpec.scala](./src/test/scala/TokenizerSpec.scala).
The Tokenizer uses Parsers for finding tokens. A Parser in our usecase is a function trying to find a specific token at the beginning of a String.
Usually a parser uses regual expressions and can be combined with combinators defined in [Combinators.scala](./src/main/scala/util/Combinators.scala) with the idea borrowed from [scala-lang.org's Combinators](https://wiki.scala-lang.org/display/SW/Parser+Combinators--Getting+Started).
Not all tokens are interesting for us. In the example above we don't need the `Whitespace` token.
Therefore our [Tokenizer.scala](./src/main/scala/Tokenizer.scala) takes a parser for the usefull tokens like numbers, operators, parenthesis and one for useless ones like whitespaces or comments which will be dropped, you'll find some parsers in [StdParsers.scala](./src/main/scala/StdParsers.scala).

Infix and Postfix
-----------------

We usualy use infix notation for mathematic expressions `1 + 2`. This means the operator is between its operands. For evaluation we cant simply go from left to right, we have to use parenthesis and additional knowledge to control precedence (eg. `multiplication before addition`, `parenthesis from inner to outer` ) and associativity (eg. `3-2-1` equals `(3-2)-1` but not `3-(2-1)`).
In contrast the postfix notation, also called [reversed polish notation](http://en.wikipedia.org/wiki/Reverse_Polish_notation), doesnt need anything like parenthesis or knowledge to evaluate it correctly. You read from left to right, if you find an operator token for n operands you take the n tokens at the left and replace these tokens with the result and proceed.
```
1+2*3 (Infix)
Integer(1),Plus,Integer(2),Star,Number(3) (Token Infix)

1 2 3 * + (Postfix)
Integer(1),Integer(2),Number(3),Star,Plus (Token Postfix)

Evaluate Infix:
1+2*3 = 1+(2*3) = 1+6 = 7

Evalutate Postfix:
1 2 3 * + = 1 6 + = 7
```

Evaluate postfix expression
---------------------------

Because we just need the tokens and the implementation of the operations and no other custom evaluation rules postfix is superb for our evaluation of the tokens like you can see in [PostfixEvaluatorsSpec.scala](./src/test/scala/PostfixEvaluatorsSpec.scala).
The postfix evaluator [PostfixEvaluators.scala](./src/main/scala/PostfixEvaluators.scala) visits the input token from left to right.
If the token is an literal it is a future operand and gets pushed on a stack. If the token is an operation a lookup for the implementation of this operator happens and will be executed.
We implement a operation with a function which get's the operand stack and the remaining input token as parameter and return the updated operand stack and a number representing how many of the remaining input tokens will be skipped.
A classic binary operation like the addition will pop two items from the operand stack, checks whether they are integers, adds them and pushes the result back on the stack.
This way leads to very flexible possible operations, but leaves a lot of work like error handling. Therefore you can see a helper function for binary operations and some operator implementations in [StdOperators.scala](./src/main/scala/StdOperators.scala).

Shunting Yard algorithm
-----------------------

Like mentioned above infix is more common to us and you probably dont want to be as rude as [HP](http://en.wikipedia.org/wiki/HP-10C_series) and forcing Postfix input only, so we have to change the infix token input from out Tokenizer to a Postfix Token output for out PostfixEvaluator. We close this gap with the Shunting Yard algorithm invented by Dijkstra for exactly this purpose.
Get a feeling what it does with the specification [ShuntingYardSpec.scala](./src/test/scala/ShuntingYardSpec.scala).
We visit each token in the infix notation from left right and hold information about the precedence (eg. addition has lower precedence than multiplication) and associativity (LEFT,RIGHT) of operator token. We fill a queue with the token in postfix order and buffer operators on an stack till we can add to the queue which happens of course if its operands got already added.
```
Read a token.
	the token is a number, then add it to the output queue.
	If the token is an operator, o1, then:
		while there is an operator token, o2, at the top of the operator stack, and either
	    	o1 is left-associative and its precedence is less than or equal to that of o2, or
        	o1 is right associative, and has precedence less than that of o2,
        then pop o2 off the operator stack, onto the output queue;
		push o1 onto the operator stack.
	If the token is a left parenthesis, then push it onto the stack.
	If the token is a right parenthesis:
		Until the token at the top of the stack is a left parenthesis, pop operators off the stack onto the output queue.
		Pop the left parenthesis from the stack, but not onto the output queue.
		If the stack runs out without finding a left parenthesis, then there are mismatched parentheses.
When there are no more tokens to read:
	While there are still operator tokens in the stack:
		If the operator token on the top of the stack is a parenthesis, then there are mismatched parentheses.
		Pop the operator onto the output queue.
Exit.
```
(pseudocode from [Wikipedia:ShuntingYard](http://en.wikipedia.org/wiki/Shunting-yard_algorithm#The_algorithm_in_detail))
Have a look at [ShuntingYard.scala](./src/main/scala/ShuntingYard.scala) to see the real implementation, which follows directly the pseudocode above.

Put it all together
---------------

Now that we fullfilled all the single steps from tokenizing, shunting to evaluating we can come back to our motivation at the beginning. [StdExpressionEvaluators.scala](./src/main/scala/StdExpressionEvaluators.scala) fills the Tokenizer, ShuningYard and PostfixEvaluator with some basic mathematic token, parser and implementation and evaluates therefore mathematic expressions with if-branches.
With a implicit class it's easy on the eyes like you see in [StdExpressionEvaluatorsSpec.scala](./src/test/scala/StdExpressionEvaluatorsSpec.scala).

> This implementation is flexible an should cover a lot more use cases than simply mathematic expressions.

APPENDIX
========


If-then-else implementation
------------

We handle `if`,`then`,`else` each like a unary operator with low precedence.
If the parameter of `if` is not equal to 0, it doesnt nothing more than removing the parameter from the stack.
Therefore the code of the `then` branch should follow.
But if the parameter of `if` is equal to 0 it searches for the `then` token and let the input token skip till after `then` - what follows is of course the code of the `else` branch.
The `then` operator skips the code of the following `else` branch.
See [StdOperators.scala](./src/main/scala/StdOperators.scala) for an implementation.

Error handling
-----------

The postfix evaluator and the shunting yard implementation use the [Try class](http://www.scala-lang.org/files/archive/nightly/docs/library/index.html#scala.util.Try) heavily.
This gives the power to return error descriptions and creates nice self documenting code.
The Tokenizer lacks a bit of error handling and has just the possibility to return `Error` token, but this should be good enough for this approach.







