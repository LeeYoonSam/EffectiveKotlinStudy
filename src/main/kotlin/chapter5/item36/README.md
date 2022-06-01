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