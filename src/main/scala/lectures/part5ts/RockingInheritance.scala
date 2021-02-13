package lectures.part5ts

object RockingInheritance extends App {
    trait Writer[T] {
        def write(value: T): Unit
    }

    trait Closeable {
        def close(status: Int): Unit
    }

    trait GenericStream[T] {
        def foreach(f: T => Unit): Unit
    }

    def processStream[T](stream: GenericStream[T] with Writer[T] with Closeable): Unit = {
        stream.foreach(stream.write)
        stream.close(0)
    }

    // diamond problem
    trait Animal {
        def name: String
    }
    trait Lion extends Animal {
        override def name: String = "Lion"
    }
    trait Tiger extends Animal {
        override def name: String = "Tiger"
    }
    class Mutant extends Lion with Tiger
    val mutant = new Mutant
    println(mutant.name) // Tiger
    /*
        Mutant Extends Animal with Lion with Tiger
        traits are applied in order
        ones to the right override earlier traits
     */

    // the super problem (type linearization)

    trait Cold {
        def print(): Unit = println("Cold")
    }
    trait Green extends Cold {
        override def print(): Unit = {
            println("green")
            super.print()
        }
    }

    trait Blue extends Cold {
        override def print(): Unit = {
            println("blue")
            super.print()
        }
    }

    class Red {
        def print(): Unit = println("red")
    }

    class White extends Red with Green with Blue {
        override def print(): Unit = {
            println("White")
            super.print()
        }
    }
    val white = new White
    white.print() // prints White blue green Cold
    /*
        White's actual type is:
        AnyRef with Red with Cold with Green with Blue with White
        white calls super, which calls blue
        blue calls super, which calls green
        green calls super which calls cold

        super just looks to the type immediately to the left in the type linearization
     */
}
