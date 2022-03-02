# 아이템 1 - 가변성을 제한하라

코틀린은 모듈로 프로그램을 설계합니다. 모듈은 `클래스`, `객체`, `함수`, `타입 별청(type alias)`, `톱 레벨(top-level) 프로퍼티` 등 다양한 요소로 구성됩니다.

이러한 요소 중 일부는 `상태(state)`를 가질 수 있습니다. 

예를 들어 읽고 쓸 수 있는 프로퍼티(read-write property) `var` 를 사용하거나, `mutable` 객체를 사용하면 상태를 가질 수 있습니다.

```kotlin
var a = 10
var list: MutableList<Int> = mutableListOf()
```
- 이처럼 요소가 상태를 갖는 경우, 해당 요소의 동작은 사용 방법뿐만 아니라 그 이력(history)에도 의존하게 됩니다.

### [BankAccount](./BankAccountSample.kt)
위 코드의 BankAccount 에는 계좌에 돈이 얼마나 있는지 나타내는 상태가 있습니다.
이처럼 상태를 갖게 하는 것은 양날의 검입니다.
시간의 변화에 따라서 변하는 요소를 표현할 수 있다는 것은 유용하지만, 상태를 적절하게 관리하는 것이 생각보다 꽤 어렵습니다.

1. 프로그램을 이해하고 디버그하기 힘들어집니다.
   - 상태를 갖는 부분들의 관계를 이해해야 하며, 상태 변경이 많아지면 이를 추적하는 것이 힘들어집니다.
   - 이러한 클래스는 이해하기도 어렵고, 이후에 코드를 수정하기도 힘듭니다.
   - 클래스가 예상하지 못한 상황 또는 오류를 발생시키는 경우에 큰 문제가 됩니다.
2. 가변성(mutability)이 있으면, 코드의 실행을 추론하기 어려워집니다.
   - 시점에 따라서 값이 달라질 수 있으므로, 현재 어떤 값을 갖고 있는지 알아야 코드의 실행을 예측할 수 있습니다.
   - 한 시점에 확인한 값이 계속 동일하게 유지된다고 확신할 수 없습니다.
3. 멀티스레드 프로그램일 때는 적절한 동기화가 필요합니다.
   - 변경이 일어나는 모든 부분에서 충돌이 발생할 수 있습니다.
4. 테스트하기 어렵습니다.
   - 모든 상태를 테스트해야 하므로, 변경이 많으면 많을수록 더 많은 조합을 테스트해야 합니다.
5. 상태 변경이 일어날 때, 이러한 변경을 다른 부분에 알려야 하는 경우가 있습니다.
   - 정렬되어 있는 리스트에 가변 요소를 추가한다면, 요소에 변경이 일어날 때마다 리스트 전체를 다시 정렬해야 합니다.

대규모 팀에서 일하고 있는 개발자라면 변경 가능한 부분에 의한 일관성(consistency)문제, 복잡성(complexity) 증가와 관련된 문제에 익숙할 것입니다.
공유 상태를 관리하는 것이 얼마나 힘든 일인지 간단한 예로 확인해 봅시다.

### [MultiThreadProperty](./MultiThreadProperty.kt)
- 위 코드는 멀티스레드를 활용해서 프로퍼티를 수정합니다.
- 충돌에 의해서 일부 연산이 이루어지지 않습니다.

### [MultiThreadPropertyCoroutine](./MultiThreadPropertyCoroutine.kt)
- 코틀린의 코루틴을 활용하면, 더 적은 스레드가 관여되므로 충돌과 관련된 문제가 줄어듭니다. 하지만 문제가 사리지는 것은 아닙니다.

일부 연산이 충돌되어 사라지므로 적절하게 추가로 동기화를 구현해야 합니다.
동기화를 잘 구현하는 것은 굉장히 어려운 일이고 변할 수 있는 지점이 많다면 훨씬 더 어려워집니다.
따라서 변할 수 이쓴ㄴ 지점은 줄일수록 좋습니다.

