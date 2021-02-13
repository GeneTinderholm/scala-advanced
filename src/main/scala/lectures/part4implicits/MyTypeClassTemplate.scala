package lectures.part4implicits

    trait MyTypeClassTemplate[T] {
        def action(value: T): String // doesn't always need to return string
    }
    object MyTypeClassTemplate {
        def apply[T](implicit instance: MyTypeClassTemplate[T]): MyTypeClassTemplate[T] = instance
    }

