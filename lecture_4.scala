// //////////////
// Functions as Objects
// //////////////

We saw how types like Int and Boolean can be implemented as normal classes
  - but what about functions?

They can, in fact FUNCTIONS ARE CREATED AS OBJECTS IN SCALA
  - "under the hood"
  - the function type:

A => B

  - is just an abbreviation for the class:

scala.Function1[A,B]

  - which is roughly defined as follows

package scala
trait Function1[A,B] {
  def apply(x: A): B
}

  - so functions are objects with apply methods
    - apply takes an arg of type A, and returns type B
  - There are also traits Function2, Function3, ...
    - for functions which take more params
    - currently allow up to 22 params


// //////////////
// Expansion of Function Values
// //////////////

// An annonymous function such as:

(x: Int) => x * x

// Expands to:

{
  class AnonFun extends Function1[Int, Int] {
    def apply(x: Int) = x * x
  }
  new AnonFun
}

// or, shorter, using ANNONYMOUS CLASS SYNTAX

new Function1[Int, Int] {
  def apply(x: Int) = x * x
}


// //////////////
// Expansion of Function Values
// //////////////

// A function call, such as f(a,b), which f is a value of some class type, expanded to:

f.apply(a,b)


// So the OO-translation of:

val f = (x: Int) => x * x
f(7)

// Would be:

val f = new Function1[Int, Int] {
  def apply(x: Int) = x * x
}
f.apply(7)


// //////////////
// Functions and Methods
// //////////////

Wait, don't we get an infite loop above?
  - does the def apply... bit get subbed with Function1, which gets subbed with def apply..., etc.?
    - no
  - why?  Because a method such as:

def f(x: Int): Boolean = ...

  - is not ITSELF a function value
    - but, if f is used in a place where Function type is expected, it is converted automatically to the function value:

(x: Int) => f(x)
// This is called eta-expansion

    - or expanded:

new Function1[Int, Boolean] {
  def apply(x: Int) = f(x)
}


// //////////////
// Excercise
// //////////////

In pacakge week4, define an:

object List {
  ...
}

With 3 functions in it so that users can create lists of lengths 1-2 using the syntax:

List()
List(1)
List(2,3)

// Answer:

// Remember before we had:
trait List[T] {
  def isEmpty: Boolean
  def head: T
  def tail: List[T]
}
class Cons[T](val head: T, val tail: List[T]) extends List[T] {
  def isEmpty = false
  override def toString = "(" + head + " " + tail + ")"
}
class Nil[T] extends List[T] {
  def isEmpty = true
  def head: Nothing = throw new NoSuchElementException("Nil.head")
  def tail: Nothing = throw new NoSuchElementException("Nil.tail")
  override def toString = "."
}

// So now we can let ppl create List(), List(1), List(2,10) like this:
object List {
  def apply[T]() = new Nil
  def apply[T](x1: T): List[T] = new Cons(x1, new Nil)
  def apply[T](x1: T, x2: T): List[T] = new Cons(x1, new Cons(x2, new Nil))
}

List()
List(1)
List(20,30)


// //////////////
// Polymorphism
// //////////////

Two principal forms of polymorphism:
  - subtyping
    - associated with OOP
    - can pass instances of a subtype where a base type is required
  - generics
    - associated with FP
    - can parameterize types with other types

Now we'll look at interactions btwn these two concepts
  - 2 main areas to cover
    - bounds
    - variance


// //////////////
// Type Bounds
// //////////////

Consider the method assertAllPos, which:
  - takes an IntSet
  - returns the IntSet itself if all its elements are positive
  - throws an exception otherwise

What's the best type we can give to assertAllPos?
  - maybe something like this?

def assertAllPos(s: IntSet): IntSet

  - but this ignores the exception
    - can we be more precise?
    - here are the possiblities:

assertAllPos(Empty) = Empty
assertAllPos(NonEmpty(...)) = {
  NonEmpty(...) || throws Exception
}

