// //////////////
// Higher Order Functions
// //////////////

Functional langs treat functions as 1st class values
  Like any other value, can be:
    - passed as a param
    - returned as a result
  Functions that do this (take funcs as params or return as results) are called HIGHER ORDER FUNCTIONS
    - the opposite of first order functions, that act only on simple data types


// //////////////
// Example:
// //////////////

// Want to take the sum of the ints btwn a and b, including a and b

def sumInts(a: Int, b: Int): Int = {
  if (a > b) 0
  else a + sumInts(a + 1, b)
}
sumInts(2,4)
// 2+3+4 == 9

// How about sum of the CUBES of all the ints btwn a and b

def cube(x: Int): Int = x * x * x

def sumCubes(a: Int, b: Int): Int = {
  if (a > b) 0
  else cube(a) + sumCubes(a + 1, b)
}
sumCubes(2,4)
// 8+27+64 == 99

// Now how about the sum of the factorials btwn a and b?

def fact(x: Int): Int = {
  if (x == 1) 1
  else x * fact(x-1)
}

def sumFactorials(a: Int, b: Int): Int = {
  if (a > b) 0
  else fact(a) + sumFactorials(a + 1, b)
}
sumFactorials(2,4)
// 2+6+24 == 32

// ---> REALLY, THIS IS ALL THE MATHEMATICAL NOTATION:

//  b
//  ∑ f(n)
// n=a

// If math can make this generalization, can Scala too?

// First, define our higher order function
def sum(f: Int => Int, a: Int, b: Int): Int =
  if (a > b) 0 else f(a) + sum(f, a + 1, b)

// Then, our specific functions to use with sum
def id(x: Int): Int = x
def cube(x: Int): Int = x * x * x
def fact(x: Int): Int = if (x == 1) 1 else x * fact(x-1)

def sumInts(a: Int, b: Int): Int = sum(id, a, b)
def sumCubes(a: Int, b: Int): Int = sum(cube, a, b)
def sumFactorials(a: Int, b: Int): Int = sum(fact, a, b)

sumInts(2,4) == 9
sumCubes(2,4) == 99
sumFactorials(2,4) == 32


// //////////////
// Anonymous functions
// //////////////

In the above, it kind of sucked having to name all the functions before using them
  - this isn't the same with other values
  - for example, you don't have to:

def str = "abc"; println(str)

  - you can just:

println("abc")

  - shouldn't we be able to do the same with functions?
    - literal functions are called anonymous

Examples:
  - anonymous cube function:

(x: Int) => x * x * x

  - anonymous summing function:

(x: Int, y: Int) => x + y

Note that these are just SYNTACTIC SUGAR
  - Can always re-write:

(x1: T1, ..., xn: Tn) => E

  - As:

def f(x1: T1, ..., xn: Tn) = E; f

So defining the function, then calling it
  - as long as f is a fresh name


// //////////////
// Anonymous functions example
// //////////////

def sum(f: Int => Int, a: Int, b: Int): Int =
  if (a > b) 0 else f(a) + sum(f, a + 1, b)

def sumInts(a: Int, b: Int): Int = sum((x: Int) => x, a, b)
def sumCubes(a: Int, b: Int): Int = sum((x: Int) => x * x * x, a, b)

sumInts(2,4) == 9
sumCubes(2,4) == 99

// Or, as short as possible
//   - compiler can infer the type of x, and of the main function

def sumInts(a: Int, b: Int) = sum(x => x, a, b)
def sumCubes(a: Int, b: Int) = sum(x => x * x * x, a, b)

sumInts(2,4) == 9
sumCubes(2,4) == 99

*** STYLE NOTE ***
  - the factorial bit is probably too much to jam into an anonymous function, for the sake of readability


// //////////////
// Challenge
// //////////////

// Write a tail-recursive sum function
def sum(f: Int => Int)(a: Int, b: Int): Int = {
  def loop(a: Int, acc: Int): Int = {
    if (a > b) acc
    else loop(a + 1, f(a) + acc)
  }
  loop(a, 0)
}

def sumInts(a: Int, b: Int) = sum(x => x)(a, b)
def sumCubes(a: Int, b: Int) = sum(x => x * x * x)(a, b)

sumInts(2,4) == 9
sumCubes(2,4) == 99


// //////////////
// Currying - motivation
// //////////////

A special form for writing higher order functions

