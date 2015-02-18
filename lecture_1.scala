// //////////////
// Programming paradigms
// //////////////

Programming paradigms
  1) Imperative programming
    All about:
      modifying mutable variables,
      using assignment,
      and control structures such as if-then-else, loops, break, continue, return
  2) Functional programming
    In a restricted sense:
      Programming without mutable variables, assignments, loops, and other imperative control structures
    In a wider sense:
      Focus on the functions
    In particular, functions can be values that are produced, consumed and composed
    All of this should become easier in a functional language
  3) Logic programming
    Rare
  4) Object oriented programming
    Orthoganal to the others?
    i.e. can be integrated into any of the other three


// //////////////
// Mathematical foundations of functional programming
// //////////////

What is a mathematical theory?
  - 1+ data types
  - operations on these types
  - laws that descrbe the realtionships btwn values and operations
    ---> normally a thoery does not describe mutations!
      - changing something while keeping the identity of the thing the same


Theories without mutation
  e.g. the theory of polynomials defines the sum of two polynomials by laws such as:

(a*x + b) + (c*x + d) = (a+c)*x + (b+d)

  But it doesn't define an operator to change a coefficient while keeping the polynomial the same!
  In imperative programming, you CAN do this:

class Polynomial { double[] coefficient; }
Polynomial p = . . . ;
p.coefficient[0] = 42;

  Another example, the theory of strings
    The theory of strings defines a concatenative operator ++ which is associative:

(a ++ b) ++ c = a ++ (b ++ c)

  But it doesn't define an operator to change a sequence element while keeping the sequence the same!
    Some langauges get this right, i.e. Java and Python both have immutable strings


So, if we want to implement high-level concepts following their mathematical theories, there's no place for mutation

Therefore, let's:
  1) Concentrate on defining theories for operators expressed as functions
  2) Avoid mutations
  3) Have powerful ways to abstract and compose functions


// //////////////
// FP languages
// //////////////

Functional programming languages:
  In a restricted sense:
    Doesn't have mutable variables, assignments, loops, and other imperative control structures
  In a wider sense:
    Enables the contruction of elegant programs that focus on functions
  In particular, functions in a FP language are first class citizens, meaning:
    They can be defined anywhere, including inside other functions
    Like any other value, can be passed as params to functions, and returned as results
    Like any other value, there exists a set of operators to compose functions

Examples:
  Restricted:
    Pure List, XSLT, XPath, XQuery, FP
    Haskell (without I/O Monad or UnsafePerformIO)
  Wider:
    Lisp, Scheme, Raket, Clojure
    SML, Ocaml, F#
    Haskell (full language)
    Scala
    Smalltalk, Ruby, JS (generally counted as OO langs, but still can be pretty functional)


// //////////////
// Elements of Programming
// //////////////

Every non-trivial prog. lang. provides:
  1) Primitive expressions representing the simplest elements
  2) Ways to combine expressions
  3) Ways to abstract expressions, which introduce a name for an expression by which it can then be referred to


// //////////////
// Evaluation of operators and names
// //////////////

Evaluation:
  Say we have the following code:

def pi = 3.14159
def radius = 10
(2 * pi) * radius

  How is the last line evaluated?
    Evaluating non-primitive expressions:
      1) Take the leftmost operator
      2) Evaluate its operands (left before right)
      3) Apply the operator to the operands
    A name is evaluated by replacing it with the righthand side of the definition
      - the evaluation process stops once it reaches a value (for example, a number)

(2 * pi) * radius
(2 * 3.14159) * radius
6.28318 * radius
6.28318 * 10
62.8318


// //////////////
// Parameters
// //////////////

Definitions can have parameters, e.g.

def square(x: Double): Double = x * x
// Note that the return type isn't necessary here, could write:
// def square(x: Double): Double = x * x
// The function param types ARE required, though
square(2)
square(5+4)
square(square(4))

def sumOfSquares(x: Double, y: Double): Double = square(x) + square(y)
sumOfSquares(5,4)


// //////////////
// Evaluation of Function Applications
// //////////////