How can we express that it does Empty sets => Empty sets, and NonEmpty to NonEmpty
  - one way:

def assertAllPos[S <: IntSet](r: S): S = ...

We've seen all this notation before, except the type parameter bit!
  - Here "<: IntSet" is an UPPER BOUND of the type parameter S
    - it means that S can be instantiated only to types that conform to IntSet
    - so, we can interpret the notation as:

S <: T      // S is a subtype of T
S >: T      // S is a supertype of T, or T is a subtype of S

  - so in the above, we're saying that the type parameter is at most-specific IntSet?  But it can also be some subtype?


// //////////////
// Recap
// //////////////

// Upper bound
[S <: NonEmpty]
Can think of as:
  - "S is a subtype of NonEmpty"
  - "S is NonEmpty, or more specific"
  - "S is a type that conforms to IntSet"
===> the more common use of type bounds
===> remember, "S is a subtype of T" basically means "S extends T"

// Lower bound
[S >: NonEmpty]
Can thin of as:
  - "S is a supertype of NonEmpty"
  - "S is NonEmpty, or less specific"
    - One of:
      NonEmpty, IntSet, AnyRef, Any
===> the less common use of type bounds

// Mixed bounds
[S >: NonEmpty <: IntSet]
This would restrict S to any type on the interval between NonEmpty and IntSet
  - in this case, it's actually just those 2 types, but it could be 2+

// //////////////
// Covariance
// //////////////

- We know that NonEmpty is a subtype of IntSet

NonEmpty <: IntSet

- Is this therefore also true?

List[NonEmpty] <: List[IntSet]

- Yes, it is true!
  - we call types for which this relationship holds COVARIANT
    - because their subtyping relationship varies with the type parameter
  - does covariance make sense for all parameterized types, not just for List?

- To answer, let's think about arrays in Java (and C#)
  - In Java:
    - An array of T elements is written T[] in Java
    - In Scala, we use parameterized type syntax Array[T] to refer to the same type
  - Arrays in Java are covariant, so one would have:

NonEmpty[] <: IntSet[]

  - BUT covariant array typing causes problems
    - to see why, consider the Java code below:


NonEmpty[] a = new NonEmpty[]{new NonEmpty(1, Empty, Empty)}
// Create an array of NonEmptys a
IntSet[] b = a
// Assign it to an IntSet b
// So now a and b both point at the same array
b[0] = Empty
// We assign the first value of the b array to be Empty
NonEmpty s = a[0]
// We set the NonEmpty s to be the first element of the array, which is actually of type Empty!

  - in that last line, it looks like we assigned an Empty set to a variable of type NonEmpty!
    - What went wrong?  How could we assign an Empty set into a variable that's supposed to be typed NonEmpty?
  - the 3rd line would actually throw an error at runtime
    - an ArrayStoreException
    - which basically protects the array at runtime to only take NonEmpty values
    - basically, to protect against this issue, Java needs to store a "type tag" with arrays at runtime
      - to protect against the wrong type getting in there

This all seems bad
  - we have to do runtime checks (performance hit)
  - we also traded a compile time error for a runtime error

You can argue that it was a mistake in Java to make arrays covariant
  - required this patching later
  - why did the designers of Java do it?
    - they wanted to be able to write a method like sort that would work for any array
      - sort would take an object array
      - covariance was necessary so that an array of strings, ints, whatever could all be passed to an object array
  - in later versions of Java, as in Scala, you can acheive this with generic types


// //////////////
// The Liskov Substitution Principle
// //////////////

So, when does it make sense to allow a type to be a subtype of another?
  - Barbara Liskov says:

"""
  If A <: B, then everything one can do with a value of type B
  one should also be able to do with a value of type A
"""

B is the supertype
A is the subtype (extends B)

Everything we can do with B, we should be able to do with A


