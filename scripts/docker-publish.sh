#!/usr/bin/env bash

set -euo pipefail

# === Variables ===
GRADLE_DIR="$HOME/.gradle"
KEY_FILE="$HOME/temp_key"
KEY_RING="/publishKey.gpg"
GRADLE_PROPERTIES="$GRADLE_DIR/gradle.properties"

# === GPG Key Setup ===
echo "$KEY" > "$KEY_FILE"
base64 --decode "$KEY_FILE" > "$KEY_RING"

# === Gradle Configuration ===
mkdir -p "$GRADLE_DIR"
{
  echo "signing.keyId=$KEY_ID"
  echo "signing.password=$KEY_PASS"
  echo "signing.secretKeyRingFile=$KEY_RING"
  echo "NEXUS_USERNAME=$PUBLISH_USER"
  echo "NEXUS_PASSWORD=$PUBLISH_PASS"
  echo "nexusUsername=$PUBLISH_USER"
  echo "nexusPassword=$PUBLISH_PASS"
} >> "$GRADLE_PROPERTIES"

# === Build and Publish ===
/app/gradlew -Preleasing=true clean publishMavenJavaPublicationToOssrhStagingRepository

# === Close Staging Repository ===
echo "--- Closing staging repository"
echo "Fetching staging repositories..."

REPOS_JSON=$(curl -s -u "$PUBLISH_USER:$PUBLISH_PASS" \
  "https://ossrh-staging-api.central.sonatype.com/manual/search/repositories")

if [[ -z "$REPOS_JSON" ]]; then
  echo "Failed to retrieve repository list. Check your credentials or network." >&2
  exit 1
fi

REPO_KEYS=($(echo "$REPOS_JSON" | jq -r '.repositories[] | select(.state == "open") | .key'))

if [[ "${#REPO_KEYS[@]}" -eq 0 ]]; then
  echo "No open repositories found."
  exit 1
elif [[ "${#REPO_KEYS[@]}" -gt 1 ]]; then
  echo "Multiple open repositories found. Please specify which one to close:"
  printf '%s\n' "${REPO_KEYS[@]}"
  exit 1
fi

REPO_KEY="${REPO_KEYS[1]}"
echo "Closing repository $REPO_KEY..."

URL="https://ossrh-staging-api.central.sonatype.com/manual/upload/repository/$REPO_KEY?publishing_type=user_managed"
RESPONSE=$(curl -s -w "\nHTTP_STATUS:%{http_code}" -X POST -u "$PUBLISH_USER:$PUBLISH_PASS" "$URL")

BODY=$(echo "$RESPONSE" | sed -n '/^HTTP_STATUS:/!p')
STATUS=$(echo "$RESPONSE" | sed -n 's/^HTTP_STATUS://p')

if [[ "$STATUS" != "200" ]]; then
  echo "Failed to close repository. HTTP Status: $STATUS"
  echo "$BODY" | jq -r
  exit 1
fi

echo "Repository $REPO_KEY closed successfully."

echo "Go to https://central.sonatype.com/publishing to release the final artefact."
echo "For full release instructions, visit:"
echo "https://github.com/bugsnag/bugsnag-java/blob/next/CONTRIBUTING.md#making-a-release"