Let's remember our summartion functions:

def sumInts(a: Int, b: Int) = sum(x => x, a, b)
def sumCubes(a: Int, b: Int) = sum(x => x * x * x, a, b)
def sumFactorials(a: Int, b: Int) = sum(fact, a, b)

Note the a and b get passed unchanged from sumInts and sumCubes into sum
  Can we be even shorter by getting rid of these params?
  Let's re-write sum as follows:
    Note that is ONLY TAKE f AS A PARAM, vs. f, a and b
    It returns a function that takes two ints and returns 1

def sum(f: Int => Int): (Int, Int) => Int = {
  def sumF(a: Int, b: Int): Int =
    if (a > b) 0
    else f(a) + sumF(a + 1, b)
  sumF
}

  Now, to define these functions, we no longer need to specify the params:

def sumInts = sum(x => x)
def sumCubes = sum(x => x * x * x)
def sumFactorials = sum(fact)

  Now we can do things like:

sumCubes(2,4)

  Can we avoid the middlemen though?
    i.e. no sumCubes function?
      Yes!

sum(x => x * x * x)(2, 4)

  The first part:
    sum(x => x * x * x)
  Returns a function of type (Int, Int) => Int
    This can then be applied to the args (2, 4)

Basically, function application assoicates to the left
  So, this is true:

sum(cube)(2,4) == (sum(cube))(2,4)


// //////////////
// Currying - syntax
// //////////////

Before, to define a function that returns a function, we wrote:

def sum(f: Int => Int): (Int, Int) => Int = {
  def sumF(a: Int, b: Int): Int =
    if (a > b) 0
    else f(a) + sumF(a + 1, b)
  sumF
}

But, Scala also lets us simply write:

def sum(f: Int => Int)(a: Int, b: Int): Int =
  if (a > b) 0
  else f(a) + sum(f)(a + 1, b)


// //////////////
// Expansion of functions with multiple param lists
// //////////////

def f(args1)...(args n) = E
// Where n>1, this is equivalent to
def f(args1)...(args n-1) = {def g(args n) = E;g}
// Where g is a fresh identifier.  This is just like what we did with sumF
// Or for short, with an annonymous function
def f(args1)...(args n-1) = (args n => E)

// By repeating the process n times, this
def f(args1)...(args n-1)(args n) = E
// Is equivalent to this
def f = (args1 => (args2 => ...(args n => E)))

// This style of function definition and application
//   With all of the annonymous functions
// Is called currying, after Haskell Curry
//   20th century logician


// //////////////
// Function types
// //////////////

// Function types associate to the RIGHT
//   So the following two are equivalent:
Int => Int => Int
Int => (Int => Int)


// What is the type of sum below?
def sum(f: Int => Int)(a: Int, b: Int): Int = ...
// It is:
(Int => Int) => (Int, Int) => Int

// Remember, function types assocate to the right
//   So first we look at:
(Int, Int) => Int

// This is just a function that takes 2 Ints, and returns an Int
// Now we look at
(Int => Int) => ***function***

// So we see that sum is a function that takes an Int => Int function
//   And returns a function that itself takes 2 Ints, and returns an Int
//   So we also could have written:
(Int => Int) => ((Int, Int) => Int)


// //////////////
// Excercise
// //////////////

1) Write a product function that calculates the product of the values of a function for the points on a given interval
2) Write factorial in terms of product
3) Write a more general function, which generalizes both sum and product

Answers here:
~/IdeaProjects/my-progfun-lecture-notes/src/main/scala/week2/excercise1.sc


// //////////////
// Fixed points of functions
// //////////////

x is a fixed point of f if:
f(x) == x

// For example, for:
def f(x: Int): Int = 1 + x/2
// A fixed point would be:
val x = 2
square(x) == x


For some functions f, we can locate the fixed points by:
  - starting with an initial estimate
  - then applying f in a repetitive way

x, f(x), f(f(x)), f(f(f(x))), ...

  - until the value does not vary (much) anymore


// In code:
val tolerance = 0.0001

def isCloseEnough(x: Double, y: Double): Boolean =
  math.abs((x-y) / x) / x < tolerance

def fixedPoint(f: Double => Double)(firstGuess: Double): Double = {
  def iterate(guess: Double): Double= {
    val next = f(guess)
    if (isCloseEnough(guess, next)) next
    else iterate(next)
  }
  iterate(firstGuess)
}

