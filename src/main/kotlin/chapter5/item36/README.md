# 아이템 36 - 상속보다는 컴포지션을 사용하라

- 상속은 굉장히 강력한 기능으로 `is-a` 관계의 객체 계층 구조를 만들기 위해 설계 되었습니다.
- 상속은 관계가 명확하지 않을 때 사용하면, 여러 가지 문제가 발생할 수 있습니다.
- 단순하게 코드 추출 또는 재사용을 위해 상속을 하려고 한다면, 조금 더 신중하게 생각해야 합니다.
- 일반적으로 이러한 경우에는 상속보다 컴포지션을 사용하는 것이 좋습니다.

## 간단한 행위 재사용

- 프로그레스바를 어떤 로직 처리 전에 출력하고, 처리 후에 숨기는 유사한 동작을 하는 두 개의 클래스가 있다고 가정

```kotlin
class ProfileLoader {
	fun laod() {
		// 프로그레스 바를 보여 줌
		// 프로파일을 읽어 들임
		// 프로그레스 바를 숨김
	}
}

class ImageLoader {
	fun load() {
		// 프로그레스 바를 보여 줌
		// 프로파일을 읽어 들임
		// 프로그레스 바를 숨김
	}
}
```

- 일반적으로 많은 개발자가 이러한 경우에 슈퍼클래스를 만들어서 공통되는 행위를 추출합니다.

```kotlin
abstract class LoaderWithProgress {
	fun load() {
		// 프로그레스 바를 보여 줌
		innerLoad()
		// 프로그레스 바를 숨김
	}

	abstract fun innerLoad()
}

class ProfileLoader: LoaderWithProgress() {
	override fun innerLoad() {
		// 프로파일을 읽어 들임
	}
}

class ImageLoader: LoaderWithProgress() {
	override fun innerLoad() {
		// 프로파일을 읽어 들임
	}
}
```

- 간단한 경우에는 문제 없이 동작 합니다.
- 단점:
    - 상속은 하나의 클래스만을 대상으로 할 수 있습니다. 상속을 사용해서 행위를 추출하다 보면, 많은 함수를 갖는 거대한 BaseXXX 클래스를 만들게 되고, 굉장히 깊고 복잡한 계층 구조가 만들어집니다.
    - 상속은 클래스의 모든 것을 가져오게 됩니다. 따라서 불필요한 함수를 갖는 클래스가 만들어 질 수 있습니다. (인터페이스 분리 법칙 (Interface Segregation Principle)을 위반하게 됩니다.)
    - 상속은 이해하기가 어렵습니다. 일반적으로 개발자가 메서드를 읽고, 메서드의 작동 방식을 이해하기 위해 슈퍼클래스를 여러 번 확인하여 한다면, 문제가 있는 것입니다.
- 이러한 이유 때문에 다른 대안을 사용하는 것이 좋습니다.
- 대표적인 대안은 바로 컴포지션(composition) 입니다.
- 컴포지션을 사용한다는 것은 객체를 프로퍼티로 갖고, 함수를 호출하는 형형태로 재사용하는 것을 의미합니다.

상속 대신 컴포지션을 활용해서 문제를 해결한다면, 아래와 같은 코드를 사용합니다.

```kotlin
class Progress {
	fun showProgress() { /* show progress */}
	fun hideProgress() { /* hide progress */}
}

class ProfileLoader {
	val progress = Progress()

	fun load() {
		progress.showProgress()
		// 프로파일을 읽어 들임
		progress.hideProgress()
	}
}

class ImageLoader {
	val progress = Progress()

	fun load() {
		progress.showProgress()
		// 프로파일을 읽어 들임
		progress.hideProgress()
	}
}
```

- 프로그레스 바를 관리하는 객체를 다른 모든 객체에서 갖고 활용하는 추가 코드가 필요 합니다.
- 추가 코드를 적절하게 처리하는 것이 조금 어려울 수도 있어 컴포지션보다 상속을 선호하는 경우도 많습니다.
- 이런 추가 코드로 인해서 코드를 읽는 사람들이 코드의 실행을 더 명확하게 예측할 수 있다는 장점도 있고, 프로그레스 바를 훨씬 자유롭게 사용할 수 있다는 장점도 있습니다.
- 컴포지션을 활용하면, 하나의 클래스 내부에서 여러 기능을 재사용할 수 있게 됩니다.

