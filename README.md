# Colls

Small, dependency-free collection utilities for Java 25. Currently provides a generic, fixed-size, overwrite-on-full `RingBuffer<T>` with queue semantics.

## Build and test locally
```bash
mvn verify
```
This compiles, runs tests, and produces jars (main, sources, javadocs) under `target/`.

## CI (GitHub Actions)
- Workflow: `.github/workflows/java-ci.yml`
- Runs on pushes and PRs; uses Temurin JDK 25 and caches the Maven repo.
- Versioning:
  - Tag push (`v*`): uses the tag (without `v`) and strips `-SNAPSHOT` for release builds.
  - Branch/PR: enforces `-SNAPSHOT`.
- Pipeline: applies the computed version via `mvn versions:set`, runs `mvn verify`, and uploads jars (main, sources, javadocs) named with that version.
- Deploy:
  - Set secrets: `MAVEN_USERNAME`, `MAVEN_PASSWORD`, `MAVEN_RELEASES_URL`, `MAVEN_SNAPSHOTS_URL` (adjust `MAVEN_SERVER_ID` if needed).
  - Pushes to `main` deploy snapshots to `MAVEN_SNAPSHOTS_URL`.
  - Tag pushes (`v*`) deploy releases to `MAVEN_RELEASES_URL` with `-SNAPSHOT` removed.
  - After a tag release, the workflow fetches `main`, bumps to the next `-SNAPSHOT`, commits `chore: start <next>`, and pushes to `main`—ensure branch protection allows GitHub Actions to push.
