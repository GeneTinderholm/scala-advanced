package lectures.part2afp

object LazyEvaluation extends App {
    lazy val x: Int = throw new RuntimeException
    lazy val y = { // only evaluates once
        println("Hello")
        42
    }
    println(y)
    println(y)
    def sideEffectCondition: Boolean = {
        println("Boo")
        true
    }
    lazy val lazyCondition = sideEffectCondition
    val simpleCondition = false

    println(if (simpleCondition && lazyCondition) "yes" else "no") // doesn't print boo

    def byNameMethod(n: => Int): Int = n + n + n + 1
    def retrieveMagicValue = {
        // side effect or long computation
        Thread.sleep(1000)
        42
    }
    def lazyByNameMethod(n: => Int): Int = {
        lazy val t = n
        t + t + t + 1
    }
//    println(byNameMethod(retrieveMagicValue)) // waits 3 seconds
//    println(lazyByNameMethod(retrieveMagicValue)) // waits 1 seconds

    def lessThan30(i: Int): Boolean = {
        println(s"$i less than 30?")
        i < 30
    }
    def greaterThan20(i: Int): Boolean = {
        println(s"$i greater than 20?")
        i > 20
    }
    val numbers = List(1, 25, 40, 5, 23)
    val lt30 = numbers.filter(lessThan30) // List(1, 25, 5, 23)
    val gt20 = lt30.filter(greaterThan20) // List(25, 23)
    println(gt20)

    val lt30Lazy = numbers.withFilter(lessThan30) // withFilter is lazy
    val gt20Lazy = lt30Lazy.withFilter(greaterThan20)
    println(gt20Lazy) // no side effects evaluated at this point
    gt20Lazy.foreach(println) // side effects happen in a different order than with gt20

    // for-comprehensions use withFilter with guards
    for {
        a <- List(1, 2, 3) if a % 2 == 0
    } yield a + 1
    List(1, 2, 3).withFilter(_ % 2 == 0).map(_ + 1) // equivalent

    class VeryBadIdeaException extends RuntimeException
    abstract class MyStream[+A] {
        def isEmpty: Boolean
        def head: A
        def tail: MyStream[A]
        def #::[B>: A](el: B): MyStream[B] // prepend operator
        def ++[B>: A](other: MyStream[B]): MyStream[B]
        def foreach(f: A => Unit): Unit
        def map[B](f: A => B): MyStream[B]
        def flatMap[B](f: A => MyStream[B]): MyStream[B]
        def filter(predicate: A => Boolean): MyStream[A]
        def take(n: Int): MyStream[A] // takes the first n elements out of this stream (finite stream)
        def takeAsList(n: Int): List[A]
    }
    object MyStream {
        def from[A](start: A)(generator: A => A): MyStream[A] = ???
    }
//    object EmptyStream extends MyStream[Nothing] {
//        def isEmpty: Boolean = true
//        def head: Nothing = throw new NoSuchElementException
//        def tail: MyStream[Nothing] = throw new NoSuchElementException
//        def #::[B >: Nothing](el: B): MyStream[B] = ??? // todo
//        def ++[B >: Nothing](other: MyStream[B]): MyStream[B] = other
//        def foreach(f: Nothing => Unit): Unit = ()
//        def map[B](f: Nothing => B): MyStream[B] = this
//        def flatMap[B](f: Nothing => MyStream[B]): MyStream[B] = this
//        def filter(predicate: Nothing => Boolean): MyStream[Nothing] = this
//        def take(n: Int): MyStream[Nothing] = this
//        def takeAsList(n: Int): List[Nothing] = List()
//    }
//    class InfiniteStream[+A](head: A, generator: A => A) extends MyStream[A] {
//        def isEmpty: Boolean = false
//        def head: A = head
//        def tail: MyStream[A] = new InfiniteStream[A](generator(head), generator)
//        def #::[B >: A](el: B): MyStream[B] = ??? // todo
//        def ++[B >: A](other: MyStream[B]): MyStream[B] = ??? // todo
//        def foreach(f: A => Unit): Unit = throw new VeryBadIdeaException
//        def map[B](f: A => B): MyStream[B] = new InfiniteStream[B](f(head), (el: B) => {
//            // need to calculate each element with generator(oldHead)
//            // new head is f(generator(oldHead))
//            // really the value that comes in from el isn't material to this process
//        })
//        def flatMap[B](f: A => MyStream[B]): MyStream[B] = this
//        def filter(predicate: A => Boolean): MyStream[A] = this
//        def take(n: Int): MyStream[A] = this
//        def takeAsList(n: Int): List[A] = List()
//    }
    class FiniteStream
}
