# Scanner CLI

`mockguard-scanner` is an optional static bytecode scanner for compiled JVM tests. It complements the runtime extension but cannot provide the same behavioral guarantees.

## Install

Download a ZIP or TAR archive from a `scanner-vX.Y.Z` GitHub release and use the generated script:

```text
bin/mockguard-scanner
bin/mockguard-scanner.bat
```

Releases also contain `mockguard-scanner-X.Y.Z-cli.jar` and `SHA256SUMS`. The standalone JAR runs with:

```bash
java -jar mockguard-scanner-X.Y.Z-cli.jar --class-dir=build/classes/kotlin/test
```

Build distributions locally with:

```bash
./gradlew :mockguard-scanner:installDist
./gradlew :mockguard-scanner:fatJar
```

## Usage

At least one `--class-dir` is required. Repeat it for mixed Java/Kotlin output:

```bash
mockguard-scanner \
  --class-dir=build/classes/java/test \
  --class-dir=build/classes/kotlin/test \
  --format=console
```

Options with values accept `--option=value` or `--option value`.

| Option | Meaning |
|---|---|
| `--class-dir=<path>` | Compiled class directory; repeatable and required |
| `--mode=FAIL\|WARN\|OFF` | Result mode; default `FAIL` |
| `--format=console\|json\|sonarqube` | Report format; default `console` |
| `--output=<file>` | Write the report to a file instead of stdout |
| `--baseline=<file>` | Suppress findings present in a baseline |
| `--write-baseline=<file>` | Write current findings as a baseline |
| `--fail-on=VIOLATIONS\|NEW` | Failure criterion; default `VIOLATIONS` |
| `--include=<pattern>` | Include matching class paths or names; repeatable |
| `--exclude=<pattern>` | Exclude matching class paths or names; repeatable |
| `--test=<class>#<method>[<descriptor>]` | Select a JVM method; repeatable |
| `--verbose` | Include skipped-class details in console output |
| `--help`, `-h` | Print help |

## Filters And Methods

Include and exclude values are simple wildcard patterns matched against relative `.class` paths or class names.

```bash
mockguard-scanner \
  --class-dir=build/classes/kotlin/test \
  --include='*ServiceTest' \
  --exclude='*Generated*'
```

Select individual methods by binary class name. Add a JVM descriptor to disambiguate overloads:

```bash
mockguard-scanner \
  --class-dir=build/classes/java/test \
  --test='com.example.OrderServiceTest#createsOrder(Ljava/lang/String;)V'
```

Kotlin backtick names use their literal JVM name. Nested classes use binary names such as `com.example.OuterTest$NestedTest`. Quote these values in the shell.

`--test` intersects with include/exclude filters. A missing method, ambiguous overload, or selected class removed by filters is an error. Without `--test`, findings are aggregated by class.

## Reports And Exit Status

| Format | Use |
|---|---|
| `console` | Human-readable local and CI output |
| `json` | Machine-readable MockGuard report |
| `sonarqube` | SonarQube Generic Issue Import JSON |

Configure SonarQube with the generated file through `sonar.externalIssuesReportPaths`.

| Mode | Behavior when the selected criterion finds violations |
|---|---|
| `FAIL` | Exit `1` |
| `WARN` | Print a warning and exit `0` |
| `OFF` | Produce output and exit `0` |

Invalid CLI usage and missing class directories also exit `1`. `--help` exits successfully.

`--fail-on=VIOLATIONS` evaluates all raw findings. `--fail-on=NEW` evaluates findings left after applying a baseline.

## Baselines

Create a baseline during gradual adoption:

```bash
mockguard-scanner \
  --class-dir=build/classes/kotlin/test \
  --write-baseline=mockguard-baseline.json \
  --mode=OFF
```

Fail only for new findings in CI:

```bash
mockguard-scanner \
  --class-dir=build/classes/kotlin/test \
  --baseline=mockguard-baseline.json \
  --fail-on=NEW \
  --mode=FAIL
```

Class-level baseline entries use `className`, `fieldName`, and `fieldType`. Method scans also use `methodName` and `methodDescriptor`, so one method cannot suppress another. Baseline versions 1 and 2 are supported. Version 1 class-level entries do not suppress method-level findings.

## Gradle Integration

The Maven artifact is a thin CLI JAR. Launch its main class with the resolved dependency classpath:

```kotlin
tasks.register<JavaExec>("mockguardScan") {
    val javaTestClasses = layout.buildDirectory.dir("classes/java/test")
    val kotlinTestClasses = layout.buildDirectory.dir("classes/kotlin/test")
    classpath = configurations.detachedConfiguration(
        dependencies.create("io.github.jfrz38:mockguard-scanner:<scanner-version>")
    )
    mainClass = "com.mockguard.scanner.MainKt"
    args(
        "--class-dir=${javaTestClasses.get().asFile}",
        "--class-dir=${kotlinTestClasses.get().asFile}",
        "--mode=FAIL",
    )
    dependsOn("compileTestJava", "compileTestKotlin")
}
```

## Maven Integration

Use `exec-maven-plugin` after test compilation and declare `io.github.jfrz38:mockguard-scanner:<scanner-version>` as a plugin dependency. Set `mainClass` to `com.mockguard.scanner.MainKt` and pass `--class-dir=${project.build.testOutputDirectory}`.

## Limitations

The scanner is intentionally heuristic. It does not currently resolve:

- mocks assigned to local variables before verification
- indirect verification helper methods
- dynamically created mocks from `Mockito.mock(...)`
- exact source line mapping
- individual parameterized, repeated, template, or dynamic test invocations
- inherited tests, lifecycle methods, or helper methods automatically
- lambda bodies and other compiler-generated methods called by a selected method
- SARIF output

Method selection targets methods physically declared in the selected class file. Direct Mockito calls and common `mockito-kotlin` wrappers are supported. Use the runtime extension when full execution behavior matters.