Applications of parameterized functions are evaluated in a similar way to operators
  1) Evaluate all function args, from left to right
  2) Replace the function application by the functions right hand side, and, at the same time...
  3) Repalce the formal parameters of the function by the actual arguments

sumOfSquares(3, 2+2)
sumOfSquares(3, 4)
square(3) + square(4)
3 * 3 + square(4)
9 + square(4)
9 + 4 * 4
9 + 16
25

This is called the "substitution model" of evaluation
  Basically, you just reduce expressions to values
  Can be applied to all expressions, as long as they have no side effects

Side effects
  In code like:

x = 10
x += 1

  The x += 1 part is a side-effect
    It has an effect on x, you can't just reduce it to a value
      Need to have a concept of stores of values to express this
  Ruling out side-effects lets us stick to a simple model of evaluation

The substitution model is formalized in the λ-calculus
  Gives a foundation for functional programming

Do all functions reduce to a value?
  No!

def loop: Int = loop
loop
loop
loop
...

  What happens here is that we define loop, then call it
    So, we should replace the name loop with the right side of the definition
    This yields loop
    Then we replace loop with the right side of the definition...
      And on forever


// //////////////
// Call-by-value evaluation strategy
// //////////////

Reduce function arguments to values, then apply function to reduced arguments
  - Advantage: every function argument evaluated only once

sumOfSquares(3, 2 + 2)
sumOfSquares(3, 4)
square(3) + square(4)
3 * 3 + square(4)
9 + square(4)
9 + 4 * 4
9 + 16
25


// //////////////
// Call-by-name evaluation strategy
// //////////////

Apply function to unreduced arguments, then reduce arguments
  - Advantage: function argument isn't evaluated if the corresponding parameter is unused in the evaluation of the function body

sumOfSquares(3, 2 + 2)
square(3) + square(2 + 2)
3 * 3 + square(2 + 2)
9 + square(2 + 2)
9 + (2 + 2) * (2 + 2)
9 + 4 * (2 + 2)
9 + 4 * 4
25

Both strategies reduce to the same final values as long as:
  - the reduced expression consists of pure functions, and
  - both evaluations terminate


// //////////////
// Comparison
// //////////////

Given:

def test(x: Int, y: Int) = x * x
// Note: y is not used at all, it's x * x

Which evaluation strategy is faster for?

// test(2, 3)
CBV
  test(2, 3)
  2 * 2
  4
CBN
  test(2, 3)
  2 * 2
  4
--> tie

// test(3+4, 8)
CBV
  test(3+4, 8)
  test(7, 8)
  7 * 7
  49
CBN
  test(3+4, 8)
  (3+4) * (3+4)
  7 * (3+4)
  7 * 7
  49
--> CBV wins

// test(7, 2*4)
CBV
  test(7, 2*4)
  test(7, 8)
  7 * 7
  49
CBN
  test(7, 2*4)
  7 * 7
  49
--> CBN wins

// test(3+4, 2*4)
CBV
  test(3+4, 2*4)
  test(7, 2*4)
  test(7, 8)
  7 * 7
  49
CBN
  test(3+4, 2*4)
  (3+4) * (3+4)
  7 * (3+4)
  7 * 7
  49
--> tie


// //////////////
// CBV, CBN and termination
// //////////////

If both evaluations terminate, CBV and CBN reduce to the same value
  - but what if termination isn't guaranteed?
    - if CBV evaluation of an expression e terminates, then CBN evaluation of e terminates, too
    - the other direction is not true (CBN termination does not imply CBV termination)

// Example: terminates under CBN but not CBV
def first(x: Int, y: Int) = x
first(1, loop)
// loop is just some non-terminating computation

CBN:
first(1,loop)
1

CBV:
first(1,loop)
first(1,loop)
...

CBN ignores loop, while CBV tries to reduce loop, but loop doesn't reduce


Scala's evaluation strategy:
  - Scala normally uses CBV
    - it's generally much faster, even though it terminates less often
    - plays nicer with imperative effects and side-effects
  - BUT if the type of a function parameter starts with =>, it uses CBN

