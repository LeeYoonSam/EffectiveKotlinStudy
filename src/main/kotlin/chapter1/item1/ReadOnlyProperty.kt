package chapter1.item1

fun main() {
    SampleCalculate().run()
    SampleSmartCast().run()
}

class SampleCalculate {
    private fun calculate(): Int {
        print("Calculating... ")
        return 42
    }

    val fizz = calculate() // 계산..
    val buzz
        get() = calculate()

    fun run() {
        print(fizz) // 42
        print(fizz) // 42
        print(buzz) // Calculating... 42
        print(buzz) // Calculating... 42
    }
}

class SampleSmartCast {
    val name: String? = "Marton"
    val surname: String = "Braun"

    val fullName: String?
        get() = name?.let { "$it $surname" }

    val fullName2: String? = name?.let { "$it $surname" }

    fun run() {
        if (fullName != null) {
//            println(fullName.length) // 오류
        }

        if (fullName2 != null) {
            println(fullName2.length)
        }
    }
}