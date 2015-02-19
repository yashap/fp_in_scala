// //////////////
// Vectors
// //////////////

- Lists
  - linear -> access to the first element is much faster than access to the middle or end

- Vectors
  - much more evenly balanced
    - a vector of up to 32 elements is an array of 32 elements
    - a vector of 1024 elements (32*32, or 2**10) has a base array with pointers to 32 other arrays (also of 32 elements each)
    - Repeats, so grows like this:
      - 1 level:  2**5  = 32
      - 2 levels: 2**10 = 1024
      - 3 levels: 2**15 = 32768
      - 4 levels: 2**20 = 1048576
      - etc.
  - number of accesses needed to get to a point is basically equal to the depth of the vector
    - basically log32(N)
      - grows super slowly
  - also pretty good for bulk operations that traverse every sequence
    - like maps, folds, etc.
    - arrays of 32 basically all fit into processor cache

- When are lists better than vectors?
  - if your access patterns have recursive structures
    - taking head and tail of a list is constant time with lists, not with vectors
  - however, if you're doing lots of map/fold/filter, then a vector is preferable


// //////////////
// Operations on vectors
// //////////////

val nums = Vector(1, 2, 3, -88)
val people = Vector("Bob", "James", "Peter")

- Support all the same operations as list, except ::
  - instead of :: there is:

x +: xs     // create a new vector with leading element x, followed by all elements of xs
xs :+ x     // create a new vector with the trailing element x, preceded by all elements of xs

- Note that the : always points to the sequence! (i.e. xs, not x)

- appending: xs :+ x
  - basically, have to replace the 32 bit array for every level where we have to do the change
  - we're left with two totally complete copies of the vector


// //////////////
// Collection Hierarchy
// //////////////

- A common base class of List and Vector is Seq
  - Seq, Set and Map all share a common base class Iterable
  - Array and String support the same operations as Seq, and are easily convertible
    - though aren't true subclasses, because they come from Java

val xs = Array(1, 2, 3, 44)
xs map (x => x * 2)

val s = "Hello World"
s filter (c => c.isUpper)

  - another simple and useful sequence is Range
    - represents an evenly spaced sequence of Int
    - three operators
      to (inclusive in upper bound)
      until (exclusive in upper bound)
      by (to determine step value)

val oneToFour: Range = 1 until 5
val oneToFive: Range = 1 to 5
1 to 10 by 3
6 to 1 by -2
6 to 0 by -2

  - ranges don't store all the values
    - just the start, end and step value, in a Range object


// //////////////
// Some more sequence operations
// //////////////

xs exists p
  // true if there's an element x of xs for which p(x) holds

xs forall p
  // true if p(x) holds for all elements of xs

xs zip ys
  // like zip in Python, makes sequence of pairs

xs.unzip
  // splits a sequence of pairs into a pair of sequences

xs.flatMap f
  // applies collection-valued function f to all elements of xs and concatenates results

xs.sum
  // sum all elements of numeric collection

xs.product
  // product of all elements in numeric collection

xs.max
  // max of all elements in collection (an Ordering must exist)

xs.min
  // min of all elements in collection (an Ordering must exist)




Left off at: 13:25 of 6.1 - Other Collections
