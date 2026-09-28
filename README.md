# MockGuard

[![Maven Central](https://img.shields.io/maven-central/v/io.github.jfrz38/mockguard)](https://central.sonatype.com/artifact/io.github.jfrz38/mockguard)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.jfrz38/mockguard-scanner)](https://central.sonatype.com/artifact/io.github.jfrz38/mockguard-scanner)
[![Build](https://img.shields.io/github/actions/workflow/status/jfrz38/mockguard/pr-build.yml?branch=main)](https://github.com/jfrz38/mockguard/actions/workflows/pr-build.yml)
[![License](https://img.shields.io/github/license/jfrz38/mockguard)](LICENSE)

Strict Mockito verification for JVM tests.

MockGuard provides two independently versioned products:

- `mockguard`: a JUnit Jupiter extension that checks real mock interactions after each test.
- `mockguard-scanner`: an optional CLI that inspects compiled test bytecode in CI.

Use the runtime extension for behavioral guarantees. Add the scanner when a fast static gate is useful.

## Why MockGuard?

A mock is part of a test's behavior contract. Every tracked mock should either be verified with `verify(...)`, `verifyNoInteractions(...)`, or `verifyNoMoreInteractions(...)`, or be explicitly ignored. MockGuard reports the ambiguous case where a dependency is present but its role is never asserted.

This catches incomplete tests and can expose dependencies that do not belong in the exercised code path.

## Runtime Setup

MockGuard is published as `io.github.jfrz38:mockguard`. Replace `<mockguard-version>` with the version shown by the Maven Central badge.

```kotlin
dependencies {
    testImplementation("io.github.jfrz38:mockguard:<mockguard-version>")
    testImplementation("org.mockito:mockito-core:5.23.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.23.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:6.1.3")
}
```

```kotlin
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

`@MockGuard` registers the extension and initializes `@Mock` and `@Spy` fields when Mockito has not already initialized them.

## Verification Rules

MockGuard accepts standard Mockito verification styles, including:

- `verify(mock)` with verification modes such as `times`, `never`, and `atLeastOnce`
- `verifyNoInteractions(mock)`
- `verifyNoMoreInteractions(mock)`

`StrictMode.FAIL` fails the test. `StrictMode.WARN` logs violations without failing it.

Mockito-based wrappers such as `mockito-kotlin` work when they delegate to Mockito verification APIs. Other mocking frameworks are outside the supported scope.

### Ignore A Mock

Use an annotation:

```kotlin
@MockGuardIgnore
@Mock
lateinit var logger: Logger
```

Or ignore it programmatically:

```kotlin
MockGuards.ignore(logger)
```

### Guard Selected Mocks

For gradual adoption, mark critical fields with `@GuardedMock`. If any field in a class is guarded, MockGuard validates only guarded mocks.

```kotlin
@MockGuard(mode = StrictMode.FAIL)
class CheckoutServiceTest {
    @GuardedMock
    @Mock
    lateinit var paymentGateway: PaymentGateway

    @Mock
    lateinit var auditLogger: AuditLogger
}
```

## Scanner

Scanner releases provide ZIP and TAR distributions, a standalone `-cli.jar`, and checksums. Run it against compiled test classes:

```bash
mockguard-scanner \
  --class-dir=build/classes/java/test \
  --class-dir=build/classes/kotlin/test \
  --format=console \
  --mode=FAIL
```

The scanner is a CLI product. Its Kotlin implementation classes are not a supported programmatic API. See [the scanner guide](docs/scanner.md) for installation, all options, baselines, build-tool integration, formats, and limitations.

## Compatibility

The project targets Java 17 and tests a specific JUnit, Mockito, Kotlin, and scanner toolchain combination. See [compatibility](docs/compatibility.md) for the current tested versions. Versions not listed there are not implied to be unsupported, but are not claimed as verified.

## Architecture

The runtime and scanner remain separate modules and releases in one repository. See [architecture](docs/architecture.md) for their responsibilities and compatibility boundary.

## Development

Use the root Gradle wrapper; it is the authoritative build for all modules.

```bash
make test             # all product and consumer checks
make test-consumer    # project and Maven publication consumer suites
make build            # full build
make scan             # run the scanner against runtime test classes
```

Product-specific gates are available as `./gradlew mockguardCheck` and `./gradlew scannerCheck`. Maintainers should follow [RELEASING.md](RELEASING.md) for independent versioning, GitHub releases, and Maven Central publication.
