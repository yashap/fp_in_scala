// //////////////
// More Useful List Methods
// //////////////

// Already know:
xs.isEmpty
xs.head
xs.tail

// Sublists and element access:
xs.length
xs.last
  // Throws exception is xs is empty
xs.init
  // List of all elements EXCEPT last
  // Throws exception is xs is empty
xs take n
  // A list of the first n elements of xs
  // If n > xs.length, then just returns xs
xs drop n
  // List without first n elements
xs(n)
  // The element at index n
  // Starts at 0, like in most langs
  // Equivalent to:
  xs apply n

// Creating new lists:
xs ++ ys
  // concat two lists
xs.reverse
xs updated (n, x)
  // update element at index n to x

// Finding elements:
xs indexOf x
  // index of first appearance of x
  // -1 if it doesn't appear
xs contains x
  // the same as the logical statement:
  // x indexOf x >= 0


// //////////////
// Efficiency of init
// //////////////

xs.head takes constant time
  - What about xs.init?
  - First, let's implement xs.last

def last[T](xs: List[T]): T = xs match {
  case List() => throw new Error("last of empty list")
  case List(x) => x
  case y :: ys => last(ys)
}

So, O(n) efficiency :(

def init[T](xs: List[T]): List[T] = xs match {
  case List() => throw new Error("init of empty list")
  case List(x) => List()
  case y :: ys => y :: init(ys)
}

Also O(n)


// //////////////
// concat
// //////////////

def concat[T](xs: List[T], ys: List[T]): List[T] = xs match {
  case List() => ys
  case z :: zs => z :: concat(zs, ys)
}

// Remember that:
ys ::: xs
// Is basically
xs.:::(ys)
// So:
concat(xs, ys) == ys ::: xs

// Complexity is O(|xs|)
//   proportional to the length of xs


// //////////////
// reverse
// //////////////

def reverse[T](xs: List[T]): List[T] = xs match {
  case List() => List()
  case y :: ys => reverse(ys) ++ List(y)
}

What's the complexity?
  - n for concat
  - and it does it n times
  - so complexty is n**2 (quadratic), which is very bad
    - can we do better?
      - probably (will see later)
      - arrays and mutable linked lists are both reversible in linear time


// //////////////
// merge sort
// //////////////

merge sort
  - if the list consists of zero or 1 elements, it's already sorted
  - otherwise
    - separate the list into 2 sub-lists, each containing ~1/2 of the elements of the original list
    - sort the two sub-lists
    - merge the two sorted sub-lists into a single sorted list

def msort(xs: List[Int]): List[Int] = {
  val n = xs.length/2
  if (n == 0) xs
  else {
    def merge(xs: List[Int], ys: List[Int]): List[Int] = xs match {
      case List() => ys
      case x :: xs1 => ys match {
        case Nil => xs
        case y :: ys1 =>
          if (x < y) x :: merge(xs1, ys)
          else y :: merge(xs, ys1)
      }
    }
    val (first, second) = xs splitAt n
    merge(msort(first), msort(second))
  }
}

Note that merge is written assuming both xs and ys are sorted
  - so if one is empty, just return the other
  - if both are non-empty, we need to compare the heads continuously


// //////////////
// splitAt
// //////////////

Function that takes a list and an index
  - returns a pair:
    - the elements up to the given index
    - the elements from that index
  - i.e. xs splitAt 2
    - first item in pair will be a list with items at indexes 0 and 1
    - second item will have indexes 2+


// //////////////
// tuples
// //////////////

// Defining a pair
val pair = ("answer", 42)
// Decomposing a pair
val (label, value) = pair
// Defining a longer tuple
val triple = ("hello", "world", 20)


A tuple type (T1, ..., Tn) is an abbreviation of the parameterized type:

scala.Tuplen[T1, ..., Tn]


A tuple expresion (e1, ..., en) is equivalent to the function application:

scala.Tuplen(e1, ..., en)


A tupple pattern (p1, ..., pn) is equivalent to the constructor pattern:

scala.Tuplen(p1, ..., pn)


// //////////////
// The Tuple classes
// //////////////

// All Tuplen classes are modeled after the following pattern:

case class Tuple2[T1, T2](_1: +T1, _2: +T2) {
  override def toString = "(" + _1 + "," + _2 +")"
}


// //////////////
// Re-writing merge with pattern matching pairs
// //////////////

def msort(xs: List[Int]): List[Int] = {
  val n = xs.length/2
  if (n == 0) xs
  else {
    def merge(xs: List[Int], ys: List[Int]): List[Int] = (xs, ys) match {
      case (Nil, ys) => ys
      case (xs, Nil) => xs
      case (x :: xs1, y :: ys1) =>
        if (x < y) x :: merge(xs1, ys)
        else y :: merge(xs, ys1)
    }
    val (first, second) = xs splitAt n
    merge(msort(first), msort(second))
  }
}


// //////////////
// Making sort general
// //////////////

Can't just do:

def msort[T](xs: List[T]): List[T] = ...

Because merge uses <, which isn't defined for arbitrary types T

Solution: parameterize merge with the necessary comparison function


def msort[T](xs: List[T])(lt: (T, T) => Boolean): List[T] = {
  val n = xs.length/2
  if (n == 0) xs
  else {
    def merge(xs: List[T], ys: List[T]): List[T] = (xs, ys) match {
      case (Nil, `ys`) => ys
      case (`xs`, Nil) => xs
      case (x :: xs1, y :: ys1) =>
        if (lt(x, y)) x :: merge(xs1, ys)
        else y :: merge(xs, ys1)
    }
    val (first, second) = xs splitAt n
    merge(msort(first)(lt), msort(second)(lt))
  }
}


Or, we could use a class in the standard library that represents orderings:

import math.Ordering

def msort[T](xs: List[T])(ord: Ordering[T]): List[T] = {
  val n = xs.length/2
  if (n == 0) xs
  else {
    def merge(xs: List[T], ys: List[T]): List[T] = (xs, ys) match {
      case (Nil, `ys`) => ys
      case (`xs`, Nil) => xs
      case (x :: xs1, y :: ys1) =>
        if (ord.lt(x, y)) x :: merge(xs1, ys)
        else y :: merge(xs, ys1)
    }
    val (first, second) = xs splitAt n
    merge(msort(first)(ord), msort(second)(ord))
  }
}

val nums = List(5, 6, 1, 100, 1000, -6)
val fruits = List("apple", "pineapple", "orange", "banana")

msort(nums)(Ordering.Int)
msort(fruits)(Ordering.String)


// //////////////
// implicit parameters
// //////////////

Finally, can actually have the compiler pick the "right" Ordering method by using the implicit keyword!

import math.Ordering

def msort[T](xs: List[T])(implicit ord: Ordering[T]): List[T] = {
  val n = xs.length/2
  if (n == 0) xs
  else {
    def merge(xs: List[T], ys: List[T]): List[T] = (xs, ys) match {
      case (Nil, `ys`) => ys
      case (`xs`, Nil) => xs
      case (x :: xs1, y :: ys1) =>
        if (ord.lt(x, y)) x :: merge(xs1, ys)
        else y :: merge(xs, ys1)
    }
    val (first, second) = xs splitAt n
    merge(msort(first), msort(second))
  }
}

val nums = List(5, 6, 1, 100, 1000, -6)
val fruits = List("apple", "pineapple", "orange", "banana")

msort(nums)
msort(fruits)


// How this works:
- the compiler will figure out the right implicit to pass based on the demanded type

- say a function takes an implicit param of type T
  - the compiler will search for an implicit definition that:
    - is marked implicit
    - has a type compatible with T
    - is visible at the point of the function call, or is defined in a companion object associated with T

- if there is a single (most specific) definition, it will be taken as the actual argument for the implicit parameter
  - otherwise, it's an error


// //////////////
// Higher-order list functions
// //////////////

- Common patterns for functions on lists
  - transforming each element
  - filtering based on criteria
  - combining the elements of a list using an operator


// //////////////
// map
// //////////////

// Example
def scaleList(xs: List[Double], factor: Double): List[Double] = xs match {
  case Nil => xs
  case y :: ys => y * factor :: scaleList(ys, factor)
}

// Can be generalized to the map method of the List class
//   applies an arbitrary function to each item in the list
// The code is something like this:
abstract class List[T] {
  ...
  def map[U](f: T => U): List[U] = this match {
    case Nil => this
    case x :: xs => f(x) :: xs.map(f)
  }
}

// Then we can write scaleList as:
def scaleList(xs: List[Double], factor: Double) =
  xs map (x => x * factor)


// //////////////
// filter
// //////////////

// Code looks someting like this:
abstract class List[T] {
  ...
  def filter(p: T => Boolean): List[T] = this match {
    case Nil => this
    case x :: xs =>
      if (p(x)) x :: xs.filter(p)
      else xs.filter(p)
  }
}

// Usage
def posElems(xs: List[Int]) =
  xs filter (x => x > 0)


// //////////////
// Variations of filter
// //////////////

xs filterNot p
  // same as xs filter (x => !p(x))
  // so the list contains only those elements of xs that DON'T satisfy the predicate p

xs partition p
  // returns a double, the first half matching p, the second half not matching
  // same as (xs filter p, xs filterNot p), but computed in a single traversal of xs

xs takeWhile p
  // xs until p isn't satisfied

xs dropWhile p
  // the remainder of xs after any leading elements matching p have been removed

xs span p
  // same as (xs takeWhile p, xs dropWhile p)
  // but computed in a single traversal of the list xs


// //////////////
// reduce left
// //////////////

// Want a general way to do things like this:
sum(List(x1, ..., xn))        = 0 + x1 + ... + xn
product(List(x1, ..., xn))    = 1 * x1 * ... * xn

// Non-general example
def sum(xs: List[Int]): Int = xs match {
  case Nil => 0
  case y :: ys => y + sum(ys)
}

// More general, we want something like:
List(x1, ..., xn) reduceLeft op = (...(x1 op x2) op ... ) op xn

// Which would let us do:
def sum(xs: List[Int]) =
  (0 :: xs) reduceLeft ((x, y) => x + y)

def product(xs: List[Int]) =
  (1 :: xs) reduceLeft ((x, y) => x * y)

// Or, shorter, using underscores
  (_ * _)
  // instead of
  ((x, y) => x * y)
// Each _ represents a parameter, in order, from left to right
//  Then what you do with it
//  So we could rewrite the above two functions as:
def sum(xs: List[Int]) = (0 :: xs) reduceLeft (_ + _)
def product(xs: List[Int]) = (1 :: xs) reduceLeft (_ * _)


// //////////////
// fold left
// //////////////

reduceLeft can only be applied to non-empty lists

foldLeft is more general
  - takes an accumulator z, as an additional parameter
  - this is what is returned when it's called on an empty list

def sum(xs: List[Int]) = (xs foldLeft 0) (_ + _)
def product(xs: List[Int]) = (xs foldLeft 1) (_ * _)


// implementing

abstract class List[T] {
  ...
  def reduceLeft(op: (T, T) => T): T = this match {
    // if Nil list, throw error
    case Nil => throw new Error("Nil.reduceLeft")
    // else, fold left with head as the zero element
    case x :: xs => (xs foldLeft x)(op)
  }
  def foldLeft[U](z: U)(op: (U, T) => U): U = this match {
    // if Nil list, return zero element
    case Nil => z
    // else, foldLeft on the tail with the zero element incremented by op
    case x :: xs => (xs foldLeft op(z, x))(op)
    // note that we'll finally return the accumulator/zero element
  }
}


// //////////////
// foldRight and reduceRight
// //////////////

abstract class List[T] {
  ...
  def reduceLeft(op: (T, T) => T): T = this match {
    case Nil => throw new Error("Nil.reduceRight")
    case x :: xs => op(x, xs.reduceRight(op))
  }
  def foldRight[U](z: U)(op: (U, T) => U): U = this match {
    case Nil => z
    case x :: xs => op(x, (xs foldRight z)(op))
  }
}


// //////////////
// foldLeft vs. foldRight
// //////////////

For operators that are associative and commutative, they're equivalent (in answer, not necessarily efficiency)

But, sometimes only 1 of the 2 operators is appropriate


def concat[T](xs: List[T], ys: List[T]): List[T] =
  (xs foldRight ys)(_ :: _)
// this makes sense because it's:
// x1 :: x2 :: ... xn :: y1 :: ... :: xm

concat(List(20,2,-100,-5), List(9,99))

def concat2[T](xs: List[T], ys: List[T]): List[T] =
 (xs foldLeft ys)(_ :: _)

concat2(List(20,2,-100,-5), List(9,99))



I AM SKIPPING THE NEXT TWO SECTIONS, CAN COME BACK LATER, BUT THEY'RE PURELY THEORETICAL!

(skipped "5.6 - Reasoning About Concat" and "5.7 - A Larger Equational Proof on Lists")