### [SynchronizationSample](./SynchronizationSample.kt)
- 가변은 생각보다 단점이 많아서 이를 완전하게 재한하는 프로그래밍 언어도 있습니다. 바로 순수 함수형 언어 입니다.
- 널리 알려진 순수 함수형 언어로는 하스켈(Haskell)이 있습니다. 이러한 프로그래밍 언어는 가변성에 너무 많은 제한이 걸려서 프로그램을 작성하기가 굉장히 어렵습니다.
- 가변성은 시스템의 상태를 나타내기 위한 중요한 방법이지만 변경이 일어나야 하는 부분을 신중하고 확실하게 결정하고 사용하기 바랍니다.

## 코틀린에서 가변성 제한하기
코틀린은 가변성을 제한할 수 있게 설계되어 있습니다. 그래서 immutable(불변) 객체를 만들거나, 프로퍼티를 변경할 수 없게 막는것이 굉장히 쉽습니다.

### 읽기 전용 프로퍼티(val)
코틀린은 val 을 사용해 읽기 전용 프로퍼티를 만들 수 있습니다. 이렇게 선언된 프로퍼티는 마치 값(value) 처럼 동작하며, 일반적인 방법으로는 값이 변하지 않습니다. (읽고 쓸 수 있는 프로퍼티는 var 로 만듭니다.)

```kotlin
val a = 10
a = 20 // 오류
```

```kotlin
val list = mutableListOf(1, 2, 3)
list.add(4)

print(list) // [1, 2, 3, 4]
```
- 읽기 전용 프로퍼티가 mutable 객체를 담고 있다면, 내부적으로 변할 수 있습니다.

```kotlin
var name: String = "Marcin"
var surname: String = "Moskata"
val fullName
    get() = "$name $surname"

fun main() {
    println(fullName) // Marcin Moskata
    name = "Maja"
    println(fullName) // Maja Moskata
}
```
- 읽기 전용 프로퍼티는 다른 프로퍼티를 활용하는 사용자 정의 게터로도 정의 할 수 있습니다.
- var 프로퍼티를 사용하는 val 프로퍼티는 var 프로퍼티가 변할 때 변할 수 있습니다.

**[ReadOnlyProperty](./ReadOnlyProperty.kt)**
- 값을 추출할 때마다 사용자 정의 게터가 호출되므로 이러한 코드를 사용할 수 있다.

<br/>

```kotlin
interface Element {
   val active: Boolean
}

class ActualElement: Element {
    override var active: Boolean = false
}
```
- 코틀린의 프로퍼티는 기본적으로 캡슐화되어 있고, 추가적으로 사용자 정의 접근자(getter, setter)를 가질 수 있습니다.
- var 은 게터와 세터를 모두 제공하지만, val 은 변경이 불가능하므로 게터만 제공합니다. 그래서 val 을 var 로 오버라이드 할 수 있습니다.

<br/>

읽기 전용 프로퍼티 val 의 값은 변경될 수 있기는 하지만, 프로퍼티 레퍼런스 자체를 변경할 수는 없으므로 동기화 문제 등을 줄일 수 있ㅅ브니다.
그래서 일반적으로 var 보다 val 을 많이 사용합니다.

<br/>

```kotlin
val name: String? = "Marton"
val surname: String = "Braun"

val fullName: String?
   get() = name?.let { "$it $surname" }

val fullName2: String? = name?.let { "$it $surname" }

fun run() {
   if (fullName != null) { 
       println(fullName.length) // 오류
   }

   if (fullName2 != null) {
      println(fullName2.length)
   }
}
```
[SampleSmartCast](./ReadOnlyProperty.kt)
- val 은 정의 옆에 상태가 바로 적히므로, 코드의 실행을 예측하는 것이 훨씬 간단합니다.
- 스마트 캐스트(smart-cast) 등의 추가적인 기능을 활용할 수 있ㅅ브니다.
- `fullName` 은 커스텀 게터로 정의했으므로 스마트 캐스트를 할 수 없습니다. 게터를 활용하므로, 값을 사용하는 시점의 name에 따라서 다른 결과가 나올 수 있기 때문입니다.
- `fullName2` 처럼 지역 변수가 아닌 프로퍼티가 final 이고, 사용자 정의 게터를 갖지 않을 경우 스마트 캐스트 할 수 있습니다.


### 가변 컬렉션과 읽기 전용 컬렉션 구분하기
- 코틀린은 읽고 쓸 수 있는 프로퍼티와 읽기 전용 프로퍼티로 구분 됩니다.
- 마찬가지로 코틀린은 읽고 쓸 수 있는 컬렉션과 읽기 전용 컬렉션으로 구분 됩니다.
- 컬렉션 계층의 읽기 전용 인터페이스, 변경을 위한 메서드를 따로 가지지 않습니다.
  - Iterable
  - Collection
  - Set
  - List
- 컬렉션 계층의 읽고 쓸 수 있는 컬렉션, 읽기 전용 인터페이스를 상속 받아서, 변경을 위한 메서드를 추가한 것입니다.
  - MutableIterable
  - MutableCollection
  - MutableSet
  - MutableList


### [ReadOnlyPropertyCollections](./ReadOnlyPropertyCollections.kt)
- 컬렉션을 진짜로 불변(immutable)하게 만들지 않고, 읽기 전용으로 설계
- 내부적으로 인터페이스를 사용하고 있으므로, 실제 컬렉션을 리턴할 수 있습니다.
- 코틀린이 내부적으로 immutable 하지 않은 컬렉션을 외부적으로 immutable 하게 보이게 만들어서 얻어지는 안전성
- 그런데 개발자가 `시스템 해킹`을 시도해서 다운 캐스팅 할 때 문제가 됩니다.
- 리스트를 읽기 전용으로 리턴하면, 이를 읽기 전용으로만 사용해야 합니다.
- 컬렉션 다운 캐스팅은 이러한 계약을 위반하고, 추상화를 무시하는 행위입니다.

```kotlin
val list = listOf(1, 2, 3)

// 이렇게 하지 마세요
    if (list is MutableList) {
        list.add(4)
    }
```

읽기 전용에서 mutable로 변경해야 한다면, 복재(copy)를 통해서 새로운 mutable 컬렉션을 만드는 list.toMutableList 를 활용해야 합니다.
```kotlin
val list = listOf(1, 2, 3)
val mutableList = list.toMutableList()
mutableList.add(4)
```
- 이렇게 코드를 작성하면 어떠한 규약도 어기지 않을 수 있으며, 기존의 객체는 여전히 immutable이라 수정할 수 없으므로, 안전하다고 할 수 있습니다. 

### 데이터 클래스의 copy

String 이나 Int 처럼 내부적인 상태를 변경하지 않는 immutable 객체를 많이 사용하는 데는 이유가 있습니다.

Immutable 객체를 사용할 때 장점
1. 한 번 정의된 상태가 유지되므로, 코드를 이해하기 쉽습니다.
2. immutable 객체는 공유했을 때도 충돌이 따로 이루어지지 않으므로, 병렬 처리를 안전하게 할 수 있습니다.
3. immutable 객체에 대한 참조는 변경되지 않으므로, 쉽게 캐시할 수 있습니다.
4. immutable 객체는 방어적 복사본을 만들 필요가 없습니다. 또한 객체를 복사할 때 깊은 복사를 따로 하지 않아도 됩니다.
5. immutable 객체는 다른 객체(mutable 또는 immutable 객체)를 만들 때 활용하기 좋습니다. 또한 immutable 객체는 실행을 더 쉽게 예측할 수 있습니다.
6. immutable 객체는 `세트(set)` 또는 `맵(map)의 키`로 사용할 수 있습니다.

요소에 수정이 일어나면 해시 테이블 내부에서 요소를 찾을 수 없게 되어 버립니다.

```kotlin
val names: SortedSet<FullName> = TreeSet()
val person = FullName("AAA", "AAA")
names.add(person)
names.add(FullName("Jordan", "Hansen"))
names.add(FullName("David", "Blanc"))

print(name) // [AAA AAA, David Blance, Jordan Hansen]
print(person in names) // true

person.name = "ZZZ"
print(name) // [ZZZ AAA, David Blance, Jordan Hansen]
print(person in names) // true
```
- 마지막 출력을 보면, 세트 내부에 해당 객체가 있음에도 false 를 리턴한다는 것을 학인할 수 있습니다.
- 객체를 변경했기 때문에 찾을 수 없는 것입니다.

<br/>

지금까지 살표본 것처러 mutable 객체는 예측하기 어려우며 위험하다는 단점이 있습니다.

