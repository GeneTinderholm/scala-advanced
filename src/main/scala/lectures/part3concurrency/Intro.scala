package lectures.part3concurrency

import java.util.concurrent.Executors

object Intro extends App {
    // jvm threads
    val threads =
        for {i <- 1 to 10}
        yield new Thread(() => println(s"running in thread $i"))
    threads.foreach(_.start())
    threads.foreach(_.join())

    // executors
    val pool = Executors.newFixedThreadPool(10)
    pool.execute(() => println("something in the thread pool"))

    pool.execute(() => {
        Thread.sleep(1000)
        println("done after 1 second")
    })
    pool.execute(() => {
        Thread.sleep(1000)
        println("almost done")
        Thread.sleep(1000)
        println("done after 2 seconds")
    })
//    pool.shutdownNow() // interrupts sleeping threads
    pool.shutdown()

//    def runInParallel = {
//        var x = 0
//        val thread1 = new Thread(() => {
//            x = 1
//        })
//        val thread2 = new Thread(() => {
//            x = 2
//        })
//        thread1.start()
//        thread2.start()
//        thread1.join()
//        thread2.join()
//        x
//    }
//    for (_ <- 1 to 1000) println(runInParallel)

    class BankAccount(var amount: Int) {
        override def toString: String = s"$amount"
    }
    def buy(account: BankAccount, thing: String, price: Int): Unit = {
        account.amount -= price
//        println(s"I've bought $thing")
//        println(s"my account is now: $account")
    }
//    for (_ <- 1 to 1000) {
//        val account = new BankAccount(50000)
//        val thread1 = new Thread(() => buy(account, "shoes", 3000))
//        val thread2 = new Thread(() => buy(account, "phone", 4000))
//        thread1.start()
//        thread2.start()
//        thread1.join()
//        thread2.join()
//        if (account.amount != 43000) println(s"Aha: ${account.amount}")
//    }
    def buySafe(account: BankAccount, thing: String, price: Int): Unit =
        account.synchronized {
            account.amount -= price
        }

    // exercises
    // inception threads (50) print greeting in reverse order
    def printGreeting(threadNum: Int, totalThreads: Int): Unit = {
        if (threadNum != totalThreads) {
            val anotherThread = new Thread(() => printGreeting(threadNum + 1, totalThreads))
            anotherThread.start()
            anotherThread.join()
        }
        println(s"Greetings from thread number $threadNum")
    }

    printGreeting(1, 50)
}
