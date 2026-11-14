# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

<!-- git-cliff: end of header -->
## [Unreleased]


### Added
- Add PR build workflow for main and develop branches (#14)

Initial github workflow
- Add dependency update group to Dependabot config
- Add release actions (#32)

* update workflows to publish

* create bump action

* add missing permissions
- Add bump version action
- Add cooldown for dependency updates

Added cooldown settings for Gradle and GitHub Actions updates.
- Add scanner CLI and multi-module validation
- Add scanner test selection via --test
- Add scanner method test (#56)
- Separate mockguard and scanner release lifecycles (#59)
- Complete independent product release automation (#63)
- Add Maven Central badge for scanner


### Fixed
- Fix actions not using working-directory since it is using make
- Fix consumer test suite dependencies


### Changed
- [iss-2] add github workspace
- [iss-2] improve gradle build
- [iss-2] prepare gradle build to publish
- [iss-2] fix gradle build to publish
- [iss-6] Ensure AfterEach is valid (#8)

* [iss-6] add after each tests

* [iss-6] add relevant methods tests
- [iss-7] add guard for mocks alone (#10)
- [iss-11] add CODEOWNERS (#12)
- [iss-4] Prepare gradle to publish to remote (#5)

* [iss-4] create action to publish

* [iss-4] improve action using PASSWORD instead of TOKEN secret
- [iss-9] create protect branch workflow (#15)
- [iss-17] fix publish workflow (#18)
- Sync to develop (#20)

* Sync to main (#16)

* [iss-2] add github workspace

* [iss-2] improve gradle build

* [iss-2] prepare gradle build to publish

* [iss-2] fix gradle build to publish

* [iss-6] Ensure AfterEach is valid (#8)

* [iss-6] add after each tests

* [iss-6] add relevant methods tests

* [iss-7] add guard for mocks alone (#10)

* [iss-11] add CODEOWNERS (#12)

* [iss-4] Prepare gradle to publish to remote (#5)

* [iss-4] create action to publish

* [iss-4] improve action using PASSWORD instead of TOKEN secret

* Add PR build workflow for main and develop branches (#14)

Initial github workflow

* [iss-9] create protect branch workflow (#15)

* Sync to main (#19)

* [iss-2] add github workspace

* [iss-2] improve gradle build

* [iss-2] prepare gradle build to publish

* [iss-2] fix gradle build to publish

* [iss-6] Ensure AfterEach is valid (#8)

* [iss-6] add after each tests

* [iss-6] add relevant methods tests

* [iss-7] add guard for mocks alone (#10)

* [iss-11] add CODEOWNERS (#12)

* [iss-4] Prepare gradle to publish to remote (#5)

* [iss-4] create action to publish

* [iss-4] improve action using PASSWORD instead of TOKEN secret

* Add PR build workflow for main and develop branches (#14)

Initial github workflow

* [iss-9] create protect branch workflow (#15)

* [iss-17] fix publish workflow (#18)
- [iss-21] Fix publish workflow (#22)

* [iss-21] fix release action

* [iss-21] improve release action

* [iss-21] fix build gradle

* [iss-21] use maven token

* [iss-21] update build retry data
- [docs] add badges (#23)
- Configure Dependabot for Gradle and GitHub Actions (#24)
- [fix] remove invalid property versioning-strategy (#25)
- Delete .idea directory
- Make bump version workflow resilient to reruns (#34)

* Make bump version workflow resilient to reruns

* Create PR as draft

* Create PR from and to selected branch

* set new branch as chore/
- Protect publish to bre run only from main
- Sync to main (#38) (#39)

* [iss-2] add github workspace

* [iss-2] improve gradle build

* [iss-2] prepare gradle build to publish

* [iss-2] fix gradle build to publish

* [iss-6] Ensure AfterEach is valid (#8)

* [iss-6] add after each tests

* [iss-6] add relevant methods tests

* [iss-7] add guard for mocks alone (#10)

* [iss-11] add CODEOWNERS (#12)

* [iss-4] Prepare gradle to publish to remote (#5)

* [iss-4] create action to publish

* [iss-4] improve action using PASSWORD instead of TOKEN secret

* Add PR build workflow for main and develop branches (#14)

Initial github workflow

* [iss-9] create protect branch workflow (#15)

* [iss-17] fix publish workflow (#18)

* Sync to develop (#20)

* Sync to main (#16)

* [iss-2] add github workspace

* [iss-2] improve gradle build

* [iss-2] prepare gradle build to publish

* [iss-2] fix gradle build to publish

* [iss-6] Ensure AfterEach is valid (#8)

* [iss-6] add after each tests

* [iss-6] add relevant methods tests

* [iss-7] add guard for mocks alone (#10)

* [iss-11] add CODEOWNERS (#12)

* [iss-4] Prepare gradle to publish to remote (#5)

* [iss-4] create action to publish

* [iss-4] improve action using PASSWORD instead of TOKEN secret

* Add PR build workflow for main and develop branches (#14)

Initial github workflow

* [iss-9] create protect branch workflow (#15)

* Sync to main (#19)

* [iss-2] add github workspace

* [iss-2] improve gradle build

* [iss-2] prepare gradle build to publish

* [iss-2] fix gradle build to publish

* [iss-6] Ensure AfterEach is valid (#8)

* [iss-6] add after each tests

* [iss-6] add relevant methods tests

* [iss-7] add guard for mocks alone (#10)

* [iss-11] add CODEOWNERS (#12)

* [iss-4] Prepare gradle to publish to remote (#5)

* [iss-4] create action to publish

* [iss-4] improve action using PASSWORD instead of TOKEN secret

* Add PR build workflow for main and develop branches (#14)

Initial github workflow

* [iss-9] create protect branch workflow (#15)

* [iss-17] fix publish workflow (#18)

* [iss-21] Fix publish workflow (#22)

* [iss-21] fix release action

* [iss-21] improve release action

* [iss-21] fix build gradle

* [iss-21] use maven token

* [iss-21] update build retry data

* [docs] add badges (#23)

* Configure Dependabot for Gradle and GitHub Actions (#24)

* [fix] remove invalid property versioning-strategy (#25)

* Delete .idea directory

* Add dependency update group to Dependabot config

* [CI Update]: Bump the dependencies group with 3 updates (#30)

Bumps the dependencies group with 3 updates: [actions/checkout](https://github.com/actions/checkout), [actions/setup-java](https://github.com/actions/setup-java) and [gradle/actions](https://github.com/gradle/actions).


Updates `actions/checkout` from 4 to 6
- [Release notes](https://github.com/actions/checkout/releases)
- [Changelog](https://github.com/actions/checkout/blob/main/CHANGELOG.md)
- [Commits](https://github.com/actions/checkout/compare/v4...v6)

Updates `actions/setup-java` from 4 to 5
- [Release notes](https://github.com/actions/setup-java/releases)
- [Commits](https://github.com/actions/setup-java/compare/v4...v5)

Updates `gradle/actions` from 4 to 6
- [Release notes](https://github.com/gradle/actions/releases)
- [Commits](https://github.com/gradle/actions/compare/v4...v6)

---
updated-dependencies:
- dependency-name: actions/checkout
  dependency-version: '6'
  dependency-type: direct:production
  update-type: version-update:semver-major
  dependency-group: dependencies
- dependency-name: actions/setup-java
  dependency-version: '5'
  dependency-type: direct:production
  update-type: version-update:semver-major
  dependency-group: dependencies
- dependency-name: gradle/actions
  dependency-version: '6'
  dependency-type: direct:production
  update-type: version-update:semver-major
  dependency-group: dependencies
...




* [Dependency Update]: Bump the dependencies group (#29)

Bumps the dependencies group in /mockguard with 11 updates:

| Package | From | To |
| --- | --- | --- |
| [org.junit.jupiter:junit-jupiter-api](https://github.com/junit-team/junit-framework) | `5.10.2` | `6.0.3` |
| [org.mockito:mockito-core](https://github.com/mockito/mockito) | `5.11.0` | `5.23.0` |
| [net.bytebuddy:byte-buddy](https://github.com/raphw/byte-buddy) | `1.14.18` | `1.18.8` |
| [net.bytebuddy:byte-buddy-agent](https://github.com/raphw/byte-buddy) | `1.14.18` | `1.18.8` |
| [org.junit.jupiter:junit-jupiter-engine](https://github.com/junit-team/junit-framework) | `5.10.2` | `6.0.3` |
| [org.junit.jupiter:junit-jupiter-params](https://github.com/junit-team/junit-framework) | `5.10.2` | `6.0.3` |
| [org.mockito:mockito-junit-jupiter](https://github.com/mockito/mockito) | `5.11.0` | `5.23.0` |
| [org.junit.platform:junit-platform-launcher](https://github.com/junit-team/junit-framework) | `1.10.2` | `6.0.3` |
| [jvm](https://github.com/JetBrains/kotlin) | `2.0.20` | `2.3.20` |
| org.gradle.toolchains.foojay-resolver-convention | `0.8.0` | `1.0.0` |
| [gradle-wrapper](https://github.com/gradle/gradle) | `8.5` | `9.4.1` |


Updates `org.junit.jupiter:junit-jupiter-api` from 5.10.2 to 6.0.3
- [Release notes](https://github.com/junit-team/junit-framework/releases)
- [Commits](https://github.com/junit-team/junit-framework/compare/r5.10.2...r6.0.3)

Updates `org.mockito:mockito-core` from 5.11.0 to 5.23.0
- [Release notes](https://github.com/mockito/mockito/releases)
- [Commits](https://github.com/mockito/mockito/compare/v5.11.0...v5.23.0)

Updates `net.bytebuddy:byte-buddy` from 1.14.18 to 1.18.8
- [Release notes](https://github.com/raphw/byte-buddy/releases)
- [Changelog](https://github.com/raphw/byte-buddy/blob/master/release-notes.md)
- [Commits](https://github.com/raphw/byte-buddy/compare/byte-buddy-1.14.18...byte-buddy-1.18.8)

Updates `net.bytebuddy:byte-buddy-agent` from 1.14.18 to 1.18.8
- [Release notes](https://github.com/raphw/byte-buddy/releases)
- [Changelog](https://github.com/raphw/byte-buddy/blob/master/release-notes.md)
- [Commits](https://github.com/raphw/byte-buddy/compare/byte-buddy-1.14.18...byte-buddy-1.18.8)

Updates `org.junit.jupiter:junit-jupiter-engine` from 5.10.2 to 6.0.3
- [Release notes](https://github.com/junit-team/junit-framework/releases)
- [Commits](https://github.com/junit-team/junit-framework/compare/r5.10.2...r6.0.3)

Updates `org.junit.jupiter:junit-jupiter-params` from 5.10.2 to 6.0.3
- [Release notes](https://github.com/junit-team/junit-framework/releases)
- [Commits](https://github.com/junit-team/junit-framework/compare/r5.10.2...r6.0.3)

Updates `org.mockito:mockito-junit-jupiter` from 5.11.0 to 5.23.0
- [Release notes](https://github.com/mockito/mockito/releases)
- [Commits](https://github.com/mockito/mockito/compare/v5.11.0...v5.23.0)

Updates `org.junit.platform:junit-platform-launcher` from 1.10.2 to 6.0.3
- [Release notes](https://github.com/junit-team/junit-framework/releases)
- [Commits](https://github.com/junit-team/junit-framework/commits/r6.0.3)

Updates `jvm` from 2.0.20 to 2.3.20
- [Release notes](https://github.com/JetBrains/kotlin/releases)
- [Changelog](https://github.com/JetBrains/kotlin/blob/master/ChangeLog.md)
- [Commits](https://github.com/JetBrains/kotlin/compare/v2.0.20...v2.3.20)

Updates `org.gradle.toolchains.foojay-resolver-convention` from 0.8.0 to 1.0.0

Updates `gradle-wrapper` from 8.5 to 9.4.1
- [Release notes](https://github.com/gradle/gradle/releases)
- [Commits](https://github.com/gradle/gradle/compare/v8.5.0...v9.4.1)

---
updated-dependencies:
- dependency-name: org.junit.jupiter:junit-jupiter-api
  dependency-version: 6.0.3
  dependency-type: direct:production
  update-type: version-update:semver-major
  dependency-group: dependencies
- dependency-name: org.mockito:mockito-core
  dependency-version: 5.23.0
  dependency-type: direct:production
  update-type: version-update:semver-minor
  dependency-group: dependencies
- dependency-name: net.bytebuddy:byte-buddy
  dependency-version: 1.18.8
  dependency-type: direct:production
  update-type: version-update:semver-minor
  dependency-group: dependencies
- dependency-name: net.bytebuddy:byte-buddy-agent
  dependency-version: 1.18.8
  dependency-type: direct:production
  update-type: version-update:semver-minor
  dependency-group: dependencies
- dependency-name: org.junit.jupiter:junit-jupiter-engine
  dependency-version: 6.0.3
  dependency-type: direct:production
  update-type: version-update:semver-major
  dependency-group: dependencies
- dependency-name: org.junit.jupiter:junit-jupiter-params
  dependency-version: 6.0.3
  dependency-type: direct:production
  update-type: version-update:semver-major
  dependency-group: dependencies
- dependency-name: org.mockito:mockito-junit-jupiter
  dependency-version: 5.23.0
  dependency-type: direct:production
  update-type: version-update:semver-minor
  dependency-group: dependencies
- dependency-name: org.junit.platform:junit-platform-launcher
  dependency-version: 6.0.3
  dependency-type: direct:production
  update-type: version-update:semver-major
  dependency-group: dependencies
- dependency-name: jvm
  dependency-version: 2.3.20
  dependency-type: direct:production
  update-type: version-update:semver-minor
  dependency-group: dependencies
- dependency-name: org.gradle.toolchains.foojay-resolver-convention
  dependency-version: 1.0.0
  dependency-type: direct:production
  update-type: version-update:semver-major
  dependency-group: dependencies
- dependency-name: gradle-wrapper
  dependency-version: 9.4.1
  dependency-type: direct:production
  update-type: version-update:semver-major
  dependency-group: dependencies
...




* chore: Bump version from 0.1.0 to 0.1.1 (#31)

* Add release actions (#32)

* update workflows to publish

* create bump action

* add missing permissions

* remove duplicate emptyJavadocJar

* Make bump version workflow resilient to reruns (#34)

* Make bump version workflow resilient to reruns

* Create PR as draft

* Create PR from and to selected branch

* set new branch as chore/

---------

Signed-off-by: dependabot[bot] <support@github.com>
Co-authored-by: dependabot[bot] <49699333+dependabot[bot]@users.noreply.github.com>
- Improve publish action (#40)

* enforce to use latest tag

* use check-version-change action
- Refactor CodeQL workflow configuration (#41)

* Refactor CodeQL workflow configuration

Updated CodeQL workflow to refine triggers and simplify the configuration.

* Upgrade checkout action and clean up workflow

Updated CodeQL workflow to use actions/checkout@v6 and removed manual build steps.
- Sync to main (#38)

* [iss-2] add github workspace

* [iss-2] improve gradle build

* [iss-2] prepare gradle build to publish

* [iss-2] fix gradle build to publish

* [iss-6] Ensure AfterEach is valid (#8)

* [iss-6] add after each tests

* [iss-6] add relevant methods tests

* [iss-7] add guard for mocks alone (#10)

* [iss-11] add CODEOWNERS (#12)

* [iss-4] Prepare gradle to publish to remote (#5)

* [iss-4] create action to publish

* [iss-4] improve action using PASSWORD instead of TOKEN secret

* Add PR build workflow for main and develop branches (#14)

Initial github workflow

* [iss-9] create protect branch workflow (#15)

* [iss-17] fix publish workflow (#18)

* Sync to develop (#20)

* Sync to main (#16)

* [iss-2] add github workspace

* [iss-2] improve gradle build

* [iss-2] prepare gradle build to publish

* [iss-2] fix gradle build to publish

* [iss-6] Ensure AfterEach is valid (#8)

* [iss-6] add after each tests

* [iss-6] add relevant methods tests

* [iss-7] add guard for mocks alone (#10)

* [iss-11] add CODEOWNERS (#12)

* [iss-4] Prepare gradle to publish to remote (#5)

* [iss-4] create action to publish

* [iss-4] improve action using PASSWORD instead of TOKEN secret

* Add PR build workflow for main and develop branches (#14)

Initial github workflow

* [iss-9] create protect branch workflow (#15)

* Sync to main (#19)

* [iss-2] add github workspace

* [iss-2] improve gradle build

* [iss-2] prepare gradle build to publish

* [iss-2] fix gradle build to publish

* [iss-6] Ensure AfterEach is valid (#8)

* [iss-6] add after each tests

* [iss-6] add relevant methods tests

* [iss-7] add guard for mocks alone (#10)

* [iss-11] add CODEOWNERS (#12)

* [iss-4] Prepare gradle to publish to remote (#5)

* [iss-4] create action to publish

* [iss-4] improve action using PASSWORD instead of TOKEN secret

* Add PR build workflow for main and develop branches (#14)

Initial github workflow

* [iss-9] create protect branch workflow (#15)

* [iss-17] fix publish workflow (#18)

* [iss-21] Fix publish workflow (#22)

* [iss-21] fix release action

* [iss-21] improve release action

* [iss-21] fix build gradle

* [iss-21] use maven token

* [iss-21] update build retry data

* [docs] add badges (#23)

* Configure Dependabot for Gradle and GitHub Actions (#24)

* [fix] remove invalid property versioning-strategy (#25)

* Delete .idea directory

* Add dependency update group to Dependabot config

* [CI Update]: Bump the dependencies group with 3 updates (#30)

Bumps the dependencies group with 3 updates: [actions/checkout](https://github.com/actions/checkout), [actions/setup-java](https://github.com/actions/setup-java) and [gradle/actions](https://github.com/gradle/actions).


Updates `actions/checkout` from 4 to 6
- [Release notes](https://github.com/actions/checkout/releases)
- [Changelog](https://github.com/actions/checkout/blob/main/CHANGELOG.md)
- [Commits](https://github.com/actions/checkout/compare/v4...v6)

Updates `actions/setup-java` from 4 to 5
- [Release notes](https://github.com/actions/setup-java/releases)
- [Commits](https://github.com/actions/setup-java/compare/v4...v5)

Updates `gradle/actions` from 4 to 6
- [Release notes](https://github.com/gradle/actions/releases)
- [Commits](https://github.com/gradle/actions/compare/v4...v6)

---
updated-dependencies:
- dependency-name: actions/checkout
  dependency-version: '6'
  dependency-type: direct:production
  update-type: version-update:semver-major
  dependency-group: dependencies
- dependency-name: actions/setup-java
  dependency-version: '5'
  dependency-type: direct:production
  update-type: version-update:semver-major
  dependency-group: dependencies
- dependency-name: gradle/actions
  dependency-version: '6'
  dependency-type: direct:production
  update-type: version-update:semver-major
  dependency-group: dependencies
...

Signed-off-by: dependabot[bot] <support@github.com>
Co-authored-by: dependabot[bot] <49699333+dependabot[bot]@users.noreply.github.com>

* [Dependency Update]: Bump the dependencies group (#29)

Bumps the dependencies group in /mockguard with 11 updates:

| Package | From | To |
| --- | --- | --- |
| [org.junit.jupiter:junit-jupiter-api](https://github.com/junit-team/junit-framework) | `5.10.2` | `6.0.3` |
| [org.mockito:mockito-core](https://github.com/mockito/mockito) | `5.11.0` | `5.23.0` |
| [net.bytebuddy:byte-buddy](https://github.com/raphw/byte-buddy) | `1.14.18` | `1.18.8` |
| [net.bytebuddy:byte-buddy-agent](https://github.com/raphw/byte-buddy) | `1.14.18` | `1.18.8` |
| [org.junit.jupiter:junit-jupiter-engine](https://github.com/junit-team/junit-framework) | `5.10.2` | `6.0.3` |
| [org.junit.jupiter:junit-jupiter-params](https://github.com/junit-team/junit-framework) | `5.10.2` | `6.0.3` |
| [org.mockito:mockito-junit-jupiter](https://github.com/mockito/mockito) | `5.11.0` | `5.23.0` |
| [org.junit.platform:junit-platform-launcher](https://github.com/junit-team/junit-framework) | `1.10.2` | `6.0.3` |
| [jvm](https://github.com/JetBrains/kotlin) | `2.0.20` | `2.3.20` |
| org.gradle.toolchains.foojay-resolver-convention | `0.8.0` | `1.0.0` |
| [gradle-wrapper](https://github.com/gradle/gradle) | `8.5` | `9.4.1` |


Updates `org.junit.jupiter:junit-jupiter-api` from 5.10.2 to 6.0.3
- [Release notes](https://github.com/junit-team/junit-framework/releases)
- [Commits](https://github.com/junit-team/junit-framework/compare/r5.10.2...r6.0.3)

Updates `org.mockito:mockito-core` from 5.11.0 to 5.23.0
- [Release notes](https://github.com/mockito/mockito/releases)
- [Commits](https://github.com/mockito/mockito/compare/v5.11.0...v5.23.0)

Updates `net.bytebuddy:byte-buddy` from 1.14.18 to 1.18.8
- [Release notes](https://github.com/raphw/byte-buddy/releases)
- [Changelog](https://github.com/raphw/byte-buddy/blob/master/release-notes.md)
- [Commits](https://github.com/raphw/byte-buddy/compare/byte-buddy-1.14.18...byte-buddy-1.18.8)

Updates `net.bytebuddy:byte-buddy-agent` from 1.14.18 to 1.18.8
- [Release notes](https://github.com/raphw/byte-buddy/releases)
- [Changelog](https://github.com/raphw/byte-buddy/blob/master/release-notes.md)
- [Commits](https://github.com/raphw/byte-buddy/compare/byte-buddy-1.14.18...byte-buddy-1.18.8)

Updates `org.junit.jupiter:junit-jupiter-engine` from 5.10.2 to 6.0.3
- [Release notes](https://github.com/junit-team/junit-framework/releases)
- [Commits](https://github.com/junit-team/junit-framework/compare/r5.10.2...r6.0.3)

Updates `org.junit.jupiter:junit-jupiter-params` from 5.10.2 to 6.0.3
- [Release notes](https://github.com/junit-team/junit-framework/releases)
- [Commits](https://github.com/junit-team/junit-framework/compare/r5.10.2...r6.0.3)

Updates `org.mockito:mockito-junit-jupiter` from 5.11.0 to 5.23.0
- [Release notes](https://github.com/mockito/mockito/releases)
- [Commits](https://github.com/mockito/mockito/compare/v5.11.0...v5.23.0)

Updates `org.junit.platform:junit-platform-launcher` from 1.10.2 to 6.0.3
- [Release notes](https://github.com/junit-team/junit-framework/releases)
- [Commits](https://github.com/junit-team/junit-framework/commits/r6.0.3)

Updates `jvm` from 2.0.20 to 2.3.20
- [Release notes](https://github.com/JetBrains/kotlin/releases)
- [Changelog](https://github.com/JetBrains/kotlin/blob/master/ChangeLog.md)
- [Commits](https://github.com/JetBrains/kotlin/compare/v2.0.20...v2.3.20)

Updates `org.gradle.toolchains.foojay-resolver-convention` from 0.8.0 to 1.0.0

Updates `gradle-wrapper` from 8.5 to 9.4.1
- [Release notes](https://github.com/gradle/gradle/releases)
- [Commits](https://github.com/gradle/gradle/compare/v8.5.0...v9.4.1)

---
updated-dependencies:
- dependency-name: org.junit.jupiter:junit-jupiter-api
  dependency-version: 6.0.3
  dependency-type: direct:production
  update-type: version-update:semver-major
  dependency-group: dependencies
- dependency-name: org.mockito:mockito-core
  dependency-version: 5.23.0
  dependency-type: direct:production
  update-type: version-update:semver-minor
  dependency-group: dependencies
- dependency-name: net.bytebuddy:byte-buddy
  dependency-version: 1.18.8
  dependency-type: direct:production
  update-type: version-update:semver-minor
  dependency-group: dependencies
- dependency-name: net.bytebuddy:byte-buddy-agent
  dependency-version: 1.18.8
  dependency-type: direct:production
  update-type: version-update:semver-minor
  dependency-group: dependencies
- dependency-name: org.junit.jupiter:junit-jupiter-engine
  dependency-version: 6.0.3
  dependency-type: direct:production
  update-type: version-update:semver-major
  dependency-group: dependencies
- dependency-name: org.junit.jupiter:junit-jupiter-params
  dependency-version: 6.0.3
  dependency-type: direct:production
  update-type: version-update:semver-major
  dependency-group: dependencies
- dependency-name: org.mockito:mockito-junit-jupiter
  dependency-version: 5.23.0
  dependency-type: direct:production
  update-type: version-update:semver-minor
  dependency-group: dependencies
- dependency-name: org.junit.platform:junit-platform-launcher
  dependency-version: 6.0.3
  dependency-type: direct:production
  update-type: version-update:semver-major
  dependency-group: dependencies
- dependency-name: jvm
  dependency-version: 2.3.20
  dependency-type: direct:production
  update-type: version-update:semver-minor
  dependency-group: dependencies
- dependency-name: org.gradle.toolchains.foojay-resolver-convention
  dependency-version: 1.0.0
  dependency-type: direct:production
  update-type: version-update:semver-major
  dependency-group: dependencies
- dependency-name: gradle-wrapper
  dependency-version: 9.4.1
  dependency-type: direct:production
  update-type: version-update:semver-major
  dependency-group: dependencies
...

Signed-off-by: dependabot[bot] <support@github.com>
Co-authored-by: dependabot[bot] <49699333+dependabot[bot]@users.noreply.github.com>

* chore: Bump version from 0.1.0 to 0.1.1 (#31)

* Add release actions (#32)

* update workflows to publish

* create bump action

* add missing permissions

* remove duplicate emptyJavadocJar

* Make bump version workflow resilient to reruns (#34)

* Make bump version workflow resilient to reruns

* Create PR as draft

* Create PR from and to selected branch

* set new branch as chore/

---------

Signed-off-by: dependabot[bot] <support@github.com>
Co-authored-by: dependabot[bot] <49699333+dependabot[bot]@users.noreply.github.com>
- Update release title to use TAG variable
- Document scanner CLI usage
- Update CI and release workflows for root build
- Delete RELEASING.md
- Bump version to 0.2.0
- Strengthen scanner and publication checks
- Refresh project documentation
- Restore Java runtime documentation


### Removed
- Remove duplicate emptyJavadocJar

## [0.1.1] - 2026-04-25


### Added
- Add base logic idea


### Changed
- Initial commit
- Initialize project
- Define project
- Implement intercept for not accesible verify methods
- Refactor IT tests
- Create unit tests
- Improve readme
- Sync to main (#16)

* [iss-2] add github workspace

* [iss-2] improve gradle build

* [iss-2] prepare gradle build to publish

* [iss-2] fix gradle build to publish

* [iss-6] Ensure AfterEach is valid (#8)

* [iss-6] add after each tests

* [iss-6] add relevant methods tests

* [iss-7] add guard for mocks alone (#10)

* [iss-11] add CODEOWNERS (#12)

* [iss-4] Prepare gradle to publish to remote (#5)

* [iss-4] create action to publish

* [iss-4] improve action using PASSWORD instead of TOKEN secret

* Add PR build workflow for main and develop branches (#14)

Initial github workflow

* [iss-9] create protect branch workflow (#15)
- Sync to main (#19)

* [iss-2] add github workspace

* [iss-2] improve gradle build

* [iss-2] prepare gradle build to publish

* [iss-2] fix gradle build to publish

* [iss-6] Ensure AfterEach is valid (#8)

* [iss-6] add after each tests

* [iss-6] add relevant methods tests

* [iss-7] add guard for mocks alone (#10)

* [iss-11] add CODEOWNERS (#12)

* [iss-4] Prepare gradle to publish to remote (#5)

* [iss-4] create action to publish

* [iss-4] improve action using PASSWORD instead of TOKEN secret

* Add PR build workflow for main and develop branches (#14)

Initial github workflow

* [iss-9] create protect branch workflow (#15)

* [iss-17] fix publish workflow (#18)
- Sync to main (#33)

* [iss-2] add github workspace

* [iss-2] improve gradle build

* [iss-2] prepare gradle build to publish

* [iss-2] fix gradle build to publish

* [iss-6] Ensure AfterEach is valid (#8)

* [iss-6] add after each tests

* [iss-6] add relevant methods tests

* [iss-7] add guard for mocks alone (#10)

* [iss-11] add CODEOWNERS (#12)

* [iss-4] Prepare gradle to publish to remote (#5)

* [iss-4] create action to publish

* [iss-4] improve action using PASSWORD instead of TOKEN secret

* Add PR build workflow for main and develop branches (#14)

Initial github workflow

* [iss-9] create protect branch workflow (#15)

* [iss-17] fix publish workflow (#18)

* Sync to develop (#20)

* Sync to main (#16)

* [iss-2] add github workspace

* [iss-2] improve gradle build

* [iss-2] prepare gradle build to publish

* [iss-2] fix gradle build to publish

* [iss-6] Ensure AfterEach is valid (#8)

* [iss-6] add after each tests

* [iss-6] add relevant methods tests

* [iss-7] add guard for mocks alone (#10)

* [iss-11] add CODEOWNERS (#12)

* [iss-4] Prepare gradle to publish to remote (#5)

* [iss-4] create action to publish

* [iss-4] improve action using PASSWORD instead of TOKEN secret

* Add PR build workflow for main and develop branches (#14)

Initial github workflow

* [iss-9] create protect branch workflow (#15)

* Sync to main (#19)

* [iss-2] add github workspace

* [iss-2] improve gradle build

* [iss-2] prepare gradle build to publish

* [iss-2] fix gradle build to publish

* [iss-6] Ensure AfterEach is valid (#8)

* [iss-6] add after each tests

* [iss-6] add relevant methods tests

* [iss-7] add guard for mocks alone (#10)

* [iss-11] add CODEOWNERS (#12)

* [iss-4] Prepare gradle to publish to remote (#5)

* [iss-4] create action to publish

* [iss-4] improve action using PASSWORD instead of TOKEN secret

* Add PR build workflow for main and develop branches (#14)

Initial github workflow

* [iss-9] create protect branch workflow (#15)

* [iss-17] fix publish workflow (#18)

* [iss-21] Fix publish workflow (#22)

* [iss-21] fix release action

* [iss-21] improve release action

* [iss-21] fix build gradle

* [iss-21] use maven token

* [iss-21] update build retry data

* [docs] add badges (#23)

* Configure Dependabot for Gradle and GitHub Actions (#24)

* [fix] remove invalid property versioning-strategy (#25)

* Delete .idea directory

* Add dependency update group to Dependabot config

* [CI Update]: Bump the dependencies group with 3 updates (#30)

Bumps the dependencies group with 3 updates: [actions/checkout](https://github.com/actions/checkout), [actions/setup-java](https://github.com/actions/setup-java) and [gradle/actions](https://github.com/gradle/actions).


Updates `actions/checkout` from 4 to 6
- [Release notes](https://github.com/actions/checkout/releases)
- [Changelog](https://github.com/actions/checkout/blob/main/CHANGELOG.md)
- [Commits](https://github.com/actions/checkout/compare/v4...v6)

Updates `actions/setup-java` from 4 to 5
- [Release notes](https://github.com/actions/setup-java/releases)
- [Commits](https://github.com/actions/setup-java/compare/v4...v5)

Updates `gradle/actions` from 4 to 6
- [Release notes](https://github.com/gradle/actions/releases)
- [Commits](https://github.com/gradle/actions/compare/v4...v6)

---
updated-dependencies:
- dependency-name: actions/checkout
  dependency-version: '6'
  dependency-type: direct:production
  update-type: version-update:semver-major
  dependency-group: dependencies
- dependency-name: actions/setup-java
  dependency-version: '5'
  dependency-type: direct:production
  update-type: version-update:semver-major
  dependency-group: dependencies
- dependency-name: gradle/actions
  dependency-version: '6'
  dependency-type: direct:production
  update-type: version-update:semver-major
  dependency-group: dependencies
...

Signed-off-by: dependabot[bot] <support@github.com>
Co-authored-by: dependabot[bot] <49699333+dependabot[bot]@users.noreply.github.com>

* [Dependency Update]: Bump the dependencies group (#29)

Bumps the dependencies group in /mockguard with 11 updates:

| Package | From | To |
| --- | --- | --- |
| [org.junit.jupiter:junit-jupiter-api](https://github.com/junit-team/junit-framework) | `5.10.2` | `6.0.3` |
| [org.mockito:mockito-core](https://github.com/mockito/mockito) | `5.11.0` | `5.23.0` |
| [net.bytebuddy:byte-buddy](https://github.com/raphw/byte-buddy) | `1.14.18` | `1.18.8` |
| [net.bytebuddy:byte-buddy-agent](https://github.com/raphw/byte-buddy) | `1.14.18` | `1.18.8` |
| [org.junit.jupiter:junit-jupiter-engine](https://github.com/junit-team/junit-framework) | `5.10.2` | `6.0.3` |
| [org.junit.jupiter:junit-jupiter-params](https://github.com/junit-team/junit-framework) | `5.10.2` | `6.0.3` |
| [org.mockito:mockito-junit-jupiter](https://github.com/mockito/mockito) | `5.11.0` | `5.23.0` |
| [org.junit.platform:junit-platform-launcher](https://github.com/junit-team/junit-framework) | `1.10.2` | `6.0.3` |
| [jvm](https://github.com/JetBrains/kotlin) | `2.0.20` | `2.3.20` |
| org.gradle.toolchains.foojay-resolver-convention | `0.8.0` | `1.0.0` |
| [gradle-wrapper](https://github.com/gradle/gradle) | `8.5` | `9.4.1` |


Updates `org.junit.jupiter:junit-jupiter-api` from 5.10.2 to 6.0.3
- [Release notes](https://github.com/junit-team/junit-framework/releases)
- [Commits](https://github.com/junit-team/junit-framework/compare/r5.10.2...r6.0.3)

Updates `org.mockito:mockito-core` from 5.11.0 to 5.23.0
- [Release notes](https://github.com/mockito/mockito/releases)
- [Commits](https://github.com/mockito/mockito/compare/v5.11.0...v5.23.0)

Updates `net.bytebuddy:byte-buddy` from 1.14.18 to 1.18.8
- [Release notes](https://github.com/raphw/byte-buddy/releases)
- [Changelog](https://github.com/raphw/byte-buddy/blob/master/release-notes.md)
- [Commits](https://github.com/raphw/byte-buddy/compare/byte-buddy-1.14.18...byte-buddy-1.18.8)

Updates `net.bytebuddy:byte-buddy-agent` from 1.14.18 to 1.18.8
- [Release notes](https://github.com/raphw/byte-buddy/releases)
- [Changelog](https://github.com/raphw/byte-buddy/blob/master/release-notes.md)
- [Commits](https://github.com/raphw/byte-buddy/compare/byte-buddy-1.14.18...byte-buddy-1.18.8)

Updates `org.junit.jupiter:junit-jupiter-engine` from 5.10.2 to 6.0.3
- [Release notes](https://github.com/junit-team/junit-framework/releases)
- [Commits](https://github.com/junit-team/junit-framework/compare/r5.10.2...r6.0.3)

Updates `org.junit.jupiter:junit-jupiter-params` from 5.10.2 to 6.0.3
- [Release notes](https://github.com/junit-team/junit-framework/releases)
- [Commits](https://github.com/junit-team/junit-framework/compare/r5.10.2...r6.0.3)

Updates `org.mockito:mockito-junit-jupiter` from 5.11.0 to 5.23.0
- [Release notes](https://github.com/mockito/mockito/releases)
- [Commits](https://github.com/mockito/mockito/compare/v5.11.0...v5.23.0)

Updates `org.junit.platform:junit-platform-launcher` from 1.10.2 to 6.0.3
- [Release notes](https://github.com/junit-team/junit-framework/releases)
- [Commits](https://github.com/junit-team/junit-framework/commits/r6.0.3)

Updates `jvm` from 2.0.20 to 2.3.20
- [Release notes](https://github.com/JetBrains/kotlin/releases)
- [Changelog](https://github.com/JetBrains/kotlin/blob/master/ChangeLog.md)
- [Commits](https://github.com/JetBrains/kotlin/compare/v2.0.20...v2.3.20)

Updates `org.gradle.toolchains.foojay-resolver-convention` from 0.8.0 to 1.0.0

Updates `gradle-wrapper` from 8.5 to 9.4.1
- [Release notes](https://github.com/gradle/gradle/releases)
- [Commits](https://github.com/gradle/gradle/compare/v8.5.0...v9.4.1)

---
updated-dependencies:
- dependency-name: org.junit.jupiter:junit-jupiter-api
  dependency-version: 6.0.3
  dependency-type: direct:production
  update-type: version-update:semver-major
  dependency-group: dependencies
- dependency-name: org.mockito:mockito-core
  dependency-version: 5.23.0
  dependency-type: direct:production
  update-type: version-update:semver-minor
  dependency-group: dependencies
- dependency-name: net.bytebuddy:byte-buddy
  dependency-version: 1.18.8
  dependency-type: direct:production
  update-type: version-update:semver-minor
  dependency-group: dependencies
- dependency-name: net.bytebuddy:byte-buddy-agent
  dependency-version: 1.18.8
  dependency-type: direct:production
  update-type: version-update:semver-minor
  dependency-group: dependencies
- dependency-name: org.junit.jupiter:junit-jupiter-engine
  dependency-version: 6.0.3
  dependency-type: direct:production
  update-type: version-update:semver-major
  dependency-group: dependencies
- dependency-name: org.junit.jupiter:junit-jupiter-params
  dependency-version: 6.0.3
  dependency-type: direct:production
  update-type: version-update:semver-major
  dependency-group: dependencies
- dependency-name: org.mockito:mockito-junit-jupiter
  dependency-version: 5.23.0
  dependency-type: direct:production
  update-type: version-update:semver-minor
  dependency-group: dependencies
- dependency-name: org.junit.platform:junit-platform-launcher
  dependency-version: 6.0.3
  dependency-type: direct:production
  update-type: version-update:semver-major
  dependency-group: dependencies
- dependency-name: jvm
  dependency-version: 2.3.20
  dependency-type: direct:production
  update-type: version-update:semver-minor
  dependency-group: dependencies
- dependency-name: org.gradle.toolchains.foojay-resolver-convention
  dependency-version: 1.0.0
  dependency-type: direct:production
  update-type: version-update:semver-major
  dependency-group: dependencies
- dependency-name: gradle-wrapper
  dependency-version: 9.4.1
  dependency-type: direct:production
  update-type: version-update:semver-major
  dependency-group: dependencies
...

Signed-off-by: dependabot[bot] <support@github.com>
Co-authored-by: dependabot[bot] <49699333+dependabot[bot]@users.noreply.github.com>

* chore: Bump version from 0.1.0 to 0.1.1 (#31)

* Add release actions (#32)

* update workflows to publish

* create bump action

* add missing permissions

* remove duplicate emptyJavadocJar

---------

Signed-off-by: dependabot[bot] <support@github.com>
Co-authored-by: dependabot[bot] <49699333+dependabot[bot]@users.noreply.github.com>
