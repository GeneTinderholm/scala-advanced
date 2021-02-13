package exercises

import scala.annotation.tailrec

class BadIdeaException extends RuntimeException
abstract class MyStream[+A] {
    def isEmpty: Boolean
    def head: A
    def tail: MyStream[A]
    def #::[B>: A](el: B): MyStream[B] // prepend operator
    def ++[B>: A](other: => MyStream[B]): MyStream[B]
    def foreach(f: A => Unit): Unit
    def map[B](f: A => B): MyStream[B]
    def flatMap[B](f: A => MyStream[B]): MyStream[B]
    def filter(predicate: A => Boolean): MyStream[A]
    def take(n: Int): MyStream[A] // takes the first n elements out of this stream (finite stream)
    def takeAsList(n: Int): List[A] = take(n).toList()
    @tailrec
    final def toList[B >: A](acc: List[B] = Nil): List[B] =
        if (isEmpty) acc.reverse
        else tail.toList(head :: acc)
}

object EmptyStream extends MyStream[Nothing] {
    def isEmpty: Boolean = true
    def head: Nothing = throw new NoSuchElementException
    def tail: MyStream[Nothing] = throw new NoSuchElementException
    def #::[B >: Nothing](el: B): MyStream[B] = new Cons(el, this)
    def ++[B >: Nothing](other: => MyStream[B]): MyStream[B] = other
    def foreach(f: Nothing => Unit): Unit = ()
    def map[B](f: Nothing => B): MyStream[B] = this
    def flatMap[B](f: Nothing => MyStream[B]): MyStream[B] = this
    def filter(predicate: Nothing => Boolean): MyStream[Nothing] = this
    def take(n: Int): MyStream[Nothing] = this
}

class Cons[+A](override val head: A, rest: => MyStream[A]) extends MyStream[A] {
    def isEmpty: Boolean = false
    override lazy val tail: MyStream[A] = rest
    def #::[B>: A](el: B): MyStream[B] = new Cons(el, this) // prepend operator
    def ++[B>: A](other: => MyStream[B]): MyStream[B] = new Cons(head, tail ++ other)
    def foreach(f: A => Unit): Unit = {
        f(head)
        tail.foreach(f)
    }
    def map[B](f: A => B): MyStream[B] = new Cons(f(head), tail.map(f)) // preserves lazy evaluation
    def flatMap[B](f: A => MyStream[B]): MyStream[B] = f(head) ++ tail.flatMap(f) // also lazy

    def filter(predicate: A => Boolean): MyStream[A] =
        if (predicate(head)) new Cons(head, tail.filter(predicate))
        else tail.filter(predicate)

    def take(n: Int): MyStream[A] =
        if (n <= 0) EmptyStream
        else if (n == 1) new Cons(head, EmptyStream)
        else new Cons(head, tail.take(n - 1))
}

object MyStream {
    def from[A](start: A)(generator: A => A): MyStream[A] =
        new Cons(start, MyStream.from(generator(start))(generator))
}
object MyStreamExercises extends App {
    val naturals = MyStream.from(1)(_ + 1)
    val first100 = naturals.takeAsList(100)
    println(first100)
    val startFrom0 = 0 #:: naturals
    println(startFrom0.head)
    println(startFrom0.tail.head)
    println(startFrom0.tail.tail.head)

//    startFrom0.take(10000).foreach(println)
    println(naturals.map(_ * 2).take(100).toList())
    println(startFrom0.flatMap(x => new Cons(x, new Cons(x + 1, EmptyStream))).take(100).toList())
    def findNextFibonacci(current: Int): Int = {
        @tailrec
        def findFibonacci(a: Int, b: Int): Int =
            if (a == current) b
            else findFibonacci(b, b + a)
        findFibonacci(1, 2)
    }
    val fibonacciStream = 0 #:: 1 #:: MyStream.from(1)(findNextFibonacci)
    // alternate fibonacci
    def fibonacci(first: Int = 0, second: Int = 1): MyStream[Int] =
        new Cons(first, fibonacci(second, first + second))

    println(fibonacciStream.take(20).toList())
    println(fibonacci().take(20).toList())

//    println(fibonacciStream.filter(_ % 2 == 0).take(10).toList())
    def findNextPrime(i: Int): Int = {
        val naturalsStartingAt2 = MyStream.from(2)(_ + 1)
        @tailrec
        def findPrime(filteredStream: MyStream[Int], current: Int): Int = {
            val newFilteredStream = filteredStream.filter(_ % current != 0)
            if (current == i) newFilteredStream.head
            else findPrime(newFilteredStream, newFilteredStream.head)
        }
        findPrime(naturalsStartingAt2, 2)
    }
    val primesStream = MyStream.from(2)(findNextPrime) // ???
    println(primesStream.take(10).toList())
    // alternate primes implementation
    def primes(current: Int = 2, stream: MyStream[Int] = MyStream.from(2)(_ + 1)): MyStream[Int] = {
        val filteredStream = stream.filter(_ % current != 0)
        new Cons(current, primes(filteredStream.head, filteredStream))
    }
    println(primes().take(10).toList())
    println(primes().take(20).toList())
    // third prime implementation that doesn't require an initial number
    def eratosthenes(numbers: MyStream[Int] = MyStream.from(2)(_ + 1)): MyStream[Int] =
        new Cons(numbers.head, eratosthenes(numbers.filter(_ % numbers.head != 0)))
    println(eratosthenes().take(10).toList())
    println(eratosthenes().take(20).toList())
}
