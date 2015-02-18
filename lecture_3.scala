// //////////////
// Abstract Classes
// //////////////

Consider the task of writing a class for sets of integers with the following operations:

abstract class IntSet {
  def contains(x: Int): Boolean
  def incl(x: Int): IntSet
}

This is an abstract class
  - can contain members which are MISSING IMPLEMENTATIONS
    - in this case, incl and contains => no function bodies!
  - you can't make new objects of an abstract class


// //////////////
// Class Extensions
// //////////////

Let's consider implementing this as a binary tree
  - two possible types of trees:
    1) A tree for the empty set
    2) A tree consisting of an integer and two sub-trees

The idea is that the trees are always sorted
  - i.e. at every branch, its right branch is a higher Int, its left branch a lower Int
  - this will make it easier to implement the conatins test
    - which determines if a given value is in the tree
  - the incl method lets you include a new value

      7
 5        12
E E   9       13
     E E     E E

class Empty extends IntSet {
  def contains(x: Int): Boolean = false
  def incl(x: Int): IntSet = new NonEmpty(x, new Empty, new Empty)
}

So the empty set definitely does not contain what you're looking for
  - however, it can be made to include new IntSets, at which point it becomes non-empty
    - "becomes", we don't actually mutate anything, we just return a new NonEmpty that is what we want

class NonEmpty(elem: Int, left: IntSet, right: IntSet) extends IntSet {
  def contains(x: Int): Boolean =
    if (x < elem) left contains x
    else if (x > elem) right contains x
    else true

  def incl(x: Int): IntSet =
    if (x < elem) new NonEmpty(elem, left incl x, right)
    else if (x > elem) new NonEmpty(elem, left, right incl x)
    else this
}

How contains works
  - contains will look to the left if you're looking for a smaller number than itself
  - will look to the right if looking for a bigger number
  - if that IntSet IS the number your looking for, returns true
  - and if you ever get passed to an Empty IntSet, returns false

How incl works:
  - if you want to include a smaller number, it's included to the left
  - larger numbers included to the right
  - same number not really included, it's already there
    - remember, this is a set, no duplicate numbers

Note that none of this is really storing state
  - "including" an element really means creating a new tree that has the expected vals/branches
  - so we're still purely functional here
  - there are called PERSISTENT DATA STRUCTURES
    - even when we make "changes", all of the old elements remain
    - we just sort of have two interpretations of the tree

SEE THIS IMAGE FOR HOW ADDTIONS TO THE TREE WORK:
./figures/Lecture_3_IntSet.jpg

Adding a toString definition, and using:


val t1 = new NonEmpty(3, new Empty, new Empty)
val t2 = t1 incl 4
val t3 = t2 incl 1 incl 5

abstract class IntSet {
  def contains(x: Int): Boolean
  def incl(x: Int): IntSet
}

class Empty extends IntSet {
  def contains(x: Int): Boolean = false
  def incl(x: Int): IntSet = new NonEmpty(x, new Empty, new Empty)
  override def toString = "."
}

class NonEmpty(elem: Int, left: IntSet, right: IntSet) extends IntSet {
  def contains(x: Int): Boolean =
    if (x < elem) left contains x
    else if (x > elem) right contains x
    else true

  def incl(x: Int): IntSet =
    if (x < elem) new NonEmpty(elem, left incl x, right)
    else if (x > elem) new NonEmpty(elem, left, right incl x)
    else this

  override def toString = "{" + left + elem + right + "}"
}


Note: both Empty and NonEmpty EXTEND IntSet:
  - this means that they CONFORM to the type IntSet
    - an object of type Empty of NonEmpty can be used wherever an IntSet is required!
  - IntSet is the SUPERCLASS of Empty and NonEmpty
  - Empty and NonEmpty are the SUBCLASSES of IntSet
  - Any user-defined class extends another class
    - If no superclass is given, the standard class Object in the pkg java.lang is assumed
  - All direct or indirect superclasses of an obj are its base classes
    - so the base classes of NonEmpty are IntSet and Object


// //////////////
// Implementing and Overriding
// //////////////

Subclasses can IMPLEMENT abstract members
  - they can also REDEFINE implemented members
  - for example

abstract class Base {
  def foo = 1
  def bar: Int
}

class Sub extends Base {
  override def foo = 2
  def bar = 3
}


// //////////////
// Object Definitions
// //////////////

Back to the IntSet example
  - every Empty is the same
    - contains nothing, points to nothing, doesn't know what points to it
  - so having a class with many instances is overkill
    - why not just have a single object?
    - can express this with an OBJECT DEFINITION
      - defines a SINGLETON OBJECT

object Empty extends IntSet {
  def contains(x: Int): Boolean = false
  def incl(x: Int): IntSet = new NonEmpty(x, Empty, Empty)
  override def toString = "."
}

  - note that singleton objects are values
    - so Empty evaluates to itself


// //////////////
// Programs
// //////////////

How to create standalone apps in Scala?
  - each app contains an object with a main method
  - for instance, here's the "Hello World!" program in Scala

