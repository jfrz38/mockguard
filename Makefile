GRADLE ?= ./gradlew
MOCKGUARD_VERSION = $(shell $(GRADLE) -q :mockguard:properties 2>/dev/null | awk -F': ' '$$1 == "version" {print $$2; exit}')
SCANNER_VERSION  = $(shell $(GRADLE) -q :mockguard-scanner:properties 2>/dev/null | awk -F': ' '$$1 == "version" {print $$2; exit}')
SCANNER_JAR     ?= mockguard-scanner/build/libs/mockguard-scanner-$(SCANNER_VERSION)-cli.jar

.PHONY: help

help: ## show make targets
	@awk 'BEGIN {FS = ":.*?## "} /^[a-zA-Z_-]+:.*?## / {sub("\\\\n",sprintf("\n%22c"," "), $$2);printf " \033[36m%-20s\033[0m  %s\n", $$1, $$2}' $(MAKEFILE_LIST)

.PHONY: clean build

clean: ## clean all build outputs
	$(GRADLE) clean

build: ## compile and run all checks
	$(GRADLE) build

# Versions
.PHONY: version scanner-version

version: ## print mockguard version (like workflow: VERSION="$(make version)")
	@echo "$(MOCKGUARD_VERSION)"

scanner-version: ## print mockguard-scanner version
	@echo "$(SCANNER_VERSION)"


# Tests
.PHONY: test test-integration test-runtime-integration test-scanner-integration test-consumer

test: ## run all tests
	$(GRADLE) test

test-integration: ## run all integration tests across all modules
	$(GRADLE) test --tests "*IntegrationTest*" --tests "*ConsumerIntegrationTest*"

test-runtime-integration: ## run mockguard runtime integration tests only
	$(GRADLE) :mockguard:test --tests "*IntegrationTest*"

test-scanner-integration: ## run mockguard-scanner integration tests only
	$(GRADLE) :mockguard-scanner:test --tests "*IntegrationTest*"

test-consumer: ## run consumer tests only
	$(GRADLE) :mockguard-consumer-tests:test

# Scanner
.PHONY: scanner-jar scan

scanner-jar: ## build the scanner fat JAR
	$(GRADLE) :mockguard-scanner:fatJar

scan: scanner-jar ## build scanner JAR and scan mockguard's compiled test classes
	$(GRADLE) :mockguard:compileTestKotlin
	java -jar $(SCANNER_JAR) --class-dir=mockguard/build/classes/kotlin/test --format=console

# Publishing
.PHONY: publish-local publish-mockguard-local publish-scanner-local
.PHONY: publish-mockguard-staging publish-mockguard-dryrun publish-mockguard
.PHONY: publish-scanner-staging publish-scanner-dryrun publish-scanner

publish-local: publish-mockguard-local publish-scanner-local ## publish both products to local ~/.m2

publish-mockguard-local: ## publish only mockguard to local ~/.m2
	$(GRADLE) :mockguard:publishToMavenLocal

publish-scanner-local: ## publish only mockguard-scanner to local ~/.m2
	$(GRADLE) :mockguard-scanner:publishToMavenLocal

publish-mockguard-staging: ## stage only mockguard for local inspection
	$(GRADLE) :mockguard:publishAllPublicationsToStagingRepository

publish-mockguard-dryrun: ## validate mockguard JReleaser deployment
	$(GRADLE) --stacktrace :mockguard:jreleaserConfig --deploy --dryrun

publish-mockguard: ## publish only mockguard to Maven Central
	$(GRADLE) --stacktrace --info :mockguard:publishAllPublicationsToStagingRepository :mockguard:jreleaserDeploy

publish-scanner-staging: ## stage only mockguard-scanner for local inspection
	$(GRADLE) :mockguard-scanner:publishAllPublicationsToStagingRepository

publish-scanner-dryrun: ## validate mockguard-scanner JReleaser deployment
	$(GRADLE) --stacktrace :mockguard-scanner:jreleaserConfig --deploy --dryrun

publish-scanner: ## publish only mockguard-scanner to Maven Central
	$(GRADLE) --stacktrace --info :mockguard-scanner:publishAllPublicationsToStagingRepository :mockguard-scanner:jreleaserDeploy
