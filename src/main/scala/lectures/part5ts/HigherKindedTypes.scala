package lectures.part5ts

object HigherKindedTypes extends App {
    trait HigherKindedType[F[_]]

    trait MyList[T] {
        def flatMap[B](f: T => MyList[B]): MyList[B]
    }
    trait MyOption[T] {
        def flatMap[B](f: T => MyOption[B]): MyOption[B]
    }
    // ...
    def multiply[A, B](listA: List[A], listB: List[B]): List[(A, B)] =
        for {
            a <- listA
            b <- listB
        } yield (a, b)
    def multiply[A, B](optionA: Option[A], optionB: Option[B]): Option[(A, B)] =
        for {
            a <- optionA
            b <- optionB
        } yield (a, b)

    // ...

    trait Monad[F[_], A] {
        def flatMap[B](f: A => F[B]): F[B]
        def map[B](f: A => B): F[B]
    }
    implicit class MonadList[A](list: List[A]) extends Monad[List, A] {
        override def flatMap[B](f: A => List[B]): List[B] = list.flatMap(f)
        override def map[B](f: A => B): List[B] = list.map(f)
    }
    implicit class MonadOption[A](option: Option[A]) extends Monad[Option, A] {
        override def flatMap[B](f: A => Option[B]): Option[B] = option.flatMap(f)
        override def map[B](f: A => B): Option[B] = option.map(f)
    }
    val intList = new MonadList[Int](List(0, 1, 2))
    intList.flatMap(x => List(x, x + 1))
    intList.map(_ * 2)

    def multiply[F[_], A, B](ma: Monad[F, A], mb: Monad[F, B]): F[(A, B)] = {
        for {
            a <- ma
            b <- mb
        } yield (a, b)
        /* ma.flatMap(a => mb.map(b => (a, b))) */
    }

    val res = multiply(new MonadList[Int](List(0, 1, 2)), new MonadList[String](List("a", "b", "c")))
    println(res)
    val res2 = multiply(new MonadOption[Int](Some(2)), new MonadOption[String](Some("scala")))
    println(res2)
    val res3 = multiply(List(0, 1, 2), List("a", "b", "c"))
    println(res3)
    val res4 = multiply(Some(2), Some("scala"))
    println(res4)
}
