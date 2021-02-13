package lectures.part4implicits
import java.util.Optional
import collection.mutable
import java.{util => javaUtil}
import scala.jdk.CollectionConverters._

object ScalaJavaConversions extends App {
    val javaSet: javaUtil.Set[Int] = new javaUtil.HashSet[Int]()
    (1 to 5).foreach(javaSet.add)
    println(javaSet)

    val scalaSet = javaSet.asScala // implicit method
    println(scalaSet)

    val numbersBuffer = mutable.ArrayBuffer[Int](1, 2, 3)
    val javaBuffer = numbersBuffer.asJava

    println(javaBuffer.asScala eq numbersBuffer) // returns original reference when it can

    val numbers = List(1, 2, 3)
    val javaNumbers = numbers.asJava
    val backToScala = javaNumbers.asScala // List is immutable, it can't give back the original list, it is converted

//    javaNumbers.add(7) // throws, list is still immutable under the hood


    // create a Scala-Java Optional-Option conversion

    implicit class OptionalOption[T](value: Option[T]) {
        def asJava: Optional[T] = value match {
            case Some(v) => Optional.of(v)
            case None => Optional.empty()
        }
    }
    implicit class OptionOptional[T](value: Optional[T]) {
        def asScala: Option[T] = if (value.isPresent) Some(value.get) else None
    }
    println(Optional.empty().asScala)
    println(Optional.of(42).asScala)
    println(None.asJava)
    println(Option(42).asJava)
}
