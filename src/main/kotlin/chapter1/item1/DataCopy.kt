package chapter1.item1

fun main() {
    UserEx1().run()
    println()
    UserEx2().run()
}

class UserEx1 {
    class User(
        val name: String,
        val surname: String
    ) {
        fun withSurname(surname: String) = User(name, surname)
    }

    fun run() {
        var user = User("Maja", "Markiewicz")
        user = user.withSurname("Moskata")
        print("${javaClass.simpleName} $user") // User(name=Maja, surname=Moskata)
    }
}

class UserEx2 {
    data class User(
        val name: String,
        val surname: String
    )

    fun run() {
        var user = User("Maja", "Markiewicz")
        user = user.copy(surname = "Moskata")
        print("${javaClass.simpleName} $user") // User(name=Maja, surname=Moskata)
    }
}