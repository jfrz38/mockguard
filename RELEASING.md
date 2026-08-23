# Releasing MockGuard

The repository contains two independently versioned products:

| Product | Version file | Git tag | Maven coordinate | GitHub assets |
|---------|--------------|---------|------------------|---------------|
| MockGuard runtime | `mockguard/build.gradle.kts` | `mockguard-vX.Y.Z` | `io.github.jfrz38:mockguard` | None |
| Scanner CLI | `mockguard-scanner/build.gradle.kts` | `scanner-vX.Y.Z` | `io.github.jfrz38:mockguard-scanner` | CLI JAR, ZIP, TAR, checksums |

`mockguard-consumer-tests` is verification infrastructure and is never published. Product versions do not need to match.

## Create A Version Bump

Run the **Bump Version** workflow from the Actions page:

1. Select `mockguard` or `scanner`.
2. Select the semantic version component to increment.
3. Leave `develop` as the base branch unless preparing a release from another integration branch intentionally.
4. Review and merge the generated pull request.

The workflow changes exactly one product version file. A scanner release must use a version greater than `0.1.0`; that version was distributed as part of the legacy combined release and must not be reused.

## Create A GitHub Release

Merging the integration branch into `main` triggers **Create Product Releases**. For each product, the workflow compares its version file with the previous `main` commit. Only a changed product can produce a release.

The workflow also requires the new version to be higher than the latest tag for that product. The existing `v0.1.1` tag remains the historical baseline for MockGuard and is not renamed or moved.

Runtime releases use `mockguard-vX.Y.Z` and contain release notes only. Scanner releases use `scanner-vX.Y.Z` and contain:

- `mockguard-scanner-X.Y.Z-cli.jar`
- `mockguard-scanner-X.Y.Z.zip`
- `mockguard-scanner-X.Y.Z.tar`
- `SHA256SUMS`

Before creating a tag, the workflow runs `mockguardCheck` or `scannerCheck`. Scanner releases additionally smoke-test the executable JAR and installed Unix script.

## Publish To Maven Central

GitHub release creation and Maven publication are separate operations. After validating a release, run **Publish Product Release** from `main` and provide:

1. The product (`mockguard` or `scanner`).
2. Its exact namespaced release tag.

The workflow checks out that tag and verifies all of the following before deployment:

- The tag prefix matches the selected product.
- A GitHub release exists for the tag.
- The tag is reachable from `main`.
- The module version equals the tag version.
- The coordinate/version is not already present on Maven Central.
- The version is higher than the latest published version, when one exists.
- The selected product's verification gate passes.

Only the selected module is staged and deployed. JReleaser owns artifact signing, checksums, Maven Central rule validation, and deployment.

The scanner Maven publication is the thin JVM artifact for Gradle, Maven, and `JavaExec` consumers. The executable fat JAR and application archives are distributed only through the scanner GitHub release.

## Local Verification

Use the product-qualified targets to inspect a publication without affecting the other module:

```bash
make publish-mockguard-staging
make publish-scanner-staging
```

The repositories are written under each module's `build/staging-deploy` directory. Validate JReleaser configuration with:

```bash
make publish-mockguard-dryrun
make publish-scanner-dryrun
```

Publishing to Maven Central requires the JReleaser GPG and Maven Central environment variables used by `.github/workflows/publish.yml`.