// Example:
def constOne(x: Int, y: => Int) = 1

// First parameter is CBV, so we have to reduce it first
constOne(1+2, loop)
constOne(3, loop)
1
// We didn't have to reduce loop, because it's CBN, and wasn't actually used
//  We can simply reduce constOne(3, loop) to 1

// What if it was all CBV?
constOne(1+2, loop)
constOne(3, loop)
constOne(3, loop)
constOne(3, loop)
...
// We keep trying to reduce loop to a value, but it never reduces

The => is part of the type definition
  => Int means that the parameter is type int, but will be passed CBN


// //////////////
// Conditionals
// //////////////

if/else in Scala is used for EXPRESSIONS, not STATEMENTS
  For example, to define an absolute value function:

def abs(x: Int): Int = if (x >= 0) x else -x
println(abs(10))
println(abs(-10))

Here the (x >= 0) part is an expression of type Boolean
  Sometimes we call these predicates

Boolean expressions can be composed of:
true, false           // constants
!b                    // negation
b && b                // conjunction
b || b                // disjunction
e <= e, e >= e, etc.  // comparison operations

Rewrite rules (i.e. substitution model) for Booleans:
!true                 // false
!false                // true
true && e             // e --> note e is just some other expression
false && e            // false --> e not evaluated!
true || e             // true --> e not evaluated!
false || e            // e

Note that && and || don't always need their right operand to be evaluated.  We say these expressions "short-circuit evaluation"
  - examples: false && e, true || e


Rewrite rules for if-else:

if (b) e1 else e2

Depends on the value of b!
if (true) e1 else e2 --> e1
if (false) e1 else e2 --> e2


// //////////////
// Value definitions
// //////////////

We already know that function parameters can by CBV or CBN
  - the same is true for value definitions!
  - def form is "by-name", it's right hand side is evaluated on each use
  - val form is "by-value"

var x = 2
val y = x * x
def yy = x * x

y and yy both evaluate to 4
  - with the val definition, y is set to a value immediately, and only once
  - with the def definition, yy is simply set to x * x
  - can be important with non-terminating functions

def loop: Boolean = loop

def x = loop
// this is fine, we've just assigned something to x
// loop isn't evaluated until we actually call x

val x = loop
// here we evaluate immediately
// this is not OK, we're in an infinite loop


// //////////////
// Challenge
// //////////////

Write functions and(x,y) and or(x,y)
  - don't use && and ||

// Remember:
true && e             // e --> note e is just some other expression
false && e            // false --> e not evaluated!
true || e             // true --> e not evaluated!
false || e            // e

// So for and(x,y), if x is true it should be y
// If x is false it should be false, AND y SHOULDN'T BE EVALUATED
def and(x: Boolean, y: => Boolean): Boolean = if (x) y else false

// So for or(x,y), if x is true it should be true, AND y SHOULDN'T BE EVALUATED
// If x is false it should be y
def or(x: Boolean, y: => Boolean): Boolean = if (x) true else y

