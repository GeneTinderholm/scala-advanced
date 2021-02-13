package lectures.part1as

import scala.util.Try

object DarkSyntaxSugar extends App {
    // methods with single param
    def singleArgMethod(arg: Int): String = s"arg little ducks"
    val description = singleArgMethod {
        // code goes here
        42
    }
    val aTryInstance = Try {
        // code
        throw new RuntimeException
    }
    List(1, 2, 3).map { el =>
        el + 1
    }

    // single abstract method
    trait Action {
        def act(x: Int): Int
    }
    val x: Action = new Action {
        override def act(x: Int): Int = x + 42
    }
    val y: Action = x => x + 42
    val z: Action = _ + 42
    val aThread = new Thread(() => println("Hello, Scala!")) // runnable
    abstract  class AnAbstractType {
        def implemented = 23
        def f(a: Int): Unit
    }
    val anAbstractInstance: AnAbstractType = (a: Int) => println(a) // overrides abstract method

    //  :: and #:: are special (and all methods ending in `:`)
    val prependedList = 2 :: List(3, 4) // not a method on Int, rewrites to List(3, 4).::(2)
    // last character decides associativity, methods ending in `:` are right associative instead of left
    class Thing {
        def -->:(value: Int): Int = 42 + value
    }
    val result = 2 -->: new Thing
    println(result)

    // multi-word method naming
    class TeenGirl(name: String) {
        def `and then said`(gossip: String) = println(s"$name said $gossip")
    }
    val lilly = new TeenGirl("Lilly")
    lilly `and then said` "things"

    // infix types
    class Composite[A, B]
    val composite: Composite[Int, String] = new Composite[Int, String]
    val composite2: Int Composite String = new Composite[Int, String]
    class -->[A, B]
    val towards: Int --> String = new -->[Int, String]

    // update method is special (like apply)
    val anArray = Array(1, 2, 3)
    anArray(2) = 7 // anArray.update(2, 7)
    // used in mutable collections

    // setters for mutable containers
    class Mutable {
        private var internalMember: Int = 0
        def member:Int = internalMember
        def member_=(value: Int): Unit = {
            internalMember = value
        }
    }
    val mutt = new Mutable
    mutt.member = 42
    println(mutt.member)
}