이미지를 읽어들이고 나서 경고창을 출력한다면, 다음과 같은 형태로 컴포지션을 활용 할 수 있습니다.

```kotlin
class ImageLoader {
	private val progress = Progress()
	private val finishedAlert = FinishedAlert()

	fun load() {
		progress.showProgress()
		// 이미지를 읽어 들임
		progress.hideProgress()
		finishedAlert.show()
	}
}
```

- 하나 이상의 클래스를 상속할 수는 없습니다.
- 상속으로 이것을 구현 하려면, 두 기능을 하나의 슈퍼클래스에 배치해야 합니다. 이 때문에 클래스들에 복잡한 계층 구조가 만들어질 수 있습니다.

3개의 클래스가 프로그레스바와 경고창을 만드는 슈퍼 클래스를 상속받는데, 2개의 서브 클래스에서는 경고창을 사용하지만, 다른 1개의 서브 클래스에서는 경고창이 필요 없을 때는 어떻게 해야 할까요?

- 파라미터가 있는 생성자를 사용하는 것

```kotlin
abstract class InternetLoader(val showAlert: Boolean) {
	fun load() {
		// 프로그레스 바를 보여 줌
		innerLoad()
		// 프로그레스 바를 숨김
		if (showAlert) {
			// 경고창 출력
		}
	}

	abstract fun innerLoad()
}

class ProfileLoader : InternetLoader(showAlert = true) {
	override fun innerLoad() {
		// 이미지를 읽어 들임
	}
}

class ImageLoader : InternetLoader(showAlert = false) {
	override fun innerLoad() {
		// 이미지를 읽어 들임
	}
}
```

- 이것은 굉장히 나쁜 해결 방법 입니다.
- 서브 클래스가 필요하지도 않은 기능을 갖고, 단순하게 이를 차단할 뿐입니다.
- 기능을 제대로 차단하지 못하면, 문제가 발생할 수 있습니다.
- 상속은 슈퍼클래스의 모든 것을 가져 옵니다. 필요한 것만 가져올 수는 없습니다.

## 모든 것을 가져올 수 밖에 없는 상속

- 상속은 슈퍼클래스의 메서드, 제약, 행위 등 모든 것을 가져옵니다. 따라서 상속은 객체의 계층 구조를 나타낼 때 굉장히 좋은 도구입니다. 하지만 일부분을 재사용하기 위한 목적으로는 적합하지 않습니다.
- `컴포지션`은 우리가 원하는 행위만 가져올 수 있습니다.
- 간단한 예로 bark(짖기)와 sniff(냄새 맡기)라는 함수를 갖는 Dog 클래스가 있다고 가정합니다.

```kotlin
abstract class Dog {
	open fun bark() { /*...*/}
	open fun sniff() { /*...*/}
}
```

- 로봇 강아지는 bark(짖기)만 가능하고, sniff(냄새 맡기)는 못하게 하려면 어떻게 해야 할까요?

```kotlin
class Labrador : Dog()

class RobtoDog : Dog() {
	override fun sniff() {
		throw Error("Operation not supported!!")
		// 인터페이스 분리 원칙에 위반
	}
}
```

- 이러한 코드는 RobotDog 가 필요도 없는 메서드를 갖기 때문에, 인터페이스 분리 원칙에 위반 됩니다. 또한 슈퍼 클래스의 동작을 서브클래스에서 깨버리므로, 리스코프 치환 원칙에도 위반 됩니다.
- 만약 RobotDog가 calculate(계산하기)라는 메서드를 갖는 Robot 이라는 클래스도 필요하다면 어떻게 해야 할까요? 코틀린은 다중 상속을 지원하지 않습니다.