반면, immutable 객체는 변경할 수 없다는 단점이 있습니다.

따라서 immutable 객체는 자신의 일부를 수정한 새로운 객체를 만들어 내는 메서드를 가져야 합니다.

예를 들어 User 라는 immutable 객체가 있고, 성(surname)을 변경해야 한다면, withSurname 과 같은 메서드를 제공해서, 
자신을 수정한 새로운 객체를 만들어 낼 수 있게 해야 합니다.

### [DataCopy](./DataCopy.kt)
- 모든 프로퍼티를 대상으로 이런 함수를 하나하나 만드는 것은 굉장히 귀찮은 일입니다.
- `data` 한정자는 `copy`라는 메서드를 활용하면, 모든 기본 생성자 프로퍼티가 같은 새로운 객체를 만들어 낼 수 있습니다.

<br/>

코틀린에서는 이와 같은 형태로 immutable 특성을 가지는 데이터 모델 클래스를 만듭니다.

변경을 할 수 있다는 측면만 보면 mutable 객체가 더 좋아 보이지만, 
이렇게 데이터 모델 클래스를 만들어 immutable 객체로 만드는 것이 더 많은 장점을 가지므로, 
기본적으로는 이렇게 만드는 것이 좋습니다.

## 다른 종류의 변경 가능 지점

변경할 수 있는 리스트를 만드는 두 가지 방법으로 하나는 mutable 컬렉션, 다른 하나는 var로 읽고 쓸 수 있는 프로퍼티를 만드는 것입니다.

```kotlin
val list1: MutableList<Int> = mutableListOf()
var list2: List<Int> = listOf()
```

<br/>

두 가지 모두 변경할 수 있지만 방법이 다릅니다.
```kotlin
list1.add(1)
list2 = list2 + 1
```

<br/>

물론 두 가지 코드 모두 다음과 같이 += 연산자를 활용해서 변경할 수 있지만, 실질적으로 이루어지는 처리는 다릅니다.
두 가지 모두 정상적으로 동작하지만, 장단점이 있고, 모두 변경 가능 지점(mutating point)이 있지만, 그 위치가 다릅니다.
```kotlin
list1 += 1 // list1.plusAssign(1)로 변경됩니다.
```
- 구체적인 리스트 구현 내부에 변경 가능 지점이 있습니다. 
- 멀티스레드 처리가 이루어질 경우, 내부적으로 적절한 동기화가 되어 있는지 확실하게 알 수 없으므로 위험합니다.

```kotlin
list2 += 1 // list2 = list2.plus(1)로 변경됩니다.
```
- 프로퍼티 자체가 변경 가능 지점입니다.
- 멀티스레드 처리의 안전성이 더 좋다고 할 수 있습니다. (잘못 만들면 일부 요소가 손실될 수도 있습니다.)

[MutatingPoint](./MutatingPoint.kt)

### mutable 리스트 대신 mutable 프로퍼티를 사용
mutable 리스트 대신 mutable 프로퍼티를 사용하는 형태는 사용자 정의 세터(또는 이를 사용하는 델리게이터)를 활용해서 변경을 추적할 수 있습니다.
예를 들어 Delegates.observable 을 사용하면, 리스트에 변경이 있을 때 로그를 출력할 수 있습니다.

[DelegateObservable](./DelegateObservable.kt)
- mutable 컬렉션도 이처럼 관찰할 수 있게 만들려면, 추가적인 구현이 필요합니다.
- mutable 프로퍼티에 읽기 전용 컬렉션을 넣어 사용하는 것이 쉽습니다.
- 여러 객체를 변경하는 여러 메서드 대신 세터를 사용하면 되고, 이를 private 으로 만들 수도 있기 때문입니다.

```kotlin
var announcements = listOf<Announcement>()
    private set
```
- mutable 컬렉션을 사용하는 것이 처음에는 더 간단하게 느껴지겠지만, mutable 프로퍼티를 사용하면 객체 변경을 제어하기가 더 쉽습니다.

