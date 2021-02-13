package lectures.part3concurrency

import java.util.concurrent.atomic.AtomicReference
import scala.collection.parallel.CollectionConverters._
import scala.collection.parallel.immutable.ParVector

object ParallelUtils extends App {
    // parallel collections
    val parList = List(1, 2, 3).par
    val aParVector = ParVector[Int](1, 2, 3)

    def measure[T](operation: => T): Long = {
        val time = System.nanoTime()
        operation
        System.nanoTime() - time
    }
    val list = (1 to 20000000).toList
    val serialTime = measure {
        list.map(_ + 1)
    }
    println(s"Serial time:   $serialTime")
    val parallelTime = measure {
        list.par.map(_ + 1)
    }
    println(s"Parallel time: $parallelTime")

    // atomic ops and references
    val atomic = new AtomicReference[Int](2)
    val currentValue = atomic.get() // thread-safe read
    atomic.set(4) // thread-safe write

    val newVal = atomic.getAndSet(5)
    atomic.compareAndSet(38, 56) // if value == 38, atomic.set(56) else do nothing
    // reference equality

    atomic.updateAndGet(_ + 1)
    atomic.getAndUpdate(_ + 1)

    atomic.accumulateAndGet(12, _ + _) // value += 12, but thread safe
    atomic.getAndAccumulate(12, _ + _)
}
