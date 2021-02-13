package lectures.part4implicits

object TypeClasses extends App {
    // type class is a trait that takes a type and describes what operations can be applied to that type

    // option 1
    trait HTMLWritable {
        def toHTML: String
    }

    case class User(name: String, age: Int, email: String) {
//        def toHTML: String = s"<div>$name ($age yo) <a href=mailto:$email /></div>" // uncommenting this breaks exercise
    }
    val john = User("John", 32, "john@rockthejvm.com")
//    john.toHTML
    /*
        1 - only works for the types we write
        2 - only one impelementation
     */

    // option 2
    object HTMLSerializerPM {
        def serializeToHTML(value: Any): String = value match {
            case User(n, a, e) => s"<div>$n $a $e</div>"
            // other cases go here
        }
    }
    /*
        - loses type safety
        - need to modify code every time
        - still only one implementation
     */

    // option 3
    trait HTMLSerializer[T] {
        def serialize(value: T): String
    }
    implicit object UserSerializer extends HTMLSerializer[User] {
        override def serialize(user: User): String = s"<div>${user.name} (${user.age} yo) <a href=mailto:${user.email} /></div>"
    }
    println(UserSerializer.serialize(john))
    // we can define types for other types
    import java.util.Date
    object DateSerializer extends HTMLSerializer[Date] {
        override def serialize(value: Date): String = s"<div>${value.toString}</div>"
    }
    // we can define multiple serializers for a given type
    object PartialUserSerializer extends HTMLSerializer[User] {
        override def serialize(user: User): String = s"<div>${user.name}</div>"
    }
    // HTMLSerializer is a type class

    // part 2
    object HTMLSerializer {
        def serialize[T](value: T)(implicit serializer: HTMLSerializer[T]): String =
            serializer.serialize(value)
        def apply[T](implicit serializer: HTMLSerializer[T]): HTMLSerializer[T] = serializer
    }
//    implicit object IntSerializer extends HTMLSerializer[Int] {
//        override def serialize(value: Int): String = s"""<div color="blue">$value</div>"""
//    }
//    println(HTMLSerializer.serialize(42))
//
//    // access to the entire type class interface
//    println(HTMLSerializer[Int].serialize(42))

    /*
        Implement pattern for Equal type class
        extended in Equality exercise
     */
//    trait Equal[T] {
//        def apply(a: T, b: T): Boolean
//    }
//    object Equal {
//        def apply[T](a: T, b: T)(implicit instance: Equal[T]): Boolean = instance(a, b)
//    }
//    implicit object UserNameEqual extends Equal[User] {
//        override def apply(a: User, b: User): Boolean = a.name == b.name
//    }
//    object UserNameAndEmailEqual extends Equal[User] {
//        override def apply(a: User, b: User): Boolean = UserNameEqual(a, b) && a.email == b.email
//    }
    val john2 = User("John", 30, "different@email.com")
//    println(Equal(john, john2))
//    println(UserNameAndEmailEqual(john, john2))

    // ad-hoc polymorphism

}
