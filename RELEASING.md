# Releasing MockGuard

MockGuard publishes two independent products.

| Product | Version source | Tag | Maven coordinate | Gate |
|---|---|---|---|---|
| Runtime | `mockguard/build.gradle.kts` | `mockguard-vX.Y.Z` | `io.github.jfrz38:mockguard` | `mockguardCheck` |
| Scanner | `mockguard-scanner/build.gradle.kts` | `scanner-vX.Y.Z` | `io.github.jfrz38:mockguard-scanner` | `scannerCheck` |

Do not synchronize versions unless both products independently need a release.

## Prepare A Version

1. Run the **Bump Version** workflow.
2. Select `mockguard` or `scanner`, the semantic-version component, and the base branch.
3. Review and merge the generated pull request through the normal build gate.
4. Merge the release change to `main`.

A version change on `main` triggers **Create Product Releases**. It validates that the new version is higher, runs the selected product gate, creates the namespaced tag, and creates a GitHub release. Scanner releases also attach the CLI JAR, ZIP, TAR, and `SHA256SUMS`.

If release creation fails before a release exists, fix the cause and rerun the workflow for the same `main` commit. The workflow treats an existing release as complete and does not create it twice.

## Publish To Maven Central

After the GitHub release exists:

1. Run **Publish Product Release** from `main`.
2. Select the product and enter its exact namespaced tag.
3. Confirm the workflow resolves the expected module version and runs the product gate.
4. Confirm the JReleaser dry run succeeds.
5. Confirm the deployment reaches Maven Central, then verify the coordinate and version in the Central Portal.

The workflow checks that the tag belongs to `main`, matches the module version, has an existing GitHub release, is higher than the published version, and has not already been published.

Required repository secrets:

- `JRELEASER_GPG_PUBLIC_KEY`
- `JRELEASER_GPG_SECRET_KEY`
- `JRELEASER_GPG_PASSPHRASE`
- `JRELEASER_MAVENCENTRAL_TOKEN`

## Local Validation

Use the root Makefile:

```bash
make publish-mockguard-staging
make publish-mockguard-dryrun
make publish-scanner-staging
make publish-scanner-dryrun
```

Staged Maven repositories are written under the selected module's `build/staging-deploy`. Publication smoke tests in `consumerCheck` compile and execute consumers against these staged artifacts.

## Failure Recovery

Before retrying a failed publish, inspect both the workflow log and the deployment in the Maven Central Portal.

- If validation or the dry run failed before upload, fix the cause and dispatch the same product/tag again.
- If upload failed and Central has no deployment, retry the same workflow after correcting credentials or transient service issues.
- If Central has a pending deployment, resolve or publish that deployment in the portal instead of uploading a duplicate bundle.
- If the version is visible in Maven Central, publication is complete even if a later workflow step reported failure. Do not retry or reuse that version.
- Maven releases are immutable. Any artifact defect after publication requires a new product version and tag.

Snapshot and direct local publication targets exist for maintainer validation, but a release must use the tagged **Publish Product Release** workflow so version, ancestry, gate, signing, and duplicate checks remain enforced.