fixedPoint(x => 1 + x/2)(1)


// //////////////
// Fixed points applied to sqrt
// //////////////

// Here's the specification of the sqrt function:
sqrt(x) = the number y such that y * y = x

// Or, dividing both sides of the equation by y:
sqrt(x) = the number y such that y = x / y

// Therefore:
sqrt(x) is a fixed point of the function (y => x / y)
// If you plug in sqrt(x) for the ys above:
// sqrt(x) = x / sqrt(x)
// x = sqrt(x) * sqrt(x)

// So we think we should be able to define sqrt as:
def sqrt(x: Double) = fixedPoint(y => x / y)(1.0)

// But it doesn't converge!
//   We can see what's happening by adding a println statement
def fixedPoint(f: Double => Double)(firstGuess: Double): Double = {
  def iterate(guess: Double): Double= {
    println("guess = " + guess)
    val next = f(guess)
    if (isCloseEnough(guess, next)) next
    else iterate(next)
  }
  iterate(firstGuess)
}

def sqrt(x: Double) = fixedPoint(y => x / y)(1.0)

sqrt(2)

// What we get
guess: 1.0
guess: 2.0
guess: 1.0
guess: 2.0
...

// Guess just oscilates between 1 and 2!
//   One way to control these oscillations is with AVERAGE DAMPING
//     Average the successive values of the original secquence

// Somehow, we do this with:
def sqrt(x: Double) = fixedPoint(y => (y + x/y) / 2)(1.0)
// I don't fully get this
//   We are taking x/y, our inital way to estimate y, and averaging it with y?

// This is very similar to the sqrt algo we saw last week


// //////////////
// Functions as return vals
// //////////////

Let's think more about what we did above
  - We first observed that the square root of x is a fixed point of the function:
    y => x/y
  - If we keep applying this function with the wrong initial guess, though, we won't converge
  - To converge, we need to average successive values
    - This technique of STABILIZING BY AVERAGING is general enough to be abstracted into its own function

def averageDamp(f: Double => Double)(x: Double) = (x + f(x)) / 2
// Takes a function f and a value x, computes average of x and f(x)

// Challenge: write a sqrt function using fixedPoint and averageDamp
def sqrt = fixedPoint(averageDamp(y => x/y))(1.0)

// So averageDamp is a function that takes a function, and returns a function!


// //////////////
// scala syntax summary
// //////////////

Language elements seen so far:
  - we've seen language elements to express types, expressions and definitions
  - we can give their context-free syntax in Extended Backus-Naur form (EBNF)
    - this means:
      | denotes an alternative
      [...] an option (0 or 1)
      {...} a repetition (0 or more)


--> EBNF grammar for Types:
Type              = SimpleType | FunctionType
FunctionType      = SimpleType '=>' Type
                    | '(' [Types] ')' '=>' Type
SimpleType        = ident
Types             = Type {',' Type}

// This says:
//  - a Type can be a SimpleType or a FunctionType
//  - a FunctionType can be:
//    - SimpleType => Type
//      or
//      (optional Types) => Type
//    - Remember that Type can be SimpleType or FunctionType!
//  - SimpleType is an identity
//  - Types are 1+ Type, separated by commas

- A type can be:
  - a numeric type (Int, Double, Byte, Short, Char, Long, Float)
  - the Boolean type (with vals true and false)
  - the String type
  - a function type, like Int => Int, or (Int, Int) => Int
- Later, we'll see more forms of types


--> EBNF grammar for expressions:
Expr              = InfixExpr
                    | FunctionExpr
                    | if '(' Expr ')' Expr else Expr
InfixExpr         = PrefixExpr
                  | InfixExpr Operator InfixExpr
Operator          = ident
PrefixExpr        ['+' | '-' | '!' | '~'] SimpleExpr
SimpleExpr        = ident | literal | SimpleExpr '.' ident
                  | Block
FunctionExpr      = Bindings '=>' Expr
Bindings          = ident [':' SimpleType]
                  | '(' [Binding {',' Binding}] ')'
Binding           = ident [':' Type]
Block             = '{' {Def ':'} Expr '}'

- An expression can be:
  - an identifier such as x, isGoodEnough
  - a literal, like 0, 1.0, "abc", 'a'
  - a function application, like sqrt(x)
  - an operator application, like -x, y + x
  - a selection, like math.abs
  - a conditional epxression, like if (x  < 0) -x else x
  - a block, like {val x = math.abs(y); x * 2}
  - an annonymous function, like x => x + 1