object Hello {
  def main(args: Array[String]) = println("Hello World!")
}

  - once this is compiled, it can be started from the command line with

> scala Hello


// //////////////
// Dynamic Binding
// //////////////

Back to our IntSet problem
  - say we want to create a union of our IntSets
    - so, the IntSet with {3,4} unioned with the IntSet {1,3,5} should contain {1,3,4,5}
  - recursive solution that I don't really understand


object Empty extends IntSet {
  def contains(x: Int): Boolean = false
  def incl(x: Int): IntSet = new NonEmpty(x, Empty, Empty)
  def union(other: IntSet): IntSet = other
  override def toString = "."
}

abstract class IntSet {
  def contains(x: Int): Boolean
  def incl(x: Int): IntSet
  def union(other: IntSet): IntSet
}

class NonEmpty(elem: Int, left: IntSet, right: IntSet) extends IntSet {
  def contains(x: Int): Boolean =
    if (x < elem) left contains x
    else if (x > elem) right contains x
    else true

  def incl(x: Int): IntSet =
    if (x < elem) new NonEmpty(elem, left incl x, right)
    else if (x > elem) new NonEmpty(elem, left, right incl x)
    else this

  def union(other: IntSet): IntSet =
    ((left union right) union other) incl elem

  override def toString = "{" + left + elem + right + "}"
}


  - OO langs (including Scala) implement DYNAMIC METHOD DISPATCH
    - this means that the code invoked by a method call depends on the runtime type of the object that contains the method
    - Examples:


Empty contains 1
-> [1/x] [Empty/this] false
=  false

(new NonEmpty(7, Empty, Empty)) contains 7
-> [7/elem, Empty/left, Empty/right] [7/x] [new NonEmpty(7,Empty,Empty)/this]
   if (x < elem) this.left contains x
   else if (x > elem) this.right contains x
   else true
-> if (7 < 7) new NonEmpty(7, Empty, Empty).left contains 7
   else if (7 > 7) new NonEmpty(7, Empty, Empty).right contains 7
   else true
=  true


  - Note that dynamic dispatch of methods is very similar to calls of higher-order functions
    - can we implement one in terms of the other?
      - objects in terms of higher-order functions?
      - higher-order functions in terms of objects?


// //////////////
// Packages
// //////////////

Classes and objects are organized in packages
  - to place a class or object in a package, use a package clause at the top of the source file


package progfun.examples

object Hello { ... }


This would place Hello in the package progfun.examples
  - you can then refer to this object by its fully qualified name:
      progfun.examples.Hello
  - for instance, to run the Hello program from the command line:

> scala progfun.examples.Hello


The following are all valid ways to refer to stuff from packages:
  - or, from objects (can import from either)

// Option 1: fully qualified name
val x = new week2.Rational(1,2)

// Option 2: import one name
import week2.Rational
val x = new Rational(1,2)

// Option 3: import 2+ names
import week2.{Rational, Hello}
val x = new Rational(1,2)

// Option 4: import all (wildcard import)
import week2._
val x = new Rational(1,2)

  - 2 and 3 are named imports, 4 is a wildcard import


// //////////////
// Automatic Imports
// //////////////

The following are imported automatically in any Scala program
  - all members of the package scala
  - all members of the package java.lang
  - all members of the singleton object scala.Predef

Here are the fully qualified names of some of their members:

Int             scala.Int
Boolean         scala.Boolean
Object          java.lang.Object
require         scala.Predef.require
assert          scala.Predef.assert


// //////////////
// Traits
// //////////////

Java and Scala are both SINGLE INHERITANCE LANGUAGES
  - a class can only have one superclass

What if a class has several natural supertypes that it conforms to?
  - or, from which it wants to inherit code?
    - here, you can use TRAITS
  - a trait is declared like an absstract class
    - but with trait instead of abstract class
  - a class can inherit from 1+ traits (as many as you want)


val s = new Square(10)
s.height
s.width
s.surface
s.move(15)
s.name

val ss = new SimpleSquare(20)
s.height
s.width
s.surface

trait Planar {
  def height: Int
  def width: Int
  def surface = height * width
}

trait Movable {
  def move(meters: Int): String = "I moved " + meters + " meters"
}

abstract class Shape {
  def name: String
}

class Square(side: Int) extends Shape with Planar with Movable {
  def height: Int = side
  def width: Int = side
  def name: String = "square"
}

class SimpleSquare(side: Int) extends Planar {
  def height: Int = side
  def width: Int = side
}


  - traits are similar to interfaces in Java
    - but, more powerful
      - traits can contain fields and concrete methods
      - in Java, they can only have abstract methods
    - however, traits CANNOT have value PARAMETERS, only classes can


// //////////////
// Scala Class Heirarchy
// //////////////

SEE THIS IMAGE FOR THE HEIRARCHY:
./figures/Lecture_3_scala_class_heirarchy.jpg

The solid arrows point FROM subclass TO superclass
  - the dotted arrows aren't subtype
    - they say "conversion is possible"
    - so, you can automatically convert a scala.Long to a scala.Float, for example
      - with subtypes, you don't need to re-arrange the bits to convert from one type to another
      - with these conversion, you definitely do, and the conversion is not necessarily loss free

