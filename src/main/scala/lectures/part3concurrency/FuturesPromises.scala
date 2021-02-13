package lectures.part3concurrency

import scala.concurrent.duration.{Duration, DurationInt}
import scala.concurrent.{Await, Future, Promise}
import scala.util.{Failure, Random, Success, Try}

// important, handles thread execution of futures
import scala.concurrent.ExecutionContext.Implicits.global

object FuturesPromises extends App {
    def calculateMeaningOfLife: Int = {
        Thread.sleep(2000)
        42
    }

    val aFuture = Future {
        calculateMeaningOfLife // calculates on another thread
    } // (global) // implied

    println(aFuture.value) // none (Option[Try[Int]]
    aFuture.onComplete {
        case Success(value) => println(value)
        case Failure(exception) => println(s"whoops $exception")
    }
    Thread.sleep(3000)

    // mini social network
    case class Profile(id: String, name: String) {
        def poke(other: Profile): Unit = {
            println(s"${this.name} poking ${other.name}")
        }
    }
    object SocialNetwork {
        // "database"
        val names = Map(
            "fb.id.1-zuck" -> "Mark",
            "fb.id.2-bill" -> "Bill",
            "fb.id.0-dummy" -> "Dummy"
        )
        val friends = Map(
            "fb.id.1-zuck" -> "fb.id.2-bill"
        )
        val random = new Random()

        // API
        def fetchProfile(id: String): Future[Profile] = Future {
            Thread.sleep(random.nextInt(300))
            Profile(id, names(id))
        }
        def fetchBestFriend(profile: Profile): Future[Profile] = Future {
            Thread.sleep(random.nextInt(400))
            val bestFriendId = friends(profile.id)
            Profile(bestFriendId, names(bestFriendId))
        }
    }
    val mark = SocialNetwork.fetchProfile("fb.id.1-zuck")
    mark.onComplete {
        case Success(markProfile) =>
            val bill = SocialNetwork.fetchBestFriend(markProfile)
            bill.onComplete {
                case Success(billProfile) => markProfile.poke(billProfile)
            }
    }

    // functional composition of futures
    // map, flatMap, filter
    val namOnTheWall = mark.map(_.name)
    val marksBestFriend = mark.flatMap(SocialNetwork.fetchBestFriend)

    for {
        mark <- SocialNetwork.fetchProfile("fb.id.1-zuck")
        bill <- SocialNetwork.fetchBestFriend(mark)
    } mark.poke(bill)
    Thread.sleep(1000)

    // fallback path
    val aProfileNoMatterWhat = SocialNetwork.fetchProfile("bad id").recover {
        case e: Throwable => Profile("fb.id.0-dummy", "Dummy")
    }
    val aFetchedProfileNoMatterWhat = SocialNetwork.fetchProfile("bad id").recoverWith {
        case e: Throwable => SocialNetwork.fetchProfile("fb.id.0-dummy")
    }
    val fallbackResult = SocialNetwork.fetchProfile("bad id").fallbackTo(SocialNetwork.fetchProfile("fb.id.0-dummy"))


    // online banking ap
    case class User(name: String)
    case class Transaction(sender: String, receiver: String, amount: Double, status: String)

    object BankingApp {
        val name = "Globocorp online banking"

        def fetchUser(name: String): Future[User] = Future {
            Thread.sleep(500)
            User(name)
        }
        def createTransaction(user: User, merchantName: String, amount: Double): Future[Transaction] = Future {
            Thread.sleep(1000)
            Transaction(user.name, merchantName, amount, "SUCCESS")
        }
        def purchase(username: String, item: String, merchantName: String, cost: Double): String = {
            val transactionStatusFuture = for {
                user <- fetchUser(username)
                transaction <- createTransaction(user, merchantName, cost)
            } yield transaction.status
            Await.result(transactionStatusFuture, 2.seconds)
        }
    }
    println(BankingApp.purchase("Gene", "Computer", "System76", 3000))

    // promises
    val promise = Promise[Int]()
    val future = promise.future

    future.onComplete {
        case Success(r) => println(s"[consumer] I've received $r")
    }
    val producer = new Thread(() => {
        println("[producer] crunching numbers...")
        Thread.sleep(500)
        promise.success(42)
        println("[producer] done")
    })
    producer.start()
    producer.join()
    // 1. future fulfilled immediately
    val immediateFuture = Future { 42 }
    // 2. two futures in sequence
    def inSequence[A, B](a: Future[A], b: Future[B]): Future[B] = a.flatMap(_ => b)
    // 3. first(a: Future[Int], b: Future[Int]) return future with the value of first one to complete
    def first[A](a: Future[A], b: Future[A]): Future[A] = {
        val promise = Promise[A]()
        def completeHandler(res: Try[A]): Unit = res match {
            case Success(res) => if (!promise.isCompleted) promise.success(res)
            case Failure(err) => if (!promise.isCompleted) promise.failure(err)
        }
        a.onComplete(completeHandler)
        b.onComplete(completeHandler)
        promise.future
    }
    def last[A](a: Future[A], b: Future[A]): Future[A] = {
        val promise = Promise[A]()
        def checkOtherFuture(other: Future[A])(res: Try[A]): Unit = res match {
            case Success(res) => if (other.isCompleted) promise.success(res)
            case Failure(err) => if (other.isCompleted) promise.failure(err)
        }
        a.onComplete(checkOtherFuture(b))
        b.onComplete(checkOtherFuture(a))
        promise.future
    }
    val futA = Future {
        Thread.sleep(250)
        42
    }
    val futB = Future {
        Thread.sleep(250)
        50
    }
    val fst = first(futA, futB)
    val lst = last(futA, futB)

    println(Await.result(fst, Duration.Inf))
    println(Await.result(lst, Duration.Inf))

//    def retryUntil[T](action: () => Future[T], condition: T => Boolean): Future[T] = {
//        val promise = Promise[T]()
//        action().onComplete {
//            case Success(res) => if(condition(res)) promise.success(res) else promise.completeWith(retryUntil(action, condition))
//            case Failure(_) => promise.completeWith(retryUntil(action, condition))
//        }
//        promise.future
//    }
    def retryUntil[T](action: () => Future[T], condition: T => Boolean): Future[T] =
        action()
            .filter(condition)
            .recoverWith {
                case _ => retryUntil(action, condition)
            }
}