// //////////////
// Excercise
// //////////////

Let's look at that problematic array example in Scala

val a: Array[NonEmpty] = Array(new NonEmpty(1, Empty, Empty))
val b: Array[IntSet] = a
b(0) = Empty
val s: NonEmpty = a(0)

In this case, we would see a type error in line 2
  - a was an array of NonEmpty
  - b was an array of IntSet
  - BUT IN SCALA, ARRAYS ARE NOT COVARIANT
    - so, b would have to be of type Array[NonEmpty]


// //////////////
// Pure Object Orientation
// //////////////

So far, we've seen 3 fundamental types
  - primitive types
  - functions
  - classes

But, in a pure OO lang, every value is actually an object
  - the type of each value is a class
  - is Scala a pure OO lang?
    - at first glance, it seems like primitive types and functions aren't objects, but is this true?


// //////////////
// Standard Classes
// //////////////

Conceptually, types such as Int of Boolean do not receive special treatment in Scala
  - they are like the other classes, defined in the package scala
    - and are subclasses of AnyVal
  - for efficiency reasons, the compiler represents the values of scala.Int as 32-bit integers, and the values of type scala.Boolean as Java's boolean, etc.
    - really, this is just an optimization
    - conceptually, these types can be treated just like normal, and the instances like normal objects


// //////////////
// Pure Booleans
// //////////////

The Boolean type maps to the JVMs primitive type Boolean
  - but one COULD define it as a class from first principles

package idealized.scala
abstract class Boolean {
  def ifThenElse[T](thn: => T, els: => T): T
  // this is an abstract method that must be implemented?

  def && (x: => Boolean): Boolean = ifThenElse(x, false)
  // if the boolean expression being fed in is true, return that
  // else, return false
  def || (x: => Boolean): Boolean = ifThenElse(true, x)
  def unary_!: Boolean            = ifThenElse(false, true)
  def == (x: Boolean): Boolean    = ifThenElse(x, x.unary_!)
  def != (x: Boolean): Boolean    = ifThenElse(x.unary_!, x)
  ...
}


// //////////////
// Boolean Constants
// //////////////

In the above, we used true and false constants
  - they can't be of type Boolean, because that's what we're defining!
  - here's how we could define them:

package idealized.scala

object true extends Boolean {
  def ifThenElse[T](thn: => T, els: => T) = thn
}

object false extends Boolean {
  def ifThenElse[T](thn: => T, els: => T) = els
}


// //////////////
// Excercise
// //////////////

Provide an implementation of the comparison operator < in class idealized.scala.Boolean
  - assume for this that false < true

abstract class Boolean {
  def ifThenElse[T](thn: => T, els: => T): T

  def && (x: => Boolean): Boolean = ifThenElse(x, false)
  def || (x: => Boolean): Boolean = ifThenElse(true, x)
  def unary_!: Boolean            = ifThenElse(false, true)
  def == (x: Boolean): Boolean    = ifThenElse(x, x.unary_!)
  def != (x: Boolean): Boolean    = ifThenElse(x.unary_!, x)

  def < (x: Boolean): Boolean     = ifThenElse(false, x)
}


// //////////////
// The class Int
// //////////////

Here's a partial specification of the class scala.Int


class Int {
  def + (that: Double): Double
  def + (that: Float): Float
  def + (that: Long): Long
  def + (that: Int): Int
  // same for -, *, /, %

  def << (cnt: Int): Int
  // same for <<<, >>, >>>, */

  def & (that: Long): Long
  def & (that: Int): Int
  // same for |, ^ */

  def == (that: Double): Boolean
  def == (that: Float): Boolean
  def == (that: Int): Boolean
  // same for !=, <, >, <=, >=
}


Can it be implemented as a class from first principles?
  - i.e. not using primitive ints, just using objects and functions?


// //////////////
// Excercise
// //////////////

Provide an implementation of the abstract class Nat that represents non-negative numbers