scala.Any
  - the base type of all types
  - Methods:
    ==
    !=
    equals
    hashCode
    toString

scala.AnyVal
  - the base type of all value types
    - basically, the primitive types that Scala inherits from Java

scala.AnyRef (a.k.a. java.lang.Object)
  - java.lang.String
  - scala.List
  - scala.Seq
  - scala.iterable
  - etc.
  --> many of these also implement the scala.ScalaObject trait!

scala.Nothing
  - Nothing is at the bottom of Scala's type hierarchy
    - it is a subtype of EVERY OTHER TYPE
  - there is no value of type Nothing
    - why is that useful?
      - to signal abnormal termination
        - i.e. what is the return type of a function that throws an error? Nothing
      - as an element type of empty collections
        - i.e. a Set of Nothing

scala.Null
  - a subtype of all the types that are themselves reference types
    - i.e. that inherit from scala.AnyRef
    - so List, String, Seq, iterable, and all sorts of other Scala and Java classes
  - every reference class type also has null as a value
    - the type of null is Null
    - cannot be a value of the subtypes of AnyVal
      - the primitive types
    - note that a val of type, say, String, that is null, still has type String

val x = null            // x: Null
val y: String = null    // y: String
val z: Int = null       // error: type mismatch


// //////////////
// Exceptions
// //////////////

Scala's exception handling is similar to Java's
  - you just:

throw Exc

  - aborts evaluation with the exception Exc
  - the type of this expression is Nothing

Example:

def error(msg: String) = throw new Error(msg)
error("test")


// //////////////
// Challenge
// //////////////

What should the type of this expression be?

if (true) 1 else false

So it can be Int or Boolean
  - since it can be either, we should go up the class heirarchy to their first shared base class

AnyVal

  - they also share Any, but it's better to pick AnyVal, because that's as specific as we can get


// //////////////
// Cons-List
// //////////////

Immutable linked list
  - A fundamental data sctructure in many functional langs
  - constructed from two building blocks

Nil    the empty list
Cons   a cell containing an element and the remainder of the list

SEE THIS IMAGE FOR EXAMPLES OF CONS-LISTS:
./figures/Lecture_3_cons_lists.jpg

Implementing a list of integers in this fashion:

val x = new Cons(4, new Cons(5, new Cons(10, new Nil)))

trait List[T] {
  def isEmpty: Boolean
  def head: T
  def tail: List[T]
}

class Cons[T](val head: T, val tail: List[T]) extends List[T] {
  def isEmpty = false
  override def toString = "(" + head + ", " + tail + ")"
}

class Nil extends List[T] {
  def isEmpty = true
  def head = throw new NoSuchElementException("Nil.head")
  def tail = throw new NoSuchElementException("Nil.tail")
}


// //////////////
// New syntax from above
// //////////////

Type parameters
  - written like this:

class Cons[T](val ...) {}

  - this makes it so we don't need a different class for an Int Cons, a String Cons, etc.
    - we can just pass the type as a type parameter

Defining values in parameters
  - just a new way to define parameters and automatically set them to values
  - these two have the same effect:

class Cons(val head: Int, val tail: IntList) extends IntList {
  override def toString = "(" + head + ", " + tail + ")"
}

class Cons(_head: Int, _tail: IntList) extends IntList {
  val head = _head
  val tail = _tail
}


// //////////////
// Generic Functions
// //////////////

Like classes, functions can have type parameters
  - for instance, here is a function that creates a list consisting of a single element:

def singleton[T](elem: T) = new Cons[T](elem, new Nil[T])

singleton[Int](1)
singleton[Boolean](true)


// //////////////
// Types and Evaluation
// //////////////

Type parameters don't affect evaluation at all
  - we can assume that all type params and type args are removed before evaluating the program
    - this is called TYPE ERASURE
  - types are checked by the compiler, then eliminated, they aren't kept around at runtime
  - Langs that use type erasure:
    - Java, Scala, Haskell, ML, OCaml
  - Langs that keep type params around at runtime:
    - C++, C#, F#


// //////////////
// Polymorphism
// //////////////

Polymorphism (applied to a function) means that a function type comes in many forms
  - (with functions) the function can be applied to arguments of many types, OR
  - (with classes) the type can have instances of many types

We've seen two principal forms of polymorphism
  - subtyping: instances of a subclass can be passed to a base class
    - i.e. anything expecting a List could also get a Nil or a Cons
    - more object oriented-ish
  - generics: instances of a function or class are created by type parameterization
    - i.e. the same function could make a list of ints, a list of doubles, etc.
    - more functional-ish


// //////////////
// Excercise
// //////////////

Write a function nth that takes an Int n and a list and selects the n'th element of the list
  - elements are numbered from 0
  - if index is outside the range from 0 up to the length of the list minus one, an IndexOutOfBoudsException should be thrown




*** LEFT OF AT ***
3.1 - Class Heirarchies
12:48
