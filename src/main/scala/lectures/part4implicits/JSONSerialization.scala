package lectures.part4implicits

import java.text.SimpleDateFormat
import java.util.{Date, TimeZone}

object JSONSerialization extends App {
    case class User(name: String, age: Int, email: String)
    case class Post(content: String, createdAt: Date)
    case class Feed(user: User, posts: List[Post])

    implicit class DateSerializer(date: Date) {
        def toISOString: String = {
            val sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"))
            sdf.format(date)
        }
    }

    trait JSONSerializable[T] {
        def serialize(value: T): String
    }
    implicit object UserSerializer extends JSONSerializable[User] {
        override def serialize(value: User): String =
            s"""{"name":"${value.name}","age":${value.age},"email":"${value.email}}""""
    }
    implicit object PostSerializer extends JSONSerializable[Post] {
        override def serialize(value: Post): String =
            s"""{"content":"${value.content}","createdAt":${value.createdAt.toISOString}}"""
    }
    implicit object FeedSerializer extends JSONSerializable[Feed] {
        override def serialize(value: Feed): String =
            s"""{"user":${value.user.toJSONString},"posts":${value.posts.map(_.toJSONString).mkString("[", ",", "]")}}"""
    }
//    implicit class ListSerializer[T: JSONSerializable](values: List[T]) extends JSONSerializable[List[T]] {
//        override def toJSON(value: List[T]): String = value.map(_.toJSON).mkString("[", ",", "]")
//    }
    implicit class JSONEnricher[T](value: T) {
        def toJSONString(implicit serializer: JSONSerializable[T]): String = serializer.serialize(value)
    }

    val user = User("John", 42, "guy@place.suffix")
    val post1 = Post("Test post. Please ignore.", new Date())
    val post2 = Post("This is a post.", new Date())
    val feed = Feed(user, List(post1, post2))
    println(feed.toJSONString)

    // above is just me screwing around
    // below is lecture example
    sealed trait JSONValue {
        def stringify: String
    }
    final case class JSONString(value: String) extends JSONValue {
        override def stringify: String = s""""$value""""
    }
    final case class JSONNumber(value: Int) extends JSONValue {
        override def stringify: String = value.toString
    }
    final case class JSONArray(values: List[JSONValue]) extends JSONValue {
        override def stringify: String = values.map(_.stringify).mkString("[", ",", "]")
    }
    final case class JSONObject(values: Map[String, JSONValue]) extends JSONValue {
        override def stringify: String = values.map {
            case (key, value) => s""""$key":${value.stringify}"""
        }.mkString("{", ",", "}")
    }
    val data = JSONObject(Map(
        "user" -> JSONString("Daniel"),
        "posts" -> JSONArray(List(
            JSONString("Scala Rocks!"),
            JSONNumber(453)
        ))
    ))
    println(data.stringify)

    trait JSONConverter[T] {
        def convert(value: T): JSONValue
    }
    implicit object StringConverter extends JSONConverter[String] {
        def convert(value: String): JSONValue = JSONString(value)
    }
    implicit object IntConverter extends JSONConverter[Int] {
        def convert(value: Int): JSONValue = JSONNumber(value)
    }
    implicit object UserConverter extends JSONConverter[User] {
        override def convert(value: User): JSONValue =
            JSONObject(Map(
                "name" -> JSONString(value.name),
                "age" -> JSONNumber(user.age),
                "email" -> JSONString(user.email)
            ))
    }
    implicit object PostConverter extends JSONConverter[Post] {
        override def convert(value: Post): JSONValue =
            JSONObject(Map(
                "content" -> JSONString(value.content),
                "createdAt" -> JSONString(value.createdAt.toISOString)
            ))
    }
    implicit object FeedConverter extends JSONConverter[Feed] {
        override def convert(value: Feed): JSONValue =
            JSONObject(Map(
                "user" -> user.toJSON,
                "posts" -> JSONArray(value.posts.map(_.toJSON))
            ))
    }

    implicit class JSONOps[T](value: T) {
        def toJSON(implicit converter: JSONConverter[T]): JSONValue = converter.convert(value)
    }
    println(feed.toJSON.stringify)
}