```kotlin
abstract class Robot {
	open fun calculate() { /*...*/ }
}

class RobotDog : Dog(), Robot() // 오류
```

- 컴포지션을 사용하면, 이런 설계 문제가 전혀 발생하지 않습니다.
- 컴포지션이 무조건 좋다는것은 아니지만 타입 계층 구조를 표현해야 한다면, 인터페이스를 활용해서 다중 상속을 하는 것이 좋을 수도 있습니다.

## 캡슐화를 깨는 상속

- 상속을 활용할 때는 외부에서 이를 어떻게 활용하는지도 중요하지만, 내부적으로 이를 어떻게 활용하는지도 중요합니다. 내부적인 구현 방법 변경에 의해서 클래스의 캡슐화가 깨질 수 있기 때문입니다.
- CounterSet 클래스가 있다고 가정 합니다.
  - 이 클래스는 자신에게 추가된 요소의 개수를 알기 위한 elementsAdded 프로퍼티를 가지며, HashSet을 기반으로 구현되었습니다.

```kotlin
class CounterSet<T>: HashSet<T> {
	var elementsAdded: Int = 0
		private set

	override fun add(element: T): Boolean {
		elementsAdded++
		return super.add(element)
	}

	override fun addAll(elements: Collection<T>): Boolean {
		elements += elements.size
		return super.addAll(elements)
	}
}
```

- 이 클래스는 큰 문제가 없어 보이지만, 실제로는 제대로 동작하지 않습니다.

```kotlin
val counterList = CounterSet<String>()
counterList.addAll(listOf("A", "B", "C"))
print(counterList.emementsAdded) // 6
```

- 문제는 HashSet의 addAll 내부에서 add 를 사용했기 때문입니다.
  - addAll과 add에서 추가한 요소 개수를 중복해서 세므로, 요소 3개를 추가했는데 6이 출력되는 것입니다.
  - 간단하게 addAll 함수를 제거해 버리면, 이런 문제가 사라집니다.

```kotlin
class CounterSet<T>: HashSet<T> {
	var elementsAdded: Int = 0
		private set

	override fun add(element: T): Boolean {
		elementsAdded++
		return super.add(element)
	}
}
```

- 자바가 HashSet.addAll 을 최적화하고 내부적으로 add 를 호출하지 않는 방식으로 구현하기로 했다면 어떻게 어떻게 될까요? 만약 그렇게 된다면 현재 구현은 자바 업데이트가 이루어지는 순간, 예상하지 못한 형태로 동작합니다.
- 라이브러리의 구현이 변경되는 일은 꽤 자주 접할 수 있는 문제인데 어떻게 해야 이런 문제가 발생할 가능성을 막을 수 있을까요?
  - 상속 대신 컴포지션 사용

```kotlin
class CounterSet<T> {
	private val innerSet = HashSet<T>()
	var elementsAdded: Int = 0
		private set

	fun add(element: T) {
		elementsAdded++
		innerSet.add(element)
	}

	fun addAll(elements: Collection<T>) {
		elementsAdded += elements.size
		innerSet.addAll(elements)
	}
}

val counterList = CounterSet<String>()
counterList.addAll(listOf("A", "B", "C"))
println(counterList.elementsAdded) // 3
```

- 이렇게 수정했을때 다형성이 사라지는 문제가 발생
- CounterSet 은 더 이상 Set 이 아닙니다. 만약 이를 유지하고 싶다면, 위임 패턴을 사용할 수 있습니다.

### 위임 패턴

- 클래스가 인터페이스를 상속받게 하고, 포함한 객체의 메서드 들을 이용해서 인터페이스에 정의한메서드를 구현하는 패턴입니다.
- 이렇게 구현된 메서드를 포워딩 메서드(forwarding method) 라고 부릅니다.

```kotlin
class CounterSet<T>(
	private val innerSet: MutableSet<T> = mutableSetOf()
): MutableSet<T> by innerSet {

	var elementsAdded: Int = 0
		private set

	override fun add(element: T): Boolean {
		elementsAdded++
		innerSet.add(element)
	}

	override fun addAll(elements: Collection<T>): Boolean {
		elementsAdded += elements.size
		return innerSet.addAll(elements)
	}
}
```