IMPORTANT NOTE:
  - recursive functions need an explicit return type
  - non-recursive functions do not (it's optional)


// //////////////
// Challenge
// //////////////

Define a sqrt function with this signature:

def sqrt(x: Double): Double = ...

The classical way to acheive this is by successive approximations using Newton's method
  - To compute sqrt(x)
    - Start with an initial estimate of y (any positive number)
    - Repeatedly improve the estimate by taking the mean of y and x/y

Example:
sqrt(2)

Estimate    Quotient              Mean
1           2 / 1 = 2             (1 + 2) / 2 = 1.5
1.5         2 / 1.5 = 1.3333      (1.5 + 1.3333) / 2 = 1.4167
1.4167      2 / 1.4167 = 1.4118   (1.4167 + 1.4118) / 2 = 1.4142


A rough idea for the implementation could look something like:

def sqrtIter(guess: Double, x: Double): Double = 
  if (isGoodEnough(guess, x)) guess
  else sqrtIter(improve(guess, x), x)

// Note that this is a recursive function

FOR CHALLENGE SOLUTION, SEE:
~/IdeaProjects/my-progfun-lecture-notes/src/main/scala/week1/session.sc

// The final code looks like this:


def abs(x: Double): Double = if (x >= 0) x else -x

def sqrtIter(guess: Double, x: Double): Double =
  if (isGoodEnough(guess, x)) guess
  else sqrtIter(improve(guess, x), x)

def isGoodEnough(guess: Double, x: Double): Boolean =
  abs(guess * guess - x) < 0.001 * x

def improve(guess: Double, x: Double): Double =
  (guess + x / guess) / 2

def sqrt(x: Double): Double = sqrtIter(1.0, x)


// //////////////
// Blocks and lexical scope
// //////////////

With the above code, the first 4 functions are just part of the implementation of sqrt
  - we really don't want them polluting the name-space

Let's pacakge them all in sqrt!
  - we can do this with nested functions


def sqrt(x: Double): Double = {

  def abs(x: Double): Double = if (x >= 0) x else -x

  def sqrtIter(guess: Double, x: Double): Double =
    if (isGoodEnough(guess, x)) guess
    else sqrtIter(improve(guess, x), x)

  def isGoodEnough(guess: Double, x: Double): Boolean =
    abs(guess * guess - x) < 0.001 * x

  def improve(guess: Double, x: Double): Double =
    (guess + x / guess) / 2

  sqrtIter(1.0, x)
}


Now there's just a single external function
  - Everything else is contained within the sqrt function
  - Also, note how the return value just comes last :)

We did this with a BLOCK
  - delimited by braces {}
  - it contains a squence of definitions or expressions
  - the last element of a block is an expression that defines its value
  - this return expression can be preceeded by auxilary function, as above
  - blocks are themselves expressions, and can appear anywhere expressions can
    - like on the right side of a def!

Blocks and lexical scope
  - defs inside a block are ONLY VISIBLE IN THE BLOCK
  - defs inside a block SHADOW defs of the same names outside the block


// //////////////
// Challenge
// //////////////

// What does result eval to?

val x = 0
def f(y: Int) = y + 1
val result = {
  val x = f(3)
  x * x
} + x

// Outside the block, x is 0
// Inside the block, x is 4
// So we have:
//  result = {4 * 4} + 0
//         = 16


// //////////////
// Clean up sqrt(x,y)
// //////////////

In our sqrt function, we use x a lot, but we never change the value
  - so, let's just define it once!
  - from then on, we can just refer to x, it doesn't have to be an arg of the inner functions
    - except abs?  Because we need it to be able to take different args each time?


def sqrt(x: Double): Double = {

  def abs(y: Double): Double = if (y >= 0) y else -y

  def sqrtIter(guess: Double): Double =
    if (isGoodEnough(guess)) guess
    else sqrtIter(improve(guess))

  def isGoodEnough(guess: Double): Boolean =
    abs(guess * guess - x) < 0.001 * x

  def improve(guess: Double): Double =
    (guess + x / guess) / 2

  sqrtIter(1.0)
}


And we don't actually need to specify return types in any non-recursive functions


def sqrt(x: Double) = {

  def abs(y: Double) = if (y >= 0) y else -y

  def sqrtIter(guess: Double): Double =
    if (isGoodEnough(guess)) guess
    else sqrtIter(improve(guess))

  def isGoodEnough(guess: Double) =
    abs(guess * guess - x) < 0.001 * x

  def improve(guess: Double) =
    (guess + x / guess) / 2

  sqrtIter(1.0)
}


// //////////////
// Semi-colons in Scala
// //////////////

Generally optional, but can be used if you want to separate multiple statements on one line
  - the following are all valid

val x = 10
val x = 10;
val x = 10; val y = 20
val x = 10; val y = 20;

So how do you write multi-line expressions in Scala?
For example, this would be 1 expression, not 2:


someLongExpression
+ someOtherExpression


Two options that will both work:

1) Parens

(someLongExpression
+ someOtherExpression)


2) Line finishes with infix operator

someLongExpression +
someOtherExpression


// //////////////
// Rewriting with functions
// //////////////

