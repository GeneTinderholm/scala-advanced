package lectures.part4implicits

import scala.annotation.tailrec

object PimpMyLibrary extends App {
    // 2.isPrime
    implicit class RichInt(val value: Int) extends AnyVal { // AnyVal is a memory optimization
        def isEven: Boolean = value % 2 == 0
        def sqrt: Double = Math.sqrt(value)
    }
    println(42.isEven)
    println(42.sqrt)

    // type enrichment (pimping)
    import scala.concurrent.duration._
    3.seconds // implicit conversion

    // exercise
    // enrich string class
    /*
        - asInt
        - encrypt (caesar cypher) takes int
     */
    implicit class RichString(str: String) {
        def asInt: Int = {
            val isNegative = str.head == '-'
            @tailrec
            def asIntHelper(current: String, acc: Int): Int =
                if (current.isEmpty) acc
                else {
                    val charAsInt = current.head.toInt - 48 // hex offset of 0 in ascii
                    val nextAcc = acc * 10 + (if (isNegative) -charAsInt else charAsInt)
                    asIntHelper(current.tail, nextAcc)
                }
            asIntHelper(if (isNegative) str.tail else str, 0)
        }
        def encrypt(offset: Int): String = {
            val zValue = 'z'.toInt
            val ZValue = 'Z'.toInt
            @tailrec
            def encryptHelper(encrypted: String, remaining: String): String = {
                if(remaining.isEmpty) encrypted
                else if (!remaining.head.isLetter) encryptHelper(encrypted + remaining.head, remaining.tail)
                else {
                    var newLetter = remaining.head.toInt + offset
                    while (remaining.head.isLower && newLetter > zValue ||
                        remaining.head.isUpper && newLetter > ZValue)
                        newLetter -= 26
                    encryptHelper(encrypted + newLetter.toChar, remaining.tail)
                }
            }
            encryptHelper("", str)
        }
    }
    println("52".asInt + 3)
    println("-52".asInt)
    println("0".asInt)
    println("This is a caesar cypher".encrypt(12))
    // keep enriching the int class
    /*
        times(function) // do thing that many times
        * (List) => 3 * List(1,2) => List(1, 2, 1, 2, 1, 2)
     */
    implicit class EnhancedInt(i: Int) {
        def times(f: () => Any): Unit = {
            if(i <= 0) ()
            else {
                f()
                (i - 1).times(f)
            }
        }
        def *[T](list: List[T]): List[T] = {
            if (i <= 0) Nil
            else list ++ (i - 1) * list
        }
    }
    4 times (() => println("hello"))
    println(3 * List(1, 2))

    implicit def stringToInt(string: String): Int = Integer.valueOf(string)

    println("8" / 4)
    implicit def intToBoolean(i: Int): Boolean = i != 0

    if (1) print("It worked")
    else print("no luck")
}
