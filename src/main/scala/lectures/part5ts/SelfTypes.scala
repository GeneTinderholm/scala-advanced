package lectures.part5ts

object SelfTypes extends App {
    // requiring a type to be mixed in

    trait Instrumentalist {
        def play(): Unit
    }

    trait Singer { this: Instrumentalist => // forces singer to also implement Instrumentalist
        def sing(): Unit
    }

    class LeadSinger extends Singer with Instrumentalist {
        def play(): Unit = println("sad guitar noises")
        def sing(): Unit = println("*bad singing goes here*")
    }

    // does not compile
//    class Vocalist extends Singer {
//        def sing(): Unit = ???
//    }

    // vs inheritance
    class A
    class B extends A // B is an A

    trait T
    trait S {self: T =>} // S requires a T

    // CAKE pattern (dependency injection)
    // java DI
    class Component
    class ComponentA extends Component
    class ComponentB extends Component
    class DependentComponent(val component: Component)

    // cake pattern
    trait ScalaComponent {
        def action(x: Int): String
    }
    trait ScalaDependentComponent { self: ScalaComponent =>
        def dependentAction(x: Int): String = action(x) + " more string"
    }
    trait ScalaApplication { self: ScalaDependentComponent => }
    // layer 1 - small components
    trait Picture extends ScalaComponent
    trait Stats extends ScalaComponent

    // layer 2 - compose
    trait Profile extends ScalaDependentComponent with Picture
    trait Analytics extends ScalaDependentComponent with Stats

    // layer 3 - app
    trait AnalyticsApp extends ScalaApplication with Analytics

    // ...


    // seemingly cyclic dependencies

    trait X { self: Y => }
    trait Y { self: X => }
    trait Z extends Y with X
}