abstract class Nat {
  def isZero: Boolean
  def predecessor: Nat
  def successor: Nat = new Succ(this)
  def +(that: Nat): Nat
  def -(that: Nat): Nat
}

// Solution:
// We'll be using the above Boolean, and true and false object

object 0 extends Nat {
  def isZero = true
  def predecessor = throw new Error("0.predecessor")
  def +(that: Nat) = that
  def -(that: Nat) = if (that.isZero) this else throw new Error("negative number")
}

class Succ(n: Nat) extends Nat {
  def isZero = false
  def predecessor = n
  def +(that: Nat) = new Succ(n + that)
  def -(that: Nat) = if (that.isZero) this else n - that.predecessor
}


These are called "Peano numbers"
  - starting with these, we could then produce further types
    - like integers and floats


// //////////////
// //////////////
// //////////////
// NOTE ---> I SKIPPED THE VARIANCE SECTION
// //////////////
// //////////////
// //////////////


// //////////////
// Decomposition
// //////////////

Say you have a heirarchy of classes
  - and you want to build tree-like data sctructures from the instances of these classes
    - how do you build such a tree?
    - how do you find out what kind of elements are in the tree?
    - how do access the data stored in these elements?


// //////////////
// Decomposition Example
// //////////////

Suppose you want to write a small interpreter for arithmetic expressions
  - let's limit ourselves to numbers and additions
  - expressions can be represented as a class heirarchy
    - with a base trait Expr
    - two subclasses, Number and Sum
  - to treat an expression, it's necessary to know the expression's shape (is it a Number or Sum) and its components
    - that brings us to the following implementation


trait Expr {
  def isNumber: Boolean
  def isSum: Boolean
  def numValue: Int
  def leftOp: Expr
  def rightOp: Expr
}

class Number(n: Int) extends Expr {
  def isNumber = true
  def isSum = false
  def numValue = n
  def leftOp = throw new Error("Number.leftOp")
  def rightOp = throw new Error("Number.rightOp")
}

// We want our sum object"
//   new Sum(e1, e2)
// to represent:
//   e1 + e2
class Sum(e1: Expr, e2: Expr) extends Expr {
  def isNumber = false
  def isSum = true
  def numValue = throw new Error("Sum.numValue")
  def leftOp = e1
  def rightOp = e2
}

// Now we can write an evaluation function
// We want it to behave like this:
// eval(Sum(Number(1), Number(2))) == 3
def eval(e: Expr): Int = {
  if (e.isNumber) e.numValue
  else if (e.isSum) eval(e.leftOp) + eval(e.rightOp)
  else throw new Error("Unknow expression " + e)
}


Problem:
  - writing all these classification and accessor functions quickly becomes tedious
    - the classification functions are things like isNumber, isBoolean
    - the accessor functions are things like numValue, leftOp, rightOp
  - and it only gets worse as we add new forms of expressions
    - say we want to be able to express products, and variables (that take a string giving an expression a name)

class Prod(e1: Expr, e2: Expr) extends Expr     // e1 * e2
class Var(x: String) extends Expr               // Variable 'x'

  - we'd need to add classification and access methods not just for these, but for all the exiting classes too!
    - Var would need 2 new methods (isVar, name), Prod would need 1 (isProd, could re-use left and right ops)
    - it would add 25 methods (3 to each of the existing, 8 each for the new ones)
      - because Expr needs 8 methods, its 4 subclasses also need 8 methods
  - key takeaway
    - as we extend the heirarchy, the num of methods GROWS QUADRATICALLY
    - clearly not sustainable!


// //////////////
// Non-Solution: Type Tests and Type Casts
// //////////////

Hacky solution:
  - use type tests and type casts
  - Scala lets you do these using methods defined in the Any class

def isInstanceOf[T]: Boolean
// checks if obj conforms to type T

def asInstanceOf[T]: T
// treats this obj as instance of type 'T'
// throw 'ClassCastException if it isn't

// Example use:
//   A formulation of the eval method using type tests and casts

def eval(e: Expr): Int =
  if (e.isInstanceOf[Number])
    e.asInstanceOf[Number].numValue
  else if (e.isInstanceOf[Sum])
    eval(e.asInstanceOf[Sum].leftOp) + eval(e.asInstanceOf[Sum].rightOp)
  else throw new Error("Unknown expression " + e)

// Pros:
//  - no need for classification methods
//  - access methods only for classes where the value is defined

// Cons:
//  - low-level and potentially unsafe


// //////////////
// Solution 1: Object-Oriented Decomposition
// //////////////

Suppose all we want to do is EVALUATE expressions
  - we could then define

trait Expr {
  def eval: Int
}
class Number(n: Int) extends Expr {
  def eval: Int = n
}
class Sum(e1: Expr, e2: Expr) extends Expr {
  def eval: Int = e1.eval + e2.eval
}

  - but what happens if you'd like to display expressions now?
    - you have to define new methods in all the subclasses
    - so adding methods is still a bit painful

trait Expr {
  def eval: Int
  def show: String
}
class Number(n: Int) extends Expr {
  def eval: Int = n
  def show: String = n.toString
}
class Sum(e1: Expr, e2: Expr) extends Expr {
  def eval: Int = e1.eval + e2.eval
  def show: String = e1.eval + " + " + e2.eval
}

  - even worse, what if you want to simplify expressions, say using the rule

a * b + a * c  ->  a * (b + c)

  - Problematic!
    - this is a non-local simplification
      - it cannot be encapsulated in the method of a single object
    - back to square one, need test and access methods for all the different subclasses


// //////////////
// Reminder: where we're at with the decomposition problem
// //////////////

Our class heirarchy:
  - Expr
    - Number
    - Sum
    - Prod
    - Var

Desired methods:
  - eval
  - show
  - simplify
    - this last one especially is difficult, can't be encapsulated in the method of a single object

Attempts seen previously:
  - Classification and access methods
    - Quadratic explosion of methods
  - Type tests and casts
    - Low-level
    - Unsafe
  - OO Decomposition
    - Does not always work (i.e. for methods like simplify)
    - Need to touch all classes to add a new method


// //////////////
// Solution 2: Functional Decomposition with Pattern Matching
// //////////////

Worth noting
  - the sole purpose of test and accessor functions is to REVERSE the construction process
    - Which subclass is used?
    - What were the argument of the contructor?

i.e. if we're left with an object, we want to know that it was constructed like this:
new Sum(e1, e2)


The situation is so common that Scala automates it
  - this automated solution is PATTERN MATCHING
  - this is acheived through CASE CLASSES
    - similar to a normal class definition, except preceeded by the modifier case
    - for example:

trait Expr
case class Number(n: Int) extends Expr
case class Sum(e1: Expr, e2: Expr) extends Expr

  - like before, this defines a trait, and two subclasses
  - it also implicitly (and automatically) defines COMPANION OBJECTS with apply methods, like this:

object Number {
  def apply(n: Int) = new Number(n)
}
object Sum {
  def apply(e1: Expr, e2: Expr) = new Sum(e1, e2)
}

  - these objects have "factory methods", that construct new objects
    - so we can now construct objects like this, using the companion object

Number(1)

  - instead of using the class directly

new Number(1)


However, these classes are now empty
  - how can we access the members?
    - Pattern Matching!
  - Pattern Matching is a generalization of the C/Java switch statement
    - in these langs, it can only be applied to numbers
    - in Scala, can be applied to class heirarchies
    - it's expressed in Scala using the keyword match

def eval(e: Expr): Int = e match {
  case Number(n) => n
  case Sum(e1, e2) => eval(e1) + eval(e2)
}

  - What's happening here?
    - we're saying "match the given expression, with a pattern, as defined by the left side of the case"
      - for example, if the Expr is a Number, then eval returns n
      - if the Expr is a Sum, then eval returns eval(e1) + eval(e2)

Pattern Matching syntax/rules:
  - match is followed by a sequence of cases
    - pat => expr
  - each case associates and expression expr with a pattern pat
  - a MatchError exception is thrown if no pattern matches the value fo the selector

e match {
  case pat => expr
  ...
  case pat => expr
}

  - so e is just subbed with the pattern that matches


// //////////////
// Forms of Patterns
// //////////////

Patterns are constructed from:
  - constructors, e.g. Number, Sum
  - variables, e.g. n, e1, e2
  - wildcard patterns, _
    - for example, Number(_) instead of Number(n)
    - with the _, we wouldn't be allowed to refer to the variable later
  - constants, e.g. 1, true, "abc", N (where N is defined as val N = 2)

Variables
  - always begin with a lowercase letter, e.g. n, e1, e2
  - the same variable name can only appear once in a pattern
    - so Sum(x, x) is not a legal pattern

Constants
  - names of constants begin with a capital letter
    - with the exception of the reserved words null, true, false
  - note how the capitalization differentiates Variables from Constants
    - important, because the variable n could match anything
    - but say a constant is defined as val N = 2
      - then N can only be 2

You can also compose these elements together
  - i.e. could match:

Sum(Number(1), Var(x))


// //////////////
// Evaluating Match Expressions
// //////////////

An expression of the form

e match { case p1 => e1 ... case pn => en }

Matches the value of the selector e with the patterns p1 ... pn in the order in which they were written
  - the whole match expression is re-written to the right hand side of the first matching case
  - references to e are replaced with the corresponding expression

What do patterns match?
  - a constructor pattern C(p1, ..., pn) matches all values of type C (or a subtype) that have been constructed with args matching the patterns p1, ..., pn
  - a variable pattern x matches any value, and binds the name of the variable to this value
  - a constant pattern c matches values that are equal to c (in the sense of ==)


Example:

// Remember that we have
trait Expr
case class Number(n: Int) extends Expr
case class Sum(e1: Expr, e2: Expr) extends Expr

def eval(e: Expr): Int = e match {
  case Number(n) => n
  case Sum(e1, e2) => eval(e1) + eval(e2)
}

// Then this:
eval(Sum(Number(1), Number(2)))

// Rewrites to:
-->
Sum(Number(1), Number(2)) match {
  case Number(n) => n
  case Sum(e1, e2) => eval(e1) + eval(e2)
}

-->
eval(Number(1)) + eval(Number(2))

-->
Number(1) match {
  case Number(n) => n
  case Sum(e1, e2) => eval(e1) + eval(e2)
} + eval(Number(2))

-->
1 + eval(Number(2))

...
-->
3


// //////////////
// Pattern Matching and Methods
// //////////////

Can also have pattern matching as a method

trait Expr {
  def eval: Int = this match {
    case Number(n) => n
    case Sum(e1, e2) => e1.eval + e2.eval
  }
}

The only difference is that instead of matching on the expression e, we match on the object itself
  - also, doesn't need args, the arg is the object itself

Another option:
  - leave eval abstract in Expr
  - implement in Sum and Number

Which option is better?
  - depends
    - are you more often adding new subclasses?
      - then, better to define eval in each subclass
      - don't have to keep changing the trait
    - or are you more often adding methods?
      - then, better to add them in the trait
      - prevents you from having to add them in every single class
  - this tradeoff is often called the "expression problem"


// //////////////
// Challenge
// //////////////

Create a show function, that returns a string representing the expression

trait Expr {
  def eval: Int = this match {
    case Number(n) => n
    case Sum(e1, e2) => e1.eval + e2.eval
  }

  def show: String = this match {
    case Number(n) => n.toString
    case Sum(e1, e2) => e1.show + " + " + e2.show
    case 
  }
}
case class Number(n: Int) extends Expr
case class Sum(e1: Expr, e2: Expr) extends Expr

val x = Sum(Number(1), Number(2))
x.eval
x.show


// //////////////
// Lists
// //////////////

List syntax:

List(x1, ..., xn)


Examples:
val fruit = List("apples", "organges", "pears")
val nums  = List(1, 2, 3)
val diag3 = List(List(1, 0, 0), List(0, 1, 0), List(0, 0, 1))
val empty = List()


Arrays vs. Lists:
  - both are sequences
  - Lists are immutable, arrays are mutable
    - very much like the cons lists we've made
  - Lists are recursive, arrays are flat


// //////////////
// List structure
// //////////////

val fruit = List("apples", "organges", "pears")

        ▢▢
       ╱  ╲
"apples"   ▢▢
          ╱  ╲
  "oranges"   ▢▢
             ╱  ╲
       "pears"  Nil


val diag3 = List(List(1, 0, 0), List(0, 1, 0), List(0, 0, 1))

          ▢▢
         ╱   ╲  
        ╱      ╲
       ╱         ╲
      ╱            ╲
     ╱               ▢▢     
    ╱               ╱  ╲    
  ▢▢               ╱    ╲
 ╱  ╲             ╱      ▢▢
1    ▢▢         ▢▢         ╲
    ╱  ╲       ╱  ╲         ╲
   0    ▢▢    0    ▢▢         ▢▢
       ╱  ╲       ╱  ╲       ╱  ╲
      0   Nil    1    ▢▢    0    ▢▢
                     ╱  ╲       ╱  ╲
                    0   Nil    0    ▢▢
                                   ╱  ╲
                                  1   Nil


// //////////////
// List types
// //////////////

Like arrays, lists are homogeneous
  - all elements must have the same type
The type of a list with elements of type T is written
  - scala.List[T]
  - or, just List[T] for short

val fruit: List[String]    = List("apples", "organges", "pears")
val nums: List[Int]        = List(1, 2, 3)
val diag3: List[List[Int]] = List(List(1, 0, 0), List(0, 1, 0), List(0, 0, 1))
val empty: List[Nothing]   = List()


// //////////////
// List constructors
// //////////////

The List() style we've seen to construct lists is syntactic sugar
  - more fundamentally, all lists are constructed from:
    - the empty list Nil, and
    - the constructor operation, :: (pronounced cons)

x :: xs gives a new list with the first element x, followed by the elements of xs


For example:

val fruit: List[String]    = "apples" :: ("organges" :: ("pears" :: Nil))
val nums: List[Int]        = 1 :: (2 :: (3 :: Nil))
val empty: List[Nothing]   = Nil


// //////////////
// Right Associativity
// //////////////

STOPPED TAKING THOROUGH NOTES AT:
5:30 of Lecture 4.7 - Lists



// //////////////
// Insertion sort
// //////////////

def isort(xs: List[Int]): List[Int] = xs match {
  case List() => List()
  case y :: ys => insert(y, isort(ys))
}

def insert(x: Int, xs: List[Int]): List[Int] = xs match {
  case List() => List(x)
  case y :: ys =>
    if (x <= y) x :: xs
    else y :: insert(x, ys)
}

// Evaluates like this:
isort(List(10,5,100))
insert(10, isort(List(5,100)))
insert(10, insert(5, isort(List(100))))
insert(10, insert(5, insert(100, isort(List())))
insert(10, insert(5, insert(100, List()))
insert(10, insert(5, List(100))
insert(10, List(5, 100))
List(5, 10, 100)

// Wost case complexity?
//   insert is O(N), if x > all emelents of xs
//   isort, ignoring insert, is always O(N), always calls itself for each element of the list
// So worst case is O(N*N)


LEFT OFF AT:
End of of Lecture 4.7 - Lists
