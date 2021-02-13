package lectures.part2afp

object CurriesPAF extends App {
    val superAdder: Int => Int => Int = x => y => x + y // curried function
    val add3 = superAdder(3)
    val seven = add3(4)
    def curriedAdder(x: Int)(y: Int): Int = x + y // also curried

    /* lifting (ETA-EXPANSION) */
    // functions != methods
    def inc(x: Int): Int = x + 1
    List(1, 2, 3).map(inc) // ETA-expansion, method is turned into an instance of the function class (x => inc(x))

    // partial function applications
    val add4: Int => Int = curriedAdder(4) // does not work without type annotation
    val add5 = curriedAdder(5) _ // forces ETA- expansion

    // exercise
    val simpleAddFunction = (x: Int, y: Int) => x + y
    def simpleAddMethod(x: Int, y: Int): Int = x + y
    def curriedAddMethod(x: Int)(y: Int): Int = x + y

    // create add7 out as many different ways as I can using the above
    val add7_1 = (z: Int) => simpleAddFunction(7, z)
    val add7_2 = (z: Int) => simpleAddMethod(7, z)
    val add7_3 = curriedAddMethod(7) _
    val add7_4 = (z: Int) => simpleAddFunction(3, simpleAddMethod(4, z))
    val add7_5 = (z: Int) => curriedAddMethod(4)(simpleAddFunction(3, z))
    val add7_6 = (z: Int) => curriedAddMethod(4)(simpleAddMethod(3, z))
    val add7_7 = (z: Int) => curriedAddMethod(4)(simpleAddMethod(2, simpleAddFunction(1, z)))

    // new
    val add7_8 = simpleAddFunction.curried(7)
    val add7_9 = curriedAddMethod(7)(_) // alternative syntax for add7_3
    val add7_10 = simpleAddMethod(7, _: Int) // alternative syntax for add7_2, turns method into function value

    // underscores
    def concatenator(a: String, b: String, c: String): String = a + b + c
    val insertName = concatenator("Hello, I'm ", _: String, ", how are you?")
    println(insertName("Gene"))
    val fillInTheBlanks = concatenator("Hello, ", _: String, _: String)
    println(fillInTheBlanks("Gene", " Scala is awesome!"))

    // exercise 2
    val aList = List(1.23456, 1.23, 309082420393328.9761234598, 11.691284928439482924384393, 4.0, Math.PI)
    val formatFunc = (frmt: String) => (num: Double) => frmt.format(num)
    println(aList.map(formatFunc("%4.2f")))
    println(aList.map(formatFunc("%8.6f")))
    println(aList.map(formatFunc("%14.12f")))

    // difference between functions vs methods and parameters that are by-name vs 0-lambda
    def byName(n: => Int): Int = n + 1
    def byFunction(f: () => Int): Int = f() + 1

    def method: Int = 42
    def parenMethod(): Int = 42

    byName(42)
    byName(method)
    byName(parenMethod())

    byFunction(() => 42)
    byFunction(() => method)
    byFunction(parenMethod _) // not actually necessary, but intellij doesn't like it without
}
