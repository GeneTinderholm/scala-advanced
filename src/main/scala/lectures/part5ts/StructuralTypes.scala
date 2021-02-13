package lectures.part5ts

import scala.language.reflectiveCalls

object StructuralTypes extends App {
    type JavaCloseable = java.io.Closeable

    class HipsterCloseable {
        def close(): Unit = println("yeah yeah, I'm closing...")
    }

//    def closeQuietly(closeable: JavaCloseable OR HispterCloseable)

    // structural type
    type UnifiedCloseable = {
        def close(): Unit
    }

    def closeQuietly(unifiedCloseable: UnifiedCloseable): Unit = unifiedCloseable.close()

    closeQuietly(new JavaCloseable {
        override def close(): Unit = println("closing verbosely")
    })
    closeQuietly(new HipsterCloseable)

    type AdvancedCloseable = JavaCloseable {
        def closeSilently(): Unit
    }

    class AdvancedJavaCloseable extends JavaCloseable {
        override def close(): Unit = println("CLOSING...")
        def closeSilently(): Unit = println("closing...")
    }

    def closeShh(advancedCloseable: AdvancedCloseable): Unit = advancedCloseable.closeSilently()

    closeShh(new AdvancedJavaCloseable)

    def altClose(closeable: { def close(): Unit }): Unit = closeable.close()

    altClose(new AnyRef {
        def close(): Unit = println("close")
    })

    /*
        Exercise
     */
    trait CBL[+T] {
        def head: T
        def tail: CBL[T]
    }

    object ConsBasedNil extends CBL[Nothing] {
        def head: Nothing = ???
        def tail: CBL[Nothing] = ???
    }
    class ConsBasedList[+T](hd: T, tl: CBL[T]) extends CBL[T] {
        def head: T = hd
        def tail: CBL[T] = tl
    }

    class Brain {
        override def toString: String = "Brain"
    }
    class Human {
        def head: Brain = new Brain
    }

    def f[T](somethingWithAHead: { def head: T }): Unit = println(somethingWithAHead.head)

    f(new Human)
    f(new ConsBasedList[Int](4, ConsBasedNil))

    object HeadEqualizer {
        type Headable[T] = { def head: T }
        def ===[T](a: Headable[T], b: Headable[T]): Boolean = a.head == b.head
    }
}
