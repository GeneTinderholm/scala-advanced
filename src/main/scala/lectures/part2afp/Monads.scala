package lectures.part2afp

object Monads extends App {
    trait Attempt[+A] {
        def flatMap[B](f: A => Attempt[B]): Attempt[B]
        def map[B](f: A => B): Attempt[B]
    }
    object Attempt {
        def apply[A](a: => A): Attempt[A] =
            try {
                Success(a)
            } catch {
                case e: Throwable => Fail(e)
            }
        def flatten[A](a: Attempt[Attempt[A]]): Attempt[A] = a.flatMap(x => x)
    }
    case class Success[+A](value: A) extends Attempt[A] {
        override def flatMap[B](f: A => Attempt[B]): Attempt[B] =
            try {
                f(value)
            } catch {
                case e: Throwable => Fail(e)
            }

        override def map[B](f: A => B): Attempt[B] = Attempt(f(value))
    }
    case class Fail(e: Throwable) extends Attempt[Nothing] {
        override def flatMap[B](f: Nothing => Attempt[B]): Attempt[B] = this
        override def map[B](f: Nothing => B): Attempt[B] = this
    }
    val attempt = Attempt {
        throw new RuntimeException("Doesn't crash!")
    }
    println(attempt)

    trait Lazy[T] {
        def use: T
        def flatMap[S](f: (=> T) => Lazy[S]): Lazy[S]
    }
    object Lazy {
        def apply[T](el: => T): Lazy[T] = new LazyImpl(el)
    }
    class LazyImpl[T](computation: => T) extends Lazy [T] {
        private lazy val internal = computation
        def use: T = internal
        def flatMap[S](f: (=> T) => Lazy[S]): Lazy[S] = f(computation)
    }

    val lazee = Lazy((() => {
        Thread.sleep(4000)
        println("things")
        4
    })())
    println("can still do things here")
    val anotherLazy = lazee.flatMap(el => Lazy(el + 1))
    println(anotherLazy.use)
    println(anotherLazy.use)
    val x = List(List(1, 2, 3), List(4, 5))
    println(x.flatMap(x => x))
    println(x.flatten)
}
