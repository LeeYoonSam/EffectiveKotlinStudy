package chapter1.item1

import kotlin.properties.Delegates

fun main() {
    var names by Delegates.observable(listOf<String>()) { _, old, new ->
        println("Names changed from $old to $new")
    }
    println(names)

    names += "Fabio"
    // names가 []에서 [Fabio]로 변합니다.
    println(names)

    names += "Bill"
    // names가 [Fabio]에서 [Fabio, Bill]로 변합니다.
    println(names)
}