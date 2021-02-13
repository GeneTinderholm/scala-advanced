package lectures.part4implicits

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object MagnetPattern extends App {
    // method overloading

    class P2PRequest
    class P2PResponse
    class Serializer[T]
    trait Actor {
        def receive(statusCode: Int): Int
        def receive(request: P2PRequest): Int
        def receive(response: P2PResponse): Int
        def receive[T : Serializer](message: T): Int
        def receive[T : Serializer](message: T, statusCode: Int): Int
        def receive(future: Future[P2PRequest]): Int
//        def receive(future: Future[P2PResponse]): Int // cannot do, type erasure
        // lots of overloads
    }
    /*
        Problems
        1. type erasure
        2. lifting doesn't work for all overloads
        3. code duplication
        4. type inference and default args
     */

    trait MessageMagnet[Result] {
        def apply(): Result
    }
    def receive[R](magnet: MessageMagnet[R]): R = magnet()

    implicit class FromP2PRequest(request: P2PRequest) extends MessageMagnet[Int] {
        def apply(): Int = {
            // logic goes here
            println("Handling P2PRequest")
            42
        }
    }
    implicit class FromP2PResponse(response: P2PResponse) extends MessageMagnet[Int] {
        def apply(): Int = {
            // logic goes here
            println("Handling P2PResponse")
            24
        }
    }
    receive(new P2PResponse)
    receive(new P2PRequest)

    // no more type erasure problems

    implicit class FromResponseFuture(future: Future[P2PResponse]) extends MessageMagnet[Int] {
        def apply(): Int = {
            println("Handling Future[P2PResponse]")
            2
        }
    }
    implicit class FromRequestFuture(future: Future[P2PRequest]) extends MessageMagnet[Int] {
        def apply(): Int = {
            println("Handling Future[P2PRequest]")
            3
        }
    }
    println(receive(Future(new P2PResponse)))
    println(receive(Future(new P2PRequest)))
    // compiler looks for implicits before types are erased

    // lifting also works
    trait MathLib {
        def add1(x: Int): Int = x + 1
        def add1(x: String): Int = x.toInt + 1
        // ...
    }
    trait AddMagnet {
        def apply(): Int
    }
    def add1(magnet: AddMagnet): Int = magnet()

    implicit class AddInt(x: Int) extends AddMagnet {
        def apply(): Int = x + 1
    }
    implicit class AddString(s: String) extends AddMagnet {
        def apply(): Int = s.toInt + 1
    }
    // ...
    val addFV = add1 _
    println(addFV(1))
    println(addFV("3"))

    // lifting in this case really only works when everything has the same type

    /*
        Drawbacks
        - really verbose
        - harder to read
        - can't name or place default arguments
        - call by name doesn't work correctly (side effects may be evaluated early)
     */
}
