package lectures.part5ts

/*
 * Cheat sheet
 * ===========
 * constructor args = covariant position
 * method args = contravariant position
 * method return types = covariant position
 *
 * Rule of thumb
 * use covariance = collection of things
 * use contravariance = group of actions
 */

object Variance extends App {
    trait Animal
    class Dog extends Animal
    class Cat extends Animal
    class Crocodile extends Animal

    // variance is the problem of type substitution of generics

    class Cage[T] // should Cage[Cat] inherit from Cage[Animal]?
    // covariance - yes
    class CovCage[+T]
    val covCage: CovCage[Animal] = new CovCage[Cat]

    // invariance - no
    class InvCage[T]
    val invCage: InvCage[Cat] = new InvCage[Cat]
//    val invCage: InvCage[Animal] = new InvCage[Cat] // no

    // contravariance - very much no (opposite)
    class ContraCage[-T]
    val contraCage: ContraCage[Cat] = new ContraCage[Animal]

    // covariant position
    class CovariantCage[+T](val animal: T) // constructor args are a covariant position
    // covariant position, in this position, typing works pretty normally
//    class CovariantVarCage[+T](var animal: T) // vars are contravariant here

//    class ContravariantCage[-T](val animal: T) // not allowed
//    class ContravariantCage[-T](var animal: T) // not allowed, vars are also covariant
    class InvariantVarCage[T](var animal: T) // you can really only use vars on invariant collections

//    class AnotherCovariantCage[+T] {
//        def addAnimal(animal: T) // method arguments are in a contravariant position
//    }
    class AnotherContravariantCage[-T] {
        def addAnimal(animal: T) = true
    }

    class MyList[+A] {
        def add[B >: A](el: B): MyList[B] = new MyList[B] // widening the type
    }

    class PetShop[-T] {
        def get[S <: T](isItAPuppy: Boolean): S = ??? // method return types are covariant
    }
    /*
     * Invariant, covariant, and contravariant version of
     * Parking[T](List[T]) // vehicles {
     *  def park(vehicle: T)
     *  def impound(vehicles: List[T])
     *  def checkVehicles(conditions: String): List[T]
     * }
     * how would api be different if list was invariant? (class IList[T])
     * make parking a monad
     * add a flatMap method
     */
    class CoParking[+T](vehicles: List[T]) { // vehicles {
        def park[S >: T](vehicle: S): CoParking[S] = ???
        def impound[S >: T](vehicles: List[S]): CoParking[S] = ???
        def checkVehicles(conditions: String): List[T] = ???
        def flatMap[S](f: T => CoParking[S]): CoParking[S] = ???
    }
    class InParking[T](vehicles: List[T]) {
        def park(vehicle: T): InParking[T] = ???
        def impound(vehicles: List[T]): InParking[T] = ???
        def checkVehicles(conditions: String): List[T] = ???
        def flatMap[S](f: T => InParking[S]): CoParking[S] = ???
    }
    class ContraParking[-T](vehicles: List[T]) {
        def park(vehicle: T): ContraParking[T] = ???
        def impound(vehicles: List[T]): ContraParking[T] = ???
        def checkVehicles[S <: T](conditions: String): List[S] = ???
        def flatMap[R <: T, S](f: R => ContraParking[S]): CoParking[S] = ???
    }
}
