#!/usr/bin/env bash
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

./gradlew -Pversion=9.9.9-test publishToTestRepoAll

REPO_DIR="$ROOT_DIR/build/repository"
mkdir -p "$REPO_DIR"
cd "$REPO_DIR"
zip -r "$ROOT_DIR/maven-repository.zip" ./*