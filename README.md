# Mock Guard

Strict mock verification for JVM unit tests.

`mockguard` is a Kotlin/JVM library for JUnit 5 + Mockito that makes mock verification explicit. When enabled, every tracked mock in a test must be verified with a Mockito verification such as `verify(...)` or `verifyNoInteractions(...)`, or be opted out explicitly.

## What it enforces

- A mock with calls but no verification is reported.
- A mock with no calls and no `verifyNoInteractions(...)` is also reported.
- Ignored mocks are excluded from validation.
- `StrictMode.WARN` logs warnings.
- `StrictMode.FAIL` fails the test.

## Usage

```kotlin
import com.mockguard.MockGuard
import com.mockguard.StrictMode
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.verify

@MockGuard(mode = StrictMode.FAIL)
class OrderServiceTest {

    @Mock lateinit var paymentGateway: PaymentGateway
    @Mock lateinit var logger: Logger

    @Test
    fun processesOrders() {
        paymentGateway.charge(100)

        verify(paymentGateway).charge(100)
        // logger would need verifyNoInteractions(logger) or @MockGuardIgnore
    }
}
```

`@MockGuard` activates the JUnit 5 extension automatically and initializes `@Mock` / `@Spy` fields if Mockito has not already done so.

## Ignoring a mock

Annotation-based:

```kotlin
@MockGuardIgnore
@Mock
lateinit var logger: Logger
```

Programmatic:

```kotlin
MockGuards.ignore(logger)
```

The programmatic API is exposed as `MockGuards` because the annotation already owns the `MockGuard` name in Kotlin.

## Gradle

```kotlin
dependencies {
    testImplementation("com.mockguard:mockguard:1.0-SNAPSHOT")
    testImplementation("org.mockito:mockito-junit-jupiter:5.11.0")
}
```
