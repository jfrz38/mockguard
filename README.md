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

Mockito-based wrappers, such as `mockito-kotlin`, are supported when they delegate to Mockito verification APIs. Other mocking frameworks such as MockK, ScalaMock, Spock mocks, EasyMock, or JMock are outside the scope of `mockguard`.

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

## Static Scanner CLI

`mockguard-scanner` is an optional static bytecode scanner. It analyzes compiled test `.class` files and reports Mockito mocks that are structurally present but not verified in the same class.

It complements the runtime JUnit extension:

| Tool                | When it runs                | What it checks                                  |
|---------------------|-----------------------------|-------------------------------------------------|
| `mockguard`         | During test execution       | Real runtime mock invocations and verifications |
| `mockguard-scanner` | After test compilation / CI | Static bytecode patterns in compiled classes    |

### Install / Download

Download the `mockguard-scanner` distribution archive from the GitHub release and unpack it. The archive contains platform scripts:

```text
bin/mockguard-scanner
bin/mockguard-scanner.bat
```

### Run the CLI

Run it against compiled test classes:

```bash
mockguard-scanner \
  --class-dir=mockguard/build/classes/kotlin/test \
  --format=console
```

For mixed Java/Kotlin projects, repeat `--class-dir`:

```bash
mockguard-scanner \
  --class-dir=build/classes/java/test \
  --class-dir=build/classes/kotlin/test \
  --format=console
```

If you prefer the standalone JAR, use `java -jar`:

```bash
java -jar mockguard-scanner-0.1.0-cli.jar \
  --class-dir=build/classes/kotlin/test \
  --format=console
```

### Build from source

Build the CLI distribution from this repository:

```bash
./gradlew :mockguard-scanner:installDist
```

The generated scripts are available under:

```text
mockguard-scanner/build/install/mockguard-scanner/bin/
```

Build the standalone CLI JAR from this repository:

```bash
./gradlew :mockguard-scanner:fatJar
```

Run the generated JAR with:

```bash
java -jar mockguard-scanner/build/libs/mockguard-scanner-0.1.0-cli.jar \
  --class-dir=build/classes/kotlin/test \
  --format=console
```

CLI options with values accept either `--option=value` or `--option value`.

Filter scanned classes with simple wildcard patterns matched against the relative `.class` path or class name:

```bash
mockguard-scanner \
  --class-dir=build/classes/kotlin/test \
  --include='*ServiceTest' \
  --exclude='*Generated*' \
  --format=console
```

Scan one JVM test method with `--test=<binary-class>#<method>`. Repeat the option to scan a list of methods:

```bash
mockguard-scanner \
  --class-dir=build/classes/kotlin/test \
  --test='com.example.OrderServiceTest#createsOrder' \
  --test='com.example.OrderServiceTest#rejectsInvalidOrder' \
  --format=console
```

The method state is isolated for every selected test, so a verification in one method cannot satisfy another method. If the method is overloaded, append its JVM descriptor:

```bash
mockguard-scanner \
  --class-dir=build/classes/java/test \
  --test='com.example.OrderServiceTest#createsOrder(Ljava/lang/String;)V'
```

Kotlin backtick names use their literal JVM name, and nested classes use their binary name. Quote both forms in the shell:

```bash
--test='com.example.OrderServiceTest#rejects invalid order'
--test='com.example.OuterTest$NestedTest#rejectsOrder'
```

`--test` intersects with `--include` and `--exclude`. Selecting a missing method, an ambiguous overload, or a class removed by those filters is an error. Without `--test`, scanning remains aggregated by class.

Generate a SonarQube Generic Issue Import report:

```bash
mockguard-scanner \
  --class-dir=mockguard/build/classes/kotlin/test \
  --format=sonarqube \
  --output=build/reports/mockguard-issues.json
```

Supported scanner formats:

| Format      | Purpose                                           |
|-------------|---------------------------------------------------|
| `console`   | Human-readable local output                       |
| `json`      | Machine-readable output for custom tooling        |
| `sonarqube` | JSON report for `sonar.externalIssuesReportPaths` |

