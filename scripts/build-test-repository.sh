#!/usr/bin/env bash
set -euo pipefail

# Run from repo root
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

# Build + publish to the local "test" repository without signing
./gradlew \
  -Preleasing=true \
  -Pversion=9.9.9-test \
  -x signMavenJavaPublication \
  -x sign \
  publishAllPublicationsToTestRepository

# If you only want a single project (e.g. :bugsnag), use this instead:
# ./gradlew -Preleasing=true -Pversion=9.9.9-test -x signMavenJavaPublication -x sign :bugsnag:publishMavenJavaPublicationToTestRepository

# Zip the generated repository
REPO_DIR="$ROOT_DIR/build/repository"
mkdir -p "$REPO_DIR"
cd "$REPO_DIR"
zip -r ../maven-repository.zip ./*