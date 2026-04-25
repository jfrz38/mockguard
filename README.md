# Mock Guard

[![Maven Central](https://img.shields.io/maven-central/v/io.github.jfrz38/mockguard)](https://central.sonatype.com/artifact/io.github.jfrz38/mockguard)
[![Build](https://img.shields.io/github/actions/workflow/status/jfrz38/mockguard/pr-build.yml?branch=main)](https://github.com/jfrz38/mockguard/actions/workflows/pr-build.yml)
[![License](https://img.shields.io/github/license/jfrz38/mockguard)](https://github.com/jfrz38/mockguard/blob/main/LICENSE)

Strict mock verification for JVM unit tests.

`mockguard` is a Kotlin/JVM library for JUnit 5 + Mockito that makes mock verification explicit. When enabled, every tracked mock in a test must be verified with a Mockito verification such as `verify(...)`, `verifyNoInteractions(...)`, or `verifyNoMoreInteractions(...)`, or be opted out explicitly.

## Why Use It?

Mocks are part of the behavior contract of a test, not just test setup.

If a dependency is injected into a unit under test, `mockguard` assumes the test should make that relationship explicit:

- either the dependency is expected to be used, so you verify how it is called
- or the dependency is expected to stay unused, so you verify that it is not called

What `mockguard` tries to prevent is the ambiguous middle ground where a mock is present but its role is never asserted. In practice, that often hides one of two problems:

- the test is incomplete and forgot to verify an important interaction
- the production design is carrying dependencies that are not really part of the business logic being exercised

That second case is especially valuable. If a service is injected but the test does not even need to assert that it is not called, that can be a sign of unnecessary coupling or a dependency that should not be part of that code path in the first place.

## What It Enforces

- A mock with calls but no verification is reported.
- A mock with no calls and no `verifyNoInteractions(...)` is also reported.
- Ignored mocks are excluded from validation.
- `StrictMode.WARN` logs warnings.
- `StrictMode.FAIL` fails the test.

## Supported Verification Styles

`mockguard` accepts standard Mockito verifications, including:

- `verify(mock)`
- `verify(mock, times(...))`
- `verify(mock, never())`
- `verify(mock, atLeastOnce())`
- `verifyNoInteractions(mock)`
- `verifyNoMoreInteractions(mock)`

`verifyNoInteractions(...)` and `verifyNoMoreInteractions(...)` are supported transparently through runtime interception of Mockito, so test code does not need wrappers or custom APIs.

## Kotlin Example

```kotlin
import com.mockguard.GuardedMock
import com.mockguard.MockGuard
import com.mockguard.StrictMode
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions

@MockGuard(mode = StrictMode.FAIL)
class OrderServiceTest {

    @Mock lateinit var paymentGateway: PaymentGateway
    @Mock lateinit var logger: Logger

    @Test
    fun processesOrders() {
        paymentGateway.charge(100)

        verify(paymentGateway).charge(100)
        verifyNoInteractions(logger)
    }
}
```

`@MockGuard` activates the JUnit 5 extension automatically and initializes `@Mock` / `@Spy` fields if Mockito has not already done so.

## Java Example With Maven

```xml
<dependencies>
    <dependency>
        <groupId>io.github.jfrz38</groupId>
        <artifactId>mockguard</artifactId>
        <version>0.1.0</version> <!-- Replace with the published version -->
        <scope>test</scope>
    </dependency>

    <dependency>
        <groupId>org.mockito</groupId>
        <artifactId>mockito-core</artifactId>
        <version>5.11.0</version>
        <scope>test</scope>
    </dependency>

    <dependency>
        <groupId>org.mockito</groupId>
        <artifactId>mockito-junit-jupiter</artifactId>
        <version>5.11.0</version>
        <scope>test</scope>
    </dependency>

    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter-engine</artifactId>
        <version>5.10.2</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

```java
import com.mockguard.MockGuard;
import com.mockguard.StrictMode;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@MockGuard(mode = StrictMode.FAIL)
class OrderServiceTest {

    @Mock
    PaymentGateway paymentGateway;

    @Mock
    Logger logger;

    @Test
    void processesOrders() {
        paymentGateway.charge(100);

        verify(paymentGateway).charge(100);
        verifyNoInteractions(logger);
    }
}
```

## Ignoring A Mock

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

## Focusing On One Critical Mock

You may not want to enforce strict verification for every dependency in every test class right away. That is a valid way to adopt `mockguard` gradually.

A good option is to use `mockguard` when one dependency is especially important for the business flow.

If you want to focus on a single critical mock, you can:

- Enable `@MockGuard` for the test class
- Mark the critical dependency with `@GuardedMock`

If at least one field is annotated with `@GuardedMock`, `mockguard` validates only those guarded mocks and ignores the rest.

Example:

```kotlin
@MockGuard(mode = StrictMode.FAIL)
class CheckoutServiceTest {

    @GuardedMock
    @Mock
    lateinit var paymentGateway: PaymentGateway

    @Mock
    lateinit var auditLogger: AuditLogger

    @Test
    fun chargesTheCustomer() {
        checkoutService.charge(100)

        verify(paymentGateway).charge(100)
    }
}
```

This gives you a focused adoption path: you can start by protecting the mocks that are truly critical to the business, and widen the rule later as the test suite becomes more explicit.

## Gradle

```kotlin
dependencies {
    testImplementation("io.github.jfrz38:mockguard:0.1.0") // Replace with the published version
    testImplementation("org.mockito:mockito-core:5.11.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.11.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.2")
}
```

## Requirements

- JUnit 5
- Mockito 5
- Java 17+
