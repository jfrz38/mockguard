# Architecture

MockGuard is a modular monorepo with two products and one consumer-test module.

## Modules

| Module | Responsibility | Release |
|---|---|---|
| `mockguard` | JUnit Jupiter runtime extension and public annotations/API | `mockguard-vX.Y.Z` |
| `mockguard-scanner` | Static bytecode CLI | `scanner-vX.Y.Z` |
| `mockguard-consumer-tests` | Java/Kotlin usage and publication contract tests | Not published |

The root Gradle build and wrapper are authoritative. Root gates keep checks product-specific while `allCheck` and `build` verify the complete repository.

## Runtime Boundary

The runtime observes actual test execution. It tracks Mockito mocks, records verification, and validates them after each test. Byte Buddy instrumentation supports Mockito's static `verifyNoInteractions` and `verifyNoMoreInteractions` APIs without requiring MockGuard wrappers.

Public runtime API lives in `com.mockguard`. Implementation details live in `com.mockguard.internal`.

## Scanner Boundary

The scanner analyzes compiled `.class` files and recognizes bytecode patterns. It does not execute tests or reproduce JUnit discovery. Its public entry point is the JVM CLI main class `com.mockguard.scanner.MainKt`; Kotlin implementation types are internal and are not a supported library API.

The scanner has no production dependency on the runtime. It recognizes the binary descriptors of `@MockGuardIgnore` and `@GuardedMock`, which keeps the CLI independently deployable.

## Compatibility Contract

Cross-product consumer tests compile fixtures using the real runtime annotations and scan their bytecode. This protects the descriptor-level contract without introducing a production dependency from scanner to runtime.

Fast consumer tests use Gradle project dependencies and the scanner fat JAR. Publication smoke tests additionally stage Maven publications, compile consumers against Maven coordinates, and launch the scanner using the thin artifact's resolved runtime classpath. Both layers are retained because they detect different failures.

## Release Model

Runtime and scanner versions remain independent. Their product gates, tags, GitHub releases, Maven coordinates, and release assets are separate. Shared dependency versions and build tooling live at the repository root; product versions remain in each module's `build.gradle.kts`.
