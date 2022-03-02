package chapter1.item1

import kotlin.concurrent.thread

fun main() {
    var list = listOf<Int>()
    for (i in 1..1000) {
        thread {
            list = list + i
        }
    }

    Thread.sleep(1000)
    println(list.size) // 1000 이 되지 않습니다.
// 실행할 때마다 911과 같은 다른 숫자가 나옵니다.
}