package lectures.part3concurrency

import scala.collection.mutable
import scala.util.Random

object ThreadCommunication extends App {
    /*
        The producer-consumer problem

        producer -> [ x ] -> consumer
     */
    class SimpleContainer {
        private var value: Int = 0

        def isEmpty: Boolean = value == 0
        def get: Int = {
            val result = value
            value = 0
            result
        }
        def set(newValue: Int): Unit = {
            value = newValue
        }
    }

    def naiveProdCons(): Unit = {
        val container = new SimpleContainer
        val consumer = new Thread(() => {
            println("[consumer] waiting...")
            while (container.isEmpty) {
                println("[consumer] actively waiting...")
            }
            println(s"[consumer] I have consumed ${container.get}")
        })
        val producer = new Thread(() => {
            println("[producer] computing...")
            Thread.sleep(500)
            val value = 42
            println(s"[producer] I have produced, after long work, the value: $value")
            container.set(value)
        })
        consumer.start()
        producer.start()
    }
//    naiveProdCons()
    def smartProdCons(): Unit = {
        val container = new SimpleContainer
        val consumer = new Thread(() => {
            println("[consumer] waiting...")
            container.synchronized {
                container.wait()
            }
            println(s"[consumer] I have consumed ${container.get}")
        })
        val producer = new Thread(() => {
            println("[producer] computing...")
            Thread.sleep(2000)
            val value = 42
            container.synchronized {
                println(s"[producer] I have produced, after long work, the value: $value")
                container.set(value)
                container.notify()
            }
        })
        consumer.start()
        producer.start()
    }

//    smartProdCons()
    /*
        producer -> [ ? ? ? ] -> consumer
     */

    def prodConsLargeBuffer(): Unit = {
        val buffer = new mutable.Queue[Int]
        val capacity = 3
        val consumer = new Thread(() => {
            val random = new Random()
            while (true) {
                buffer.synchronized {
                    if (buffer.isEmpty) {
                        println("[consumer] buffer empty, waiting...")
                        buffer.wait()
                    }
                    val x = buffer.dequeue()
                    println(s"[consumer] consumed $x")

                    // todo
                    buffer.notify()
                }

                Thread.sleep(random.nextInt(500))
            }
        })
        val producer = new Thread(() => {
            val random = new Random()
            var i = 0
            while (true) {
                buffer.synchronized {
                    if (buffer.size == capacity) {
                        println("[producer] buffer is full, waiting...")
                        buffer.wait()
                    }
                    buffer.enqueue(i)
                    println(f"[producer] produced $i")
                    buffer.notify()
                    i += 1
                }
                Thread.sleep(random.nextInt(250))
            }
        })
        consumer.start()
        producer.start()
        consumer.join()
    }
//    prodConsLargeBuffer()
    /*
        producer1 -> [ ? ? ? ] -> consumer 1
        producer2 -> ^^^^^^^^^ -> consumer 2
     */
    def multiActorProdCons(): Unit = {
        val buffer = new mutable.Queue[Int]
        val capacity = 3
        def consumerCallback (i: Int): Unit = {
            val random = new Random()
            while (true) {
                buffer.synchronized {
                    while (buffer.isEmpty) {
                        println(f"[consumer$i] buffer empty, waiting...")
                        buffer.wait()
                    }
                    val x = buffer.dequeue()
                    println(s"[consumer$i] consumed $x")

                    // todo
                    buffer.notify()
                }

                Thread.sleep(random.nextInt(500))
            }
        }
        def producerCallback (i: Int): Unit = {
            val random = new Random()
            var x = 0
            while (true) {
                buffer.synchronized {
                    while (buffer.size == capacity) {
                        println(s"[producer$i] buffer is full, waiting...")
                        buffer.wait()
                    }
                    buffer.enqueue(x)
                    println(f"[producer$i] produced $x")
                    buffer.notify()
                    x += 1
                }
                Thread.sleep(random.nextInt(500))
            }
        }
        val consumer1 = new Thread(() => consumerCallback(1))
        val consumer2 = new Thread(() => consumerCallback(2))
        val producer1 = new Thread(() => producerCallback(1))
        val producer2 = new Thread(() => producerCallback(2))
        consumer1.start()
        consumer2.start()
        producer1.start()
        producer2.start()
        consumer1.join()
    }
//    multiActorProdCons()
    def createDeadlock(): Unit = {
        val resource1 = List(1)
        val resource2 = List(2)
        val thread1 = new Thread(() => {
            println("[thread1] getting resource 1")
            resource1.synchronized {
                println("[thread1] got resource 1")
                println("[thread1] getting resource 2")
                resource2.synchronized {
                    println("[thread1] have both resources")
                }
            }
        })
        val thread2 = new Thread(() => {
            println("[thread2] getting resource 2")
            resource2.synchronized {
                println("[thread2] got resource 2")
                println("[thread2] getting resource 1")
                resource1.synchronized {
                    println("[thread2] have both resources")
                }
            }
        })
        thread1.start()
        thread2.start()
        thread1.join()
    }
//    createDeadlock()
}