--> EBNF grammar for definitions:
Def               = FunDef | ValDef
FunDef            = def ident {'(' [Parameters] ')'} [':' Type] '=' Expr
ValDef            = val ident [':' Type] '=' Expr
Parameter         = ident ':' [ '=>' ] Type
Parameters        = Parameter {',' Parameter}

- a definition can be:
  - a function definition, like def square(x: Int) = x * x
  - a value definition, like val y = square(2)
- a parameter can be:
  - a call-by-value parameter, like (x: Int)
  - a call-by-name parameter, like (x: => Int)


// //////////////
// Functions and Data - Rational Numbers Example
// //////////////

We'll learn how functions can create and encapsulate data
  - Example:
    - design a package for doing rational number arithmetic
    - rational number x/y is:
      - x -> numerator, an int
      - y -> denominator, an int

We could do this with:

def addRationalNumerator(n1: Int, d1: Int, n2: Int, d2: Int): Int = ...
def addRationalNumerator(n1: Int, d1: Int, n2: Int, d2: Int): Int = ...

But this would quickly get confusing
  - better to combine the num and denom in a single data structure
  - Can do this by defining a class

class Rational(x: Int, y: Int) {
  def numer = x
  def denom = y
}

This introduces two entities
  - A new type, named Rational
  - A constructor to create elements of this type
  --> Scala keeps the names of types and values in different namespaces
    - so there's no conflict having these two things both called Rational
    - Scala just knows from the context

Objects
  - We call the elements of a class type objects
  - can create them with the new operator

val x = new Rational(1, 2)
x.numer
x.denom

  - we would say that objects of class Rational have two members
    - numer and denom

// Including a add and toString methods
class Rational(x: Int, y: Int) {
  def numer = x
  def denom = y

  def add(that: Rational) =
    new Rational(
      numer * that.denom + that.numer * denom,
      denom * that.denom
    )

  override def toString = numer + "/" + denom
}

val x = new Rational(1,2)
val y = new Rational(4,3)
x.add(y)


// //////////////
// Rational Excercise
// //////////////

// 1) Add a method neg to class Rational
// 2) Add a method sub to subtract two rational numbers
// 3) Calculate:
//   1/3 - 5/7 - 3/2

class Rational(x: Int, y: Int) {
  def numer = x
  def denom = y

  def add(that: Rational) =
    new Rational(
      numer * that.denom + that.numer * denom,
      denom * that.denom
    )

  def neg = new Rational(-numer, denom)

  def sub(that: Rational) = add(that.neg)

  override def toString = numer + "/" + denom
}

val x = new Rational(1, 3)
val y = new Rational(5, 7)
val z = new Rational(3, 2)
x.sub(y).sub(z)


// //////////////
// More fun with Rationals
// //////////////

// Let's divide numer and denom by their gcd!
//   - the general function to find the gcd is:

def gcd(a: Int, b: Int): Int =
  if (b == 0) a else gcd(b, a%b)

// And we can add this as a private method like this:

class Rational(x: Int, y: Int) {
  private def gcd(a: Int, b: Int): Int = if (b == 0) a else gcd(b, a%b)
  private val g = gcd(x, y)
  def numer = x / g
  def denom = y / g
  ...
}

// Let's add comparison methods
//  - less and max
//    - less = this is less than that
//    - max = returns the larger of the two rationals
//  - NOTE THAT WE CAN use this whenever we want
//    - if we refer to a "naked" x, it inherently means this.x 
//    - if I want, can rewrite the whole thing with this everywhere

class Rational(x: Int, y: Int) {
  private def gcd(a: Int, b: Int): Int = if (b == 0) a else gcd(b, a%b)
  private val g = gcd(x, y)
  val numer = x / g
  val denom = y / g

  def add(that: Rational) =
    new Rational(
      this.numer * that.denom + that.numer * this.denom,
      this.denom * that.denom
    )

  def neg = new Rational(-this.numer, this.denom)

  def sub(that: Rational) = this.add(that.neg)

  def less(that: Rational) = this.numer * that.denom < that.numer * this.denom

  def max(that: Rational) = if (this.less(that)) that else this

  override def toString = this.numer + "/" + this.denom
}


// //////////////
// require vs. asset
// //////////////

