package exercises

import lectures.part4implicits.TypeClasses.{HTMLSerializer, User, UserSerializer, PartialUserSerializer}

object Equality extends App {
    trait Equal[T] {
        def apply(a: T, b: T): Boolean
    }
    object Equal {
        def apply[T](a: T, b: T)(implicit instance: Equal[T]): Boolean = instance(a, b)
    }
    implicit object UserNameEqual extends Equal[User] {
        override def apply(a: User, b: User): Boolean = a.name == b.name
    }
    object UserNameAndEmailEqual extends Equal[User] {
        override def apply(a: User, b: User): Boolean = UserNameEqual(a, b) && a.email == b.email
    }
    val john = User("John", 30, "address@email.com")
    val john2 = User("John", 30, "different@email.com")
    println(Equal(john, john2))

    implicit class HTMLEnrichment[T](value:T) {
        def toHTML(implicit serializer: HTMLSerializer[T]): String = serializer.serialize(value)
    }
    println(john.toHTML) // println(new HTMLEnrichment[User](john).toHTML(UserSerializer))
    println(john.toHTML(PartialUserSerializer)) // you can specify others
    /*
        - choose implementation to import (UserSerializer is implicit)
        - extend to new types
     */

    implicit class EqualEnrichment[T](value: T) {
        def ===(other: T)(implicit equal: Equal[T]): Boolean = equal(value, other)
        def !==(other: T)(implicit equal: Equal[T]): Boolean = !equal(value, other)
    }

    println(john === john2)
    val terry = User("Terry", 42, "terry@awesome.address")
    println(john !== terry)
    // type safe as well

    // context bounds
    def htmlBoilerplate[T](content: T)(implicit serializer: HTMLSerializer[T]): String =
        s"<html><body>${content.toHTML(serializer)}</body></html>"
    def htmlSugar[T: HTMLSerializer](content: T): String = // equivalent, automatically injects implicit
        s"<html><body>${content.toHTML}</body></html>"
    println(htmlBoilerplate(john))
    println(htmlSugar(john))

    // implicitly
    case class Permissions(mask: String)
    implicit val defaultPermissions: Permissions = Permissions("0744")
    val standardPermissions = implicitly[Permissions] // gets the implicit value for the type in the current scope
    println(standardPermissions)
}
