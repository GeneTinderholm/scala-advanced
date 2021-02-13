package lectures.part5ts

object TypeMembers extends App {
    class Animal
    class Dog extends Animal
    class Cat extends Animal

    class AnimalCollection {
        type AnimalType // abstract type member
        type BoundedAnimal <: Animal // must extend animal (Animal, Dog, Cat)
        type SuperBoundedAnimal >: Dog <: Animal // must extend animal, and be a supertype of dog (Dog)
        type AnimalC = Cat
    }
    val ac = new AnimalCollection
//    val dog: ac.AnimalType = ???
//    val cat: ac.BoundedAnimal = new Dog
    val pup: ac.SuperBoundedAnimal = new Dog
    val cat: ac.AnimalC = new Cat

    type CatAlias = Cat
    val anotherCat: CatAlias = new Cat

    // alternative to generics
    trait MyList {
        type T
        def add(el: T): MyList
    }
    class NonEmptyList(value: Int) extends MyList {
        override type T = Int
        override def add(el: Int): MyList = new NonEmptyList(value + el)
    }

    // .type
    type CatsType = cat.type
    val newCat: CatsType = cat
//    new CatsType // doesn't have a constructor

    /*
        Exercise - enforce a type to be applicable to SOME TYPES only
     */
    trait MList {
        type A
        def head: A
        def tail: MList
    }

    trait NList extends MList {
        type N <: Number
        override type A = N
        def head: N
        def tail: NList
    }

    trait ApplicableToNumbers {
        type A <: Number
    }

    // make this not compile
//    class CustomList(hd: String, tl: CustomList) extends NList {
//        type N = String
//        def head: String = hd
//        def tail: CustomList = tl
//    }
    // this should still be ok
    // only could get it working with java ints
    class IntList(hd: java.lang.Integer, tl: IntList) extends NList {
        override type N = java.lang.Integer
        def head: java.lang.Integer = hd
        def tail: IntList = tl
    }
}