### 최악의 방식
```kotlin
// 이렇게 하지 마세요.
var list3 = mutableListOf<Int>()
```
- 프로퍼티와 컬렉션을 모두 변경 가능한 지점으로 만드는것은 최악의 방식입니다.
- 변경될 수 있는 두 지점 모두에 대한 동기화를 구현해야 합니다.
- 모호성이 발생해서 `+=`를 사용할 수 없게 됩니다.
- 상태를 변경할 수 있는 불필요한 방법은 만들지 않아야 합니다.
- 상태를 변경하는 모든 방법은 코드를 이해하고 유지해야 하므로 비용이 발생합니다.
- 따라서 모든 가변성을 제한하는 것이 좋습니다.

## 변경 가능 지점 노출하지 말기
상태를 나타내는 mutable 객체를 외부에 노출하는 것은 굉장히 위험합니다.

```kotlin
data class User(val name: String)

class UserRepository {
    private val storedUsers: MutableMap<Int, String> = mutableMapOf()
    
    fun loadAll(): MutableMap<Int, String> {
        return storedUsers
    }
}
```
- loadAll 을 사용해서 private 상태인 UserRepository 를 수정할 수 있습니다.

```kotlin
val userRepository = UserRepository()
val storedUsers = userRepository.loadAll()
storedUsers[4] = "Kirill"
//...

print(userRepository.loadAll()) // {4=Kirill}
```
- 이러한 코드는 돌발적인 수정이 일어날 때 위험할 수 있습니다.

<br/>

이를 처리하는 방법은 두 가지 입니다.

### 리턴되는 mutable 객체를 복제
- 방어적 복제(defensive copying)라고 부릅니다.
- data 한정자로 만들어지는 copy 메서드를 활용하면 좋습니다.

```kotlin
class UserHolder {
    private val user: MutableUser()
            
    fun get(): MutableUser {
        return user.copy()
    }
    
    //...
}
```

### 가능하다면 무조건 가변성을 제한
- 컬렉션은 객체를 읽기 전용 슈퍼타입으로 업캐스트하여 가변성을 제한할수도 있습니다.

```kotlin
data class User(val name: String)

class UserRepository {
    private val storedUsers: MutableMap<Int, String> = 
        mutableMapOf()
    
    fun loadAll(): Map<Int, String> {
        return storedUsers
    }
    
    //...
}
```

## 정리
이번 장에서는 가변성을 제한한 immutable 객체를 사용하는 것이 좋은 이유에 대해서 알아 보았습니다.
코틀린은 가변성을 제한하기 위해 다양한 도구들을 제공합니다.
이를 활용해 가변 지점을 제한하며 코드를 작성하도록 합시다.

### 이때 활용할 수 있는 몇 가지 규칙을 정리해 보면, 다음과 같습니다.
- var 보다는 val 을 사용하는 것이 좋습니다.
- mutable 프로퍼티보다는 immutable 프로퍼티를 사용하는 것이 좋습니다.
- mutable 객체와 클래스보다는 immutable 객체와 클래스를 사용하는 것이 좋습니다.
- 변경이 필요한 대상을 만들어야 한다면, immutable 데이터 클래스로 만들고 copy 를 활용하는 것이 좋습니다.
- 컬렉션에 상태를 저장해야 한다면, mutable 컬렉션보다는 읽기 전용 컬렉션을 사용하는 것이 좋습니다.
- 변이 지점을 적절하게 설계하고, 불필요한 변이 지점은 만들지 않는것이 좋습니다.
- mutable 객체를 외부에 노출하지 않는 것이 좋습니다.

### 예외
- 가끔 효율성 때문에 immutable 객체보다 mutable 객체를 사용하는 것이 좋을 때가 있습니다. 이러한 최적화는 코드에서 성능이 중요한 부분에서만 사용하는 것이 좋습니다.
- immutable 객체를 사용할 때는 언제나 멀티스레드 때에 더 많은 주의를 기울여야 한다는 것을 기억하세요

--- 

## 추가 궁금사항 정리

### @Throws 어노테이션

```kotlin
@Throws(InsufficientFunds::class)
```
- 이 어노테이션은 JVM 메소드로 컴파일될 때 함수에 의해 선언되어야 하는 예외를 나타냅니다.

변환 예:
```kotlin
// 선언
@Throws(IOException::class)
fun readFile(name: String): String {...}

-----------------------------------------------------

// 변환
String readFile(String name) throws IOException {...}
```