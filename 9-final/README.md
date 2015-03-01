Shunting Yard
=============

Abstract
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
Now have a look at [Tokens.scala](./src/main/scala/Tokens.scala).

Tokenizer
---------

We give our computer an expression as an String. So the computer has to build tokens out of loose characters - thats the job of the Tokenizer. The Tokenizer uses Parsers for finding tokens.
A Parser in our usecase is a function trying to find a specific token at the beginning of a String.
Usually a parser uses regual expressions and can be combined with combinators ( [Combinators.scala](./src/main/scala/util/Combinators.scala) ).
Not all tokens are interesting for us. In the example above we dont need the `Whitespace` token.
Therefore our [Tokenizer.scala](./src/main/scala/Tokenizer.scala) takes a parser for the usefull tokens like numbers, operators, parenthesis and one for useless ones like whitespaces or comments and  [TokenizerSpec.scala](./src/test/scala/TokenizerSpec.scala).

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
Because we just need the tokens and the implementation of the operations and no other custom evaluation rules postfix is superb for our evaluation of the tokens. Have a look at [PostfixEvaluators.scala](./src/main/scala/PostfixEvaluators.scala) which uses a stack to hold all possible operands left to the current token and [PostfixEvaluatorsSpec.scala](./src/test/scala/PostfixEvaluatorsSpec.scala).

Shunting Yard algorithm
-----------------------

Like mentioned above infix is more common to us and you probably dont want to be as rude as [HP](http://en.wikipedia.org/wiki/HP-10C_series) and forcing Postfix input only, so we have to change the infix token input from out Tokenizer to a Postfix Token output for out PostfixEvaluator. We close this gap with the Shunting Yard algorithm invented by Dijkstra for exactly this purpose.
We visit each token in the infix notation from left right and hold information about the precedence (eg. number) and associativity (LEFT,RIGHT) of operator token. We fill a queue with the token in postfix order and buffer operators on an stack till we can add to the queue which happens of course if its operands got already added.
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
		If the token at the top of the stack is a function token, pop it onto the output queue.
		If the stack runs out without finding a left parenthesis, then there are mismatched parentheses.
When there are no more tokens to read:
	While there are still operator tokens in the stack:
		If the operator token on the top of the stack is a parenthesis, then there are mismatched parentheses.
		Pop the operator onto the output queue.
Exit.
```
(source: [Wikipedia:ShuntingYard](http://en.wikipedia.org/wiki/Shunting-yard_algorithm#The_algorithm_in_detail))

Have a look at the [ShuntingYard.scala](./src/main/scala/ShuntingYard.scala) and [ShuntingYardSpec.scala](./src/test/scala/ShuntingYardSpec.scala)





