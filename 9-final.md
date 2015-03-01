[1]: http://en.wikipedia.org/wiki/LALR_parser

## Final project

##### Task

There are 4 parts to the final project.

1. Choose a topic by 13.02.2015.
2. Investigate the topic.
3. Write Scala code to achieve the goal of the topic.
4. Document your code with comments or with a separate essay
   so that an average Scala programmer can understand your
   topic.

##### Grading

Your performance will be evaluated according to the following
criteria:

* How difficult the topic is,
* How much success you have achieved,
* How easy it is to learn about the topic from your documentation.

##### Topic list

You may choose one of the following topics to investigate.
Each sample topic may come with a reference list. The papers
are there for inspiration; you need *not* implement everything
described therein.

* A topic **you** come up with, as long as it is related to parsing.
  Please email me a short description of the topic and a link
  to an article about the topic for approval by 13.02.2015.

* **Mixfix parsing**: The user specifies the precedence and
  associativity of operators; the system should generate
  a parser. Must support operators with 3 or more operands
  (e. g., if-then-else).

  - Simon Peyton Jones. Parsing distfix operators.
    *Communications of the ACM* 29(2), 1986.

  - Nils Anders Danielsson and Ulf Norell.
    Parsing mixfix operators.
    Available on author's homepage.

* **Shunting-yard algorithm**: Implement Dijkstra's shunting-yard
  algorithm to support operators of any precedence and associativity.
  Extend the algorithm for operators with 3 or more operands
  (e. g., if-then-else).

* **Linear-time parsers**: We have been using a grammar interpreter
  that takes exponential time in the worst case. Investigate
  traditional parsing algorithms such as [LALR][1]. Write
  a grammar interpreter that runs in time linear in the input
  code size. A simple tokenizer is probably necessary.

* **Bidirectional grammar transformation** (original research):
  You have written `simplifyAE` many times to turn syntax trees close
  to the source code into syntax trees close to the meaning
  of the program. It is time to automate the process. Design
  a domain-specific language to describe transformation between
  grammars. Compile that language into methods to convert between
  syntax trees of the grammars before and after the transformation.

  (This could develop into a Bachelor's thesis topic.)
