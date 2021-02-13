package lectures.part5ts

object PathDependentTypes extends App {
    class Outer {
        class Inner
        object InnerObject
        type InnerType
    }
    val outer = new Outer()
    val inner = new outer.Inner // inner class is instance specific

    val otherOuter = new Outer()
    val otherInner: otherOuter.Inner = new otherOuter.Inner // not fungible

    // all of the instance Inner classes extend Outer#Inner
    val anotherInner: Outer#Inner = new outer.Inner
    val yetAnotherInner: Outer#Inner = new otherOuter.Inner

    /*
        Exercise
        database keyed by Ints or Strings, but maybe others later
     */
    trait ItemLike {
        type Key
    }
    trait Item[K] extends ItemLike {
        override type Key = K
    }
    trait IntItem extends Item[Int]
    trait StringItem extends Item[String]

    def get[ItemType <: ItemLike](key: ItemType#Key): ItemType = ???

    get[IntItem](42)
    get[StringItem]("hello")
//    get[IntItem]("hello") // does not compile
}