First, let's review function application:
  1) First, evaluate expressions in the params e1 ... en, resulting in the values v1 ... vn
  2) Replace the application with the body of the function f, in which
  3) The actual params v1 ... vn replace the formal params

This can be formalized as a rewriting of the program itself
  Say we have:

def f(x1, ..., xn) = B; ... f(v1, ..., vn)

  This can be rewritten:

def f(x1, ..., xn) = B; ... [v1/x1, ..., vn/xn]B

  This bit:
    [v1/x1, ..., vn/xn]B
  Means "the expression B, in which all occurences of xi are replaced by vi"
    Note how v1, the subbed in val, is put first

  [v1/x1, ..., vn/xn] is called a "substitution"


// //////////////
// Rewriting example
// //////////////

Consider gcd, the function that comutes the greatest common divisor of two numbers
  Here's an implementation using Euclid's algo:


def gcd(a: Int, b: Int): Int =
  if (b == 0) a else gcd(b, a % b)


gcd(14, 21)
--> if (21 == 0) 14 else gcd(21, 14 % 21)
--> if (false) 14 else gcd(21, 14 % 21)
--> gcd(21, 14 % 21)
--> gcd(21, 14)
--> if (14 == 0) 21 else gcd(14, 21 % 14)
--> if (false) 21 else gcd(14, 21 % 14)
--> gcd(14, 21 % 14)
--> gcd(14, 7)
--> if (7 == 0) 14 else gcd(7, 14 % 7)
--> if (false) 14 else gcd(7, 14 % 7)
--> gcd(7, 14 % 7)
--> gcd(7, 0)
--> if (0 == 0) 7 else gcd(0, 7 % 0)
--> if (true) 7 else gcd(0, 7 % 0)
--> 7


Now consider a classical algo for factorial:


def factorial(n: Int): Int =
  if (n == 0) 1 else n * factorial(n-1)


factorial(4)
--> if(4 == 0) 1 else n * factorial(4-1)
--> if(false) 1 else n * factorial(4-1)
--> 4 * (factorial(4-1))
--> 4 * (factorial(3))
--> 4 * (if(3 == 0) 1 else n * factorial(3-1))
--> 4 * (if(false) 1 else n * factorial(3-1))
--> 4 * (3 * factorial(3-1))
--> 4 * (3 * factorial(2))
->> 4 * (3 * (2 * factorial(1)))
->> 4 * (3 * (2 * (1 * factorial(0))))
->> 4 * (3 * (2 * (1 * 1)))
->> 120


gcd is tail recursive, factorial is not!
  gcd oscilates, ends up in repeated calls to gcd
    expression doesn't get bigger, just cycles
  factorial actually gets bigger over time

Significance:
  If a function calls itself as its last action, the functions stack frame can be reused
    Tail recursive functions are iterative processes
    We really just need one stack frame that we can re-use
      Such calls are called tail-calls
  Basically, without tail recursion we have to keep all the intermediate results
    With tail recursion, we don't need to

More general:
  Functions can call not just themselves, but also other functions
    If the last action of a function consists of calling a function (itself OR another function), once stack frame is sufficient for both functions.  Such calls are called tail-calls

You can actually get the compiler to check if its tail recursive:


@tailrec
def gcd(a: Int, b: Int): Int =
  if (b == 0) a else gcd(b, a % b)
// This would work fine

@tailrec
def factorial(n: Int): Int =
  if (n == 0) 1 else n * factorial(n-1)
// This would throw an error


Even though tail recursion is more efficient, non-tailrec is fine
  Just go for tailrec if it's going to be really deep recursion
  Otherwise, that's premature optimization


For fun, let's design a tail recusive version of factorial

def factorial(n: Int): Int = {
  def loop(acc: Int, n: Int): Int =
    if (n == 0) acc
    else loop(acc * n, n-1)
  loop(1, n)
}

Explained:
  We'll be returning acc, which will start at 1, and then be multiplied by n, n-1, n-2, etc.
  So, with every iteration, we just have to multiply loop by the current value of n, and then re-call with n one lower
  We stop when n gets to zero, and just return acc
