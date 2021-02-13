package lectures.part2afp

object PartialFunctions extends App {
    val aFunction = (x: Int) => x + 1
    class FunctionNotApplicableException extends RuntimeException
    val aFussyFunction = (x: Int) =>
        if (x == 1) 42
        else if (x == 2) 56
        else if (x == 5) 999
        else throw new FunctionNotApplicableException
    val aNicerFussyFunction = (x: Int) => x match {
        case 1 => 42
        case 2 => 56
        case 5 => 999
        case _ => throw new FunctionNotApplicableException
    }
    val aPartialFunction: PartialFunction[Int, Int] = {
        case 1 => 42
        case 2 => 56
        case 5 => 999
    } // partial function value
    println(aPartialFunction(1))
    println(aPartialFunction(2))
    println(aPartialFunction(5))
//    println(aPartialFunction(6)) // match error

    // Partial Function utils
    println(aPartialFunction.isDefinedAt(5))
    println(aPartialFunction.isDefinedAt(67))

    // lift to total function
    val lifted = aPartialFunction.lift // Int => Option[Int]
    println(lifted(5))
    println(lifted(67))

    val pfChain = aPartialFunction.orElse[Int, Int]({
        case 45 => 67
    })
    println(pfChain(2))
    println(pfChain(45))

    // Partial functions extend normal functions
    val aTotalFunction: Int => Int = {
        case 1 => 99
    }
    // Higher order functions accept partial functions
    val aMappedList = List(1, 2, 3, 1, 3, 2, 1).map {
        case 1 => 42
        case 2 => 78
        case 3 => 1000
    }
    println(aMappedList)

    // partial functions can only have one parameter type

    // Exercises
    /*
        1. construct a PF instance (anonymous class)
        2. dumb chatbot as partial function
     */

    // 1
    val pf = new PartialFunction[Int, Int] {
        override def isDefinedAt(x: Int): Boolean = x == 1 || x == 2 || x == 4
        override def apply(x: Int): Int = x match {
            case 1 => 4
            case 2 => 1
            case 4 => 2
        }
    }
    println(pf(1))
    println(pf(2))
    println(pf(4))

    // 2
    val chatbot: PartialFunction[String, String] = {
        case "Hi" => "Hello"
        case "How are you?" => "Fine."
        case "Do you like cheese?" => "Yes"
    }
    scala.io.Source.stdin.getLines().map(chatbot).foreach(println)
}
