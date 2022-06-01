# 아이템 35 - 복잡한 객체를 생성하기 위한 DSL 을 정의하라

## 사용자 정의 DSL 만들기

- [https://pl.kotl.in/5B_zBrESD](https://pl.kotl.in/5B_zBrESD)

```kotlin
/**
 * Kotlin DSL 기본
 */

fun main() {    
   println("plus1: ${plus1(1,2)}")
   println("plus2: ${plus2(1,2)}")
   println("plus3: ${plus3(1,2)}")
   println("plus4: ${plus4(1,2)}")
   println("plus5: ${plus5(1,2)}")
   
   println("Int.myPlus: ${1.myPlus(2)}")
   println("myPlus: ${myPlus(1, 2)}")
   
   // 리시버를 가진 익명 확장 함수와 람다 표현식은 다음과 같은 방법으로 호출할수 있습니다.
   println("myPlus2: ${myPlus2.invoke(1, 2)}")
   println("myPlus2: ${myPlus2(1, 2)}")
   println("myPlus2: ${1.myPlus2(2)}")
   
   val user = User().apply {
       name = "Marcin"
       surname = "Moskata"
   }
   println("user: ${user.name}, ${user.surname}")
}

fun plus(a: Int, b: Int) = a + b

val plus1: (Int, Int) -> Int = { a, b -> a + b } // 람다 표현식
val plus2: (Int, Int) -> Int = fun(a,b) = a + b	// 익명 함수
val plus3: (Int, Int) -> Int = ::plus			// 함수 레퍼런스

// 람다 표현식과 익명함수의 아규먼트 타입 추론
val plus4 = { a: Int, b: Int -> a + b }			// 람다 표현식
val plus5 = fun(a: Int, b: Int) = a + b			// 익명 함수

// 확장함수
fun Int.myPlus(other: Int) = this + other

// 리시버를 가진 함수 타입
val myPlus = fun Int.(other: Int) = this + other

val myPlus2: Int.(Int) -> Int = { this + it }

// 리시버를 가진 함수 타입의 가장 중요한 특징은 this의 참조 대상을 변경할 수 있다는 것입니다.
// this 는 apply 함수에서 리시버 객체의 메서드와 프로퍼티를 간단하게 참조할 수 있게 해주기도 합니다.
// 리시버를 가진 함수 타입은 코틀린 DSL을 구성하는 가장 기본적인 블록입니다.
inline fun <T> T.apply(block: T.() -> Unit): T {
    this.block()
    return this
}

class User {
    var name: String = ""
    var surname: String =""
}
```