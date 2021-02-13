package exercises

trait MySet[A] extends (A => Boolean) {
    /*
        Exercise: implement a functional set
     */
    def contains(el: A): Boolean
    def +(elem: A): MySet[A]
    def ++(anotherSet: MySet[A]): MySet[A]
    def map[B](f: A => B): MySet[B]
    def flatMap[B](f: A => MySet[B]): MySet[B]
    def filter(p: A => Boolean): MySet[A]
    def foreach(f: A => Unit): Unit
    def elementString: String
    def isEmpty: Boolean
    def remove(el: A): MySet[A]
    def remove(other: MySet[A]): MySet[A]
    def intersection(other: MySet[A]): MySet[A]
    def difference(other: MySet[A]): MySet[A]
    def unary_! : MySet[A]
    def apply(el: A): Boolean = contains(el)
}

//class MyList[A](val head: Option[A], val tail: Option[MySet[A]] = None) extends MySet[A] {
//    def contains(el: A): Boolean = {
//        if (head.isEmpty) false
//        else if (head.get == el) true
//        else if (tail.isEmpty) false
//        else tail.get.contains(el)
//    }
//
//    def +(elem: A): MySet[A] =
//        if (contains(elem)) this
//        else new MyList(Some(elem), Some(this))
//
//    def ++(anotherSet: MySet[A]): MySet[A] =
//        if (head.isEmpty) anotherSet
//        else if (tail.isEmpty) new MyList(head, Some(anotherSet))
//        else tail.get ++ anotherSet + head.get
//
//    def map[B](f: A => B): MySet[B] = {
//        if (head.isEmpty) new MyList(None)
//        else if (tail.isEmpty) new MyList(Some(f(head.get)))
//        else tail.get.map(f) + f(head.get)
//    }
//
//    def flatMap[B](f: A => MySet[B]): MySet[B] = {
//        if (head.isEmpty) new MyList(None)
//        else if (tail.isEmpty) f(head.get)
//        else tail.get.flatMap(f) ++ f(head.get)
//    }
//
//    def filter(p: A => Boolean): MySet[A] = {
//        if (head.isEmpty) new MyList(None)
//        else if(tail.isEmpty) {
//            if (p(head.get)) new MyList(head)
//            else new MyList(None)
//        }
//        else {
//            if (p(head.get)) tail.get.filter(p) + head.get
//            else tail.get.filter(p)
//        }
//    }
//
//    def foreach(f: A => Unit): Unit =
//        if(head.isEmpty) ()
//        else {
//            f(head.get)
//            if (tail.nonEmpty)
//                tail.get.foreach(f)
//        }
//    def apply(el: A): Boolean = contains(el)
//}

// alternative implementation

class NegatedSet[A](val originalSet: MySet[A]) extends MySet[A] {
    def contains(el: A): Boolean = !originalSet.contains(el)
    def +(elem: A): MySet[A] = !(originalSet + elem)
    def ++(anotherSet: MySet[A]): MySet[A] = !(originalSet ++ anotherSet)
    def map[B](f: A => B): MySet[B] = !(originalSet map f)
    def flatMap[B](f: A => MySet[B]): MySet[B] = !(originalSet flatMap f)
    def filter(p: A => Boolean): MySet[A] = !(originalSet filter p)
    def foreach(f: A => Unit): Unit = ???
    def unary_! : MySet[A] = originalSet
    def elementString: String = s"!$originalSet"
    def isEmpty: Boolean = !originalSet.isEmpty
    def remove(el: A): MySet[A] = !originalSet.remove(el)
    def remove(other: MySet[A]): MySet[A] = !originalSet.remove(other)
    def intersection(other: MySet[A]): MySet[A] = !originalSet.intersection(other)
    def difference(other: MySet[A]): MySet[A] = !originalSet.difference(other)
}

// all elements of type A that satisfy a property
class PropertyBasedSet[A](property: A => Boolean) extends MySet[A] {
    def contains(el: A): Boolean = property(el)

    def +(elem: A): MySet[A] = // new NonEmptySet[A](elem, this) // ???
        new PropertyBasedSet[A](x => property(x) || x == elem)

    def ++(anotherSet: MySet[A]): MySet[A] =
        new PropertyBasedSet[A](x => property(x) || anotherSet(x))

    def map[B](f: A => B): MySet[B] = politelyFail // new PropertyBasedSet[B]((el: B) => true) // no idea
    def flatMap[B](f: A => MySet[B]): MySet[B] = politelyFail
    def filter(p: A => Boolean): MySet[A] = new PropertyBasedSet[A]((el: A) => property(el) && p(el))
    def foreach(f: A => Unit): Unit = politelyFail
    def elementString: String = ???
    def isEmpty: Boolean = ???
    def remove(el: A): MySet[A] = filter(_ != el)
    def remove(other: MySet[A]): MySet[A] = ???
    def intersection(other: MySet[A]): MySet[A] = filter(other)
    def difference(other: MySet[A]): MySet[A] = filter(!other)
    def unary_! : MySet[A] = new PropertyBasedSet[A](!property(_))

    def politelyFail = throw new IllegalArgumentException("Really deep rabbit hole!")
}

class EmptySet[A] extends MySet[A] {
    def contains(el: A): Boolean = false
    def +(elem: A): MySet[A] = new NonEmptySet[A](elem)
    def ++(anotherSet: MySet[A]): MySet[A] = anotherSet
    def map[B](f: A => B): MySet[B] = new EmptySet[B]
    def flatMap[B](f: A => MySet[B]): MySet[B] = new EmptySet[B]
    def filter(p: A => Boolean): MySet[A] = this
    def foreach(f: A => Unit): Unit = ()
    def unary_! : MySet[A] = new PropertyBasedSet[A](_ => true)
    def elementString: String = ")"
    def isEmpty = true
    def remove(el: A): MySet[A] = this
    def remove(other: MySet[A]): MySet[A] = this
    def intersection(other: MySet[A]): MySet[A] = this
    def difference(other: MySet[A]): MySet[A] = other
}

class NonEmptySet[A](val head: A, val tail: MySet[A] = new EmptySet[A]) extends MySet[A] {
    def isEmpty: Boolean = false
    def contains(el: A): Boolean =
        if (head == el) true
        else tail.contains(el)

    def +(elem: A): MySet[A] =
        if (contains(elem)) this
        else new NonEmptySet[A](elem, this)

    def ++(anotherSet: MySet[A]): MySet[A] =
        tail ++ anotherSet + head

    def map[B](f: A => B): MySet[B] =
        tail.map(f) + f(head)

    def flatMap[B](f: A => MySet[B]): MySet[B] =
        tail.flatMap(f) ++ f(head)

    def filter(p: A => Boolean): MySet[A] =
        if (p(head)) tail.filter(p) + head
        else tail.filter(p)

    def foreach(f: A => Unit): Unit = {
        f(head)
        tail.foreach(f)
    }
    def elementString: String = s"$head${if(tail.isEmpty) "" else " "}${tail.elementString}"
    def remove(el: A): MySet[A] =
        if (head == el) tail
        else tail.remove(el) + head

    def remove(other: MySet[A]): MySet[A] =
        if (other.contains(head)) tail.remove(other)
        else tail.remove(other) + head

    def intersection(other: MySet[A]): MySet[A] =
        filter(other)

    def difference(other: MySet[A]): MySet[A] = {
        val inter = intersection(other)
        (this ++ other).remove(inter)
    }

    def unary_! : MySet[A] = new PropertyBasedSet[A](!contains(_))
    override def toString(): String = s"($elementString"
}

object MySet {
    def apply[A](values: A*): MySet[A] =
        values.foldLeft[MySet[A]](new EmptySet[A])((acc: MySet[A], el: A) => acc + el)
}

object TestExercise extends App {
    val x = MySet(1, 2, 3, 4, 5)
    println(x)
    val y = x + 5
    println(y)
    val z = y + 6
    println(z)
    val a = x + 7
    println(a)
    val b = z ++ a
    println(b)
    println(b map(_ * 10))
    println(b filter (_ % 2 == 0))
    println(b flatMap (el => MySet(el, el * 10)))
    println(z intersection MySet(2, 3, 4, 10, 12, 13))

    val s = MySet(1, 2, 3, 4)
    val negative = !s
    println(s"negative(0) = ${negative(0)}")
    println(s"negative(1) = ${negative(1)}")
    println(s"negative(2) = ${negative(2)}")
    println(s"negative(3) = ${negative(3)}")
    println(s"negative(4) = ${negative(4)}")
    println(s"negative(5) = ${negative(5)}")

    val negativeEven = negative.filter(_ % 2 == 0)
    println(s"negativeEven(0) = ${negativeEven(0)}")
    println(s"negativeEven(4) = ${negativeEven(4)}")
    println(s"negativeEven(5) = ${negativeEven(5)}")
    println(s"negativeEven(6) = ${negativeEven(6)}")
    println(s"negativeEven(7) = ${negativeEven(7)}")
    println(s"negativeEven(8) = ${negativeEven(8)}")

    val negativeEvenPlus5 = negativeEven + 5

    println(s"negativeEvenPlus5(0) = ${negativeEvenPlus5(0)}")
    println(s"negativeEvenPlus5(4) = ${negativeEvenPlus5(4)}")
    println(s"negativeEvenPlus5(5) = ${negativeEvenPlus5(5)}")
    println(s"negativeEvenPlus5(6) = ${negativeEvenPlus5(6)}")
    println(s"negativeEvenPlus5(7) = ${negativeEvenPlus5(7)}")
    println(s"negativeEvenPlus5(8) = ${negativeEvenPlus5(8)}")
}