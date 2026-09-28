# Compatibility

MockGuard documents versions exercised by this repository's build and consumer suites. This is a tested matrix, not a minimum-version promise.

## Tested Toolchain

| Component | Tested version |
|---|---:|
| Java toolchain and bytecode target | 17 |
| Kotlin | 2.4.10 |
| JUnit | 6.1.3 |
| Mockito | 5.23.0 |
| mockito-kotlin | 6.3.0 |
| Byte Buddy | 1.18.11 |
| Groovy scanner fixtures | 5.1.1 |
| Gradle wrapper | 9.7.1 |

The runtime consumer suites compile and execute Java and Kotlin test fixtures. Publication smoke tests repeat that compilation against the staged Maven POM and JAR rather than a Gradle project dependency.

The scanner suites cover Java, Kotlin, and Groovy bytecode fixtures. Its publication smoke test launches `com.mockguard.scanner.MainKt` from the thin Maven artifact plus dependencies resolved from its published POM.

## Supported Scope

- JUnit Jupiter tests using Mockito mocks and spies
- Standard Mockito verification APIs
- Mockito wrappers, including tested `mockito-kotlin` calls, when they delegate to Mockito verification APIs
- JVM 17 or newer when running artifacts compiled for Java 17

MockK, ScalaMock, Spock mocks, EasyMock, JMock, and other mocking engines are outside the supported scope.

## Version Policy

`mockguard` and `mockguard-scanner` have independent versions. A runtime release does not require a scanner release, and vice versa. Compatibility between them is protected by consumer fixtures using the runtime's real public annotations and the scanner's recognized JVM descriptors.

Versions outside this table may work. They are not claimed as compatible until added to the automated test matrix.
