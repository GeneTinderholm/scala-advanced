package lectures.part4implicits

object OrganizingImplicits extends App {
    implicit val reverseOrdering: Ordering[Int] = Ordering.fromLessThan(_ > _)
    println(List(1, 4, 5, 3, 2).sorted) // scala.Predef contains an implicit Ordering

    /*
        Implicits:
            - val/var
            - object
            - accessor methods (defs with no parentheses)
     */
    // Exercise
    case class Person(name: String, age: Int)
//    implicit val ageOrdering: Ordering[Person] = Ordering.fromLessThan(_.age < _.age)
//    implicit val nameOrdering: Ordering[Person] = Ordering.fromLessThan(_.name < _.name)
    val people = List(
        Person("Steve", 30),
        Person("Amy", 22),
        Person("John", 66),
    )
    // can also be stored in objects and imported
    object NameOrdering {
        implicit val nameOrdering: Ordering[Person] = Ordering.fromLessThan(_.name < _.name)
    }
    object AgeOrdering {
        implicit val ageOrdering: Ordering[Person] = Ordering.fromLessThan(_.age < _.age)
    }
    import NameOrdering._
//    import AgeOrdering._
    println(people.sorted)

    /*
        Implicit scope
        - normal scope (local scope)
        - imported scope
        - companion object of all types involved in the method signature
        -
     */
    case class Purchase(nUnits: Int, unitPrice: Double)
    object Purchase {
        implicit val totalPriceOrdering: Ordering[Purchase] =
            Ordering.fromLessThan((a, b) => a.nUnits * a.unitPrice < b.nUnits * b.unitPrice)
    }
    object UnitCostOrdering {
        implicit val totalPriceOrdering: Ordering[Purchase] = Ordering.fromLessThan(_.unitPrice < _.unitPrice)
    }
    object UnitCountOrdering {
        implicit val totalPriceOrdering: Ordering[Purchase] = Ordering.fromLessThan(_.nUnits < _.nUnits)
    }

}