- 위 예제처럼 다형성이 필요한데, 상속된 메서드를 직접 활용하는 것이 위험할 때는 이와 같은 위임 패턴을 사용하는 것이 좋습니다.
- 컴포지션은 재사용하기 쉽고, 다 많은 유연성을 제공

## 오버라이딩 제한하기

- 개발자가 상속용으로 설계되지 않은 클래스를 상속하지 못하게 하려면, final 을 사용하면 됩니다. 그런데 어떤 이유로 상속은 허용까지만, 메서드는 오버라이드하지 못하게 만들고 싶은 경우가 있을 수 있습니다.
  - open 키워드를 사용
  - open 클래스는 open 메서드만 오버라이드 할 수 있습니다.

```kotlin
open class parent {
	fun a() { }
	open fun b() { }
}

class Child: Parent() {
	override fun a() {} // 오류
	override fun b() {}
}
```

- 상속용으로 설계된 메서드에만 open 을 붙이면 됩니다. 참고로 메서드 오버라이드할 때, 서브 클래스에서 해당 메서드에 final 을 붙을 수도 있습니다.

```kotlin
open class ProfileLoader: InternetLoader() {
	final override fun loadFromInternet() {
		// 프로파일 읽어 들임
	}
}
```

## 정리

- 컴포지션과 상속은 다음과 같은 차이가 있습니다.
  - `컴포지션은 더 안전합니다.` 다른 클래스의 내부적인 구현에 의존하지 않고, 외부에서 관찰되는 동작에만 의존하므로 안전합니다.
  - `컴포지션은 더 유연합니다.` 상속은 한 클래스만을 대상으로 할 수 있지만, 컴포지션은 여러 클래스를 대상으로 할 수 있습니다. 상속은 모든 것을 받지만, 컴포지션은 필요한 것만 받을 수 있습니다. 슈퍼클래스의 동작을 변경하면, 서브 클래스의 동작도 큰 영향을 받습니다. 하지만 컴포지션을 활용하면, 이러한 영향이 제한적입니다.
  - `컴포지션은 더 명시적입니다.` 이것은 장점이자 단점으로 슈퍼클래스의 메서드를 사용할 때는 리시버를 따로 지정하지 않아도 됩니다.(this 키워드를 사용하지 않아도 됩니다). 덜 명시적입니다다. 즉, 코드가 짧아질 수 있지만, 메서드가 어디에서 왔는지 혼동될 수 있으므로 위험할 수 있습니다. 컴포지션을 활용하면, 리시버를 명시적으로 활용할 수 밖에 없으므로 메서드가 어디에 있는 것인지 확실하게 알 수 있습니다.
  - `컴포지션은 생각보다 번거롭습니다.` 컴포지션은 객체를 명시적으로 사용해야 하므로, 대상 클래스에 일부 기능을 추가할 때 이를 포함하는 객체의 코드를 변경해야 합니다. 그래서 상속을 사용할 때보다 코드를 수정해야 하는 경우가 더 많습니다.
  - `상속은 다형성을 활용할 수 있습니다.` 이것은 양날의 검입니다. Animal 을 상속해서 Dog 를 만들었다면, 굉장히 편리하게 활용될 수 있을 것입니다. 하지만 이는 코드에 제한을 겁니다. Dog 는 반드시 Animal로 동작해야 하기 때문입니다. 상속을 사용할 경우 슈퍼클래스와 서브클래스의 규약을 항상 잘 지켜서 코드를 작성해야 합니다.
- 일반적으로 OOP 에서는 상속보다 컴포지션을 사용하는 것이 좋습니다.
- 상속은 어제 사용하면 좋을까요?
  - 명확한 ‘is a 관계’일 때 상속을 사용하는 것이 좋습니다.
  - 슈퍼클래스를 상속하는 모든 서브클래스는 슈퍼클래스로도 동작할 수 있어야 합니다.