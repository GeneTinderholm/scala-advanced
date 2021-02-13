package lectures.part5ts

import scala.reflect.runtime.universe

object Reflection extends App {
    case class Person(name: String) {
        def sayMyName(): Unit = println(s"Hi, my name is $name")
    }
    // import scala.reflect.runtime.universe
    // create mirror
    val mirror = universe.runtimeMirror(getClass.getClassLoader)
    // create class object
    val clazz = mirror.staticClass("lectures.part5ts.Reflection.Person")
    // create reflected mirror
    val classMirror = mirror.reflectClass(clazz)
    // get constructor
    val constructor = clazz.primaryConstructor.asMethod
    // reflect the constructor
    val constructorMirror = classMirror.reflectConstructor(constructor)
    val instance = constructorMirror("John")
    println(instance)

    val mary = Person("Mary")
    val methodName = "sayMyName"
    val instanceMirror = mirror.reflect(mary)
    val methodSymbol = universe.typeOf[Person].decl(universe.TermName(methodName)).asMethod
    val methodMirror = instanceMirror.reflectMethod(methodSymbol)
    methodMirror()

    val johnMirror = mirror.reflect(instance)
    val johnMethodMirror = johnMirror.reflectMethod(methodSymbol)
    johnMethodMirror()

    // type erasure
    // pain point 1, cannot differentiate generic types at runtime
    // pain point 2, limitations on overloads (cannot have multiple overloads of same generic type)

    // type tags
    // import universe._
    import universe._
    val tTag = typeTag[Person]
    println(tTag.tpe)

    class MyMap[K, V]

    def getTypeArguments[T](value: T)(implicit typeTage: TypeTag[T]): (Type, Symbol, List[Type]) =
        typeTag.tpe match {
            case TypeRef(t, s, l) => (t, s, l)
        }
    val map = new MyMap[Int, String]
    val (t, s, l) = getTypeArguments(map)
    println(s"Type of map: $t")
    println(s"Symbol of map: $s")
    println(s"Type arguments of map: $l")

    def isSubType[A, B](implicit ttagA: TypeTag[A], ttagB: TypeTag[B]): Boolean = {
        ttagA.tpe <:< ttagB.tpe
    }
    class Animal
    class Dog extends Animal
    println(isSubType[Dog, Animal])

    val methodSymbol2 = typeTag[Person].tpe.decl(universe.TermName(methodName)).asMethod
    val methodMirror2 = instanceMirror.reflectMethod(methodSymbol2)
    methodMirror2()
}