// Can enforce things like "denom must be positive"
class Rational(x: Int, y: Int) {
  require(y > 0, "denominator must be positive")
  ...
}

// Could also use assert
class Rational(x: Int, y: Int) {
  assert(y > 0, "denominator must be positive")
  ...
}

// The difference is the intent
//  - require is used to enforce a precondition on the caller of a function
//  - assert is used to check the code of the function itself


// //////////////
// Multiple constructors
// //////////////

// What if we want to let ppl just give us a numer, and we assume denom is 1?

class Rational(x: Int, y: Int) {
  def this(x: Int) = this(x, 1)
  ...
}


// //////////////
// Classes and our substitution model of evaluation
// //////////////

// Question 1:
//   - How is the instantiation of the class:
//       new C(e1, ..., en)
//     evaluated?

// Answer:
//   - Evaluate the args e1, ..., em just like the args of a normal function
//   - The resulting expression, say:
//       new C(v1, ..., vm)
//     is already a value!


// Question 2:
//   - Supposed we have the following class definition:
//       class C(x1, ..., xm){... def f(y1, ..., yn) = b ...}
//   - where
//     - the formal params of the class are x1, ..., xm
//     - the class defines a method f with formal params y1, ..., yn
//   - How is the following expression evaluated?
//     - note that we've passed all params
new C(v1, ..., vm).f(w1, ..., wn)

// Answer:
//   - It's rewritten to:
[w1/y1, ..., wn/yn][v1/x1, ..., vm/xm][new C(v1, ..., vm)/this]b
//   - So we're looking at 3 substitutions
//     1) The substitution of the formal params of the method f:
//          y1, ..., yn
//        with the args
//          w1, ..., wn
//     2) The substitution of the formal params of the class C:
//          x1, ..., xn
//        with the args
//          v1, ..., vn
//     3) The substitution of the self reference this by the value of the object:
//          new C(v1, ..., vn)


// //////////////
// Object Rewriting Examples
// //////////////

new Rational(1, 2).numer
// Remember, 3 subs: class params, method params, this
[1/x, 2/y][][new Rational(1,2)/this]x
// Since the RHS is just x, all we need is the 1/x bit
1

new Rational(1, 2).less(new Rational(2, 3))
[1/x, 2/y][new Rational(2, 3)/that][new Rational(1, 2)/this] this.numer * that.denom < that.numer * this.denom
new Rational(1, 2).numer * new Rational(2, 3).denom < new Rational(2, 3).numer * new Rational(1, 2).denom
[1/x, 2/y][][new Rational(1,2)/this]x * [2/x, 3/y][][new Rational(2,3)/this]y < [2/x, 3/y][][new Rational(2,3)/this]x * [1/x, 2/y][][new Rational(1,2)/this]y
1 * 3 < 2 * 2
true


// //////////////
// Operators
// //////////////

How can we override +, -, *, etc.?
  - First, note that any operator can be written infix or with dot notation
    - so the following are all valid:

infix:          dot notation:
r add s         r.add(s)
r less s        r.less(s)
r max s         r.max(s)

  - Second, scala allows symbolic identifiers (as well as alphanumeric, or mixed)
    - so the following are all valid identifiers:

x1
*
+?%&
vector_++
counter_=

  - Finally, how do we differentiate between:
    - negation (prefix -)
    - subtraction (infix -)
  - Anything you want prefix style you must call with unary_ before the operator
    - so in this case:

def unary_- = new Rational(-numer, denom)

  - Note that with a symbolic operator, you MUST HAVE A SPACE AFTER THE OPERATOR:

def + **stuff** is OK
def +**stuff** is not


// //////////////
// Operator precedence
// //////////////

The precedence of the operator is determined by its FIRST CHARACTER

// <lowest precedence>
(all letters)
|
^
&
< >
= !
:
+ -
* / %
(all other special characters)
// <highest precedence>


So, to fully parenthesize:

a + b ^? c ?^ d less a ==> b | c

The steps would be:

a + b ^? c ?^ d less a ==> b | c
a + b ^? (c ?^ d) less a ==> b | c
(a + b) ^? (c ?^ d) less a ==> b | c
(a + b) ^? (c ?^ d) less (a ==> b) | c
((a + b) ^? (c ?^ d)) less (a ==> b) | c
((a + b) ^? (c ?^ d)) less ((a ==> b) | c)
