package lectures.part5ts

object RecursiveTypesAndFBoundedPolymorphism extends App {
    // recursive type: F-Bounded Polymorphism
    trait Animal[A <: Animal[A]] { self: A => // self argument
        def breed: List[Animal[A]]
    }

    class Cat extends Animal[Cat] {
        override def breed: List[Cat] = List(new Cat) // forces return type to be a List[Cat] (or List[Animal[Cat]])
    }

    class Crocodile extends Animal[Crocodile] {
        override def breed: List[Crocodile] = List(new Crocodile)
    }
    // limitation, f-bounded polymorphism stops being effective when your type hierarchy has many levels

    // alternate implementation with type classes
    trait Fish[A] {
        def breed(a: A): List[A]
    }
    class Cod
    object Cod {
        implicit object CodCanBreed extends Fish[Cod] {
            def breed(a: Cod): List[Cod] = List(new Cod)
        }
    }
    implicit class CanBreedOps[A](animal: A) {
        def breed(implicit canBreed: Fish[A]): List[A] = canBreed.breed(animal)
    }
    val cod = new Cod
    cod.breed
}