Supported scanner modes:

| Mode   | Behavior                                   |
|--------|--------------------------------------------|
| `FAIL` | Exit with code 1 when violations are found |
| `WARN` | Print a warning but exit successfully      |
| `OFF`  | Produce output but never fail              |

Exit codes:

| Exit code | Meaning                                                                            |
|-----------|------------------------------------------------------------------------------------|
| `0`       | Scan completed without failing, including `WARN`/`OFF` runs with violations        |
| `1`       | Invalid CLI usage, missing class directory, or violations found with `--mode=FAIL` |

Baseline support for gradual adoption:

```bash
# Write the current scanner violations to a baseline file.
mockguard-scanner \
  --class-dir=build/classes/kotlin/test \
  --write-baseline=mockguard-baseline.json \
  --mode=OFF

# In CI, ignore known baseline violations and fail only on new ones.
mockguard-scanner \
  --class-dir=build/classes/kotlin/test \
  --baseline=mockguard-baseline.json \
  --fail-on=NEW \
  --mode=FAIL
```

Baseline entries from class-level scans are keyed by `className`, `fieldName`, and `fieldType`. Entries from `--test` scans additionally include `methodName` and `methodDescriptor`, so one method cannot suppress a violation from another. Version 1 baseline files remain readable, but class-level entries do not suppress method-level findings.

Example Gradle task in a consuming project:

```kotlin
tasks.register<JavaExec>("mockguardScan") {
    val javaTestClasses = layout.buildDirectory.dir("classes/java/test")
    val kotlinTestClasses = layout.buildDirectory.dir("classes/kotlin/test")
    classpath = configurations.detachedConfiguration(
        dependencies.create("io.github.jfrz38:mockguard-scanner:0.1.0")
    )
    mainClass = "com.mockguard.scanner.MainKt"
    args(
        "--class-dir=${javaTestClasses.get().asFile}",
        "--class-dir=${kotlinTestClasses.get().asFile}",
        "--format=console",
        "--mode=FAIL",
        "--verbose",
    )
    dependsOn("compileTestJava", "compileTestKotlin")
}
```

Example Maven usage through `exec-maven-plugin`:

```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>exec-maven-plugin</artifactId>
    <executions>
        <execution>
            <id>mockguard-scan</id>
            <phase>test-compile</phase>
            <goals>
                <goal>java</goal>
            </goals>
            <configuration>
                <mainClass>com.mockguard.scanner.MainKt</mainClass>
                <arguments>
                    <argument>--class-dir=${project.build.testOutputDirectory}</argument>
                    <argument>--format=console</argument>
                    <argument>--mode=FAIL</argument>
                </arguments>
            </configuration>
        </execution>
    </executions>
</plugin>
```

### Scanner Limitations

The scanner is intentionally lightweight and heuristic in V1. It currently does not support:

- mocks assigned to local variables before verification, such as `val m = service; verify(m)`
- indirect helper verification, such as `verifyService(service)`
- dynamically created mocks via `Mockito.mock(...)`
- exact source line mapping in reports
- SARIF output
- individual invocations of parameterized, repeated, or template tests; `--test` selects their shared JVM method
- individual dynamic tests created by a test factory
- inherited test resolution or automatic inclusion of lifecycle and helper methods
- lambda bodies or other compiler-generated methods called by a selected method

Method selection targets methods physically declared in the selected `.class` file. It does not attempt to reproduce JUnit discovery or runtime execution.

The scanner supports direct Mockito verification calls and common `mockito-kotlin` verification wrappers. Other mocking frameworks such as MockK, ScalaMock, Spock mocks, EasyMock, or JMock are outside the scanner scope.

Use `--verbose` to include details about `.class` files that could not be scanned. Without `--verbose`, the console output only reports the skipped class count.

Use the runtime `mockguard` extension when you need full behavioral guarantees.

## Requirements

- JUnit 5
- Mockito 5
- Java 17+
