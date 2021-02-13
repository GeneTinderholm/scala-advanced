package lectures.part1as

import scala.util.Random

object AdvancedPatternMatching extends App {
    val numbers = List(1)
    numbers match {
        case head :: Nil => println(s"the only element is $head")
        case _ => ()
    }
    class Person(val name: String, val age: Int)
    object Person {
        def unapply(arg: Person): Option[(String, Int)] = Some((arg.name, arg.age))  // allows pattern matching in match
        def unapply(age: Int): Option[String] = Some(if (age < 21) "minor" else "major")
    }
    val bob = new Person ("Bob", 25)
    val greeting = bob match {
        case Person(n, a) => s"Hi, my name is $n and I am $a years old"
    }
    println(greeting)
    val legalStatus = bob.age match {
        case Person(status: String) => s"My legal status = $status" // status is the return value of unapply(age: Int)
    }
    println(legalStatus)
    // the unapply methods in the Person class get called with bob.age
    // arbitrary example
    object Obj {
        def unapply(thing: List[Int]): Option[Int] =
            thing.headOption
            // if (thing.isEmpty) None
            // else Some(thing.head)
    }
    val listOfInts = List(1, 2, 3, 4)
    val hd = listOfInts match {
        case Obj(x) => x + 4 // x is listOfInts.head (1), returns 5
    }
    println(hd)

    // cal also use boolean results
    object singleDigit {
        def unapply(x: Int): Boolean = x < 10
    }
    object even {
        def unapply(x: Int): Boolean = x % 2 == 0
    }
    val random = new Random()
    val y = random.nextInt(20)
    val message = y match {
        case singleDigit() => "single digit"
        case even() => "an even number"
        case _ => "no property"
    }
    println(s"$y is $message")

    // infix patterns
    case class Or[A, B](a: A, b: B)
    val either = Or(2, "two")
    val humanDescription = either match {
        case number Or string => s"$number is written as $string"
    }
    println(humanDescription)

    // decomposing sequences
    val vararg = numbers match {
        case List(1, _*) => "starts with 1"
    }

    abstract class MyList[+A] {
        def head: A = ???
        def tail: MyList[A] = ???
    }
    case object Empty extends MyList[Nothing]
    case class Cons[+A](override val head: A, override val tail:MyList[A]) extends MyList[A]
    object MyList {
        def unapplySeq[A](list: MyList[A]): Option[Seq[A]] =
            if (list == Empty) Some(Seq.empty)
            else unapplySeq(list.tail).map(list.head +: _)
    }
    val myList: MyList[Int] = Cons(1, Cons(2, Cons(3, Empty)))
    val decomposed = myList match {
        case MyList(1, 2, _*) => "Starts with 1 and 2"
        case _ => "?"
    }
    println(decomposed)

    // custom return types for unapply
    // isEmpty: Boolean, get: something
    abstract class Wrapper [T] {
        def isEmpty: Boolean
        def get: T
    }
    object PersonWrapper {
        def unapply(person: Person): Wrapper[String] = new Wrapper[String] {
            override def isEmpty: Boolean = false
            override def get: String = person.name
        }
    }
    println(bob match {
        case PersonWrapper(n) => s"This person's name is $n"
        case _ => "The man with no name"
    })
}
