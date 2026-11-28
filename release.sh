#!/usr/bin/env bash
# Qaf release script: bump version -> sync -> build signed APK -> verify -> publish.
#
# Usage:
#   ./release.sh 1.2.0 "Release notes text"
#
# You only choose the versionName (the number users see). The versionCode
# (the number Android uses to order versions) is increased automatically.

set -euo pipefail

VERSION_NAME="${1:-}"
NOTES="${2:-}"

ROOT="${QAF_ROOT:-/workspaces/qaf_app}"
APP="$ROOT/capacitor-app"
GRADLE_FILE="$APP/android/app/build.gradle"
APK="$APP/android/app/build/outputs/apk/release/app-release.apk"
BT="${QAF_BUILD_TOOLS:-/home/codespace/android-sdk/build-tools/35.0.0}"
BUILD_LOG="/tmp/qaf-build.log"

# Signing certificate of every published version. If a build is signed with
# anything else, people who installed an earlier version cannot update.
EXPECTED_SHA="b3312ba1f51106da24b5c7efbfd24680d79a737fe1304d10d729438bb4d55922"

# The only permissions Qaf is allowed to ship with.
ALLOWED_PERMS=(
  "android.permission.INTERNET"
  "com.thecurumo.qaf.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION"
)

step() { echo; echo "==> $1"; }
fail() { echo; echo "STOPPED: $1"; exit 1; }

if [[ -z "$VERSION_NAME" ]]; then
  echo "Usage: ./release.sh <versionName> [\"release notes\"]"
  echo "Example: ./release.sh 1.2.0 \"Added Dehkhoda dictionary and typography settings\""
  exit 1
fi

[[ "$VERSION_NAME" =~ ^[0-9]+\.[0-9]+\.[0-9]+$ ]] || fail "version must look like 1.2.0 (three numbers separated by dots), got: $VERSION_NAME"
[[ -n "$NOTES" ]] || NOTES="Qaf $VERSION_NAME"

TAG="v$VERSION_NAME"
TMP_APK="/tmp/qaf-$TAG.apk"
trap 'rm -f "$TMP_APK"' EXIT

# ---------------------------------------------------------------- checks
step "Pre-flight checks"
command -v git >/dev/null || fail "git is not installed"
command -v gh >/dev/null || fail "GitHub CLI (gh) is not installed"
git -C "$ROOT" rev-parse --is-inside-work-tree >/dev/null 2>&1 || fail "$ROOT is not a git repository"
[[ -f "$APP/android/key.properties" ]] || fail "android/key.properties is missing, so the APK could not be signed"
[[ -f "$GRADLE_FILE" ]] || fail "build.gradle not found at $GRADLE_FILE"
[[ -x "$BT/apksigner" && -x "$BT/aapt2" ]] || fail "Android build-tools not found at $BT"

if git -C "$ROOT" rev-parse -q --verify "refs/tags/$TAG" >/dev/null 2>&1; then
  fail "tag $TAG already exists locally. Choose a new version number."
fi
if git -C "$ROOT" ls-remote --exit-code --tags origin "refs/tags/$TAG" >/dev/null 2>&1; then
  fail "tag $TAG already exists on GitHub. Choose a new version number."
fi

CURRENT_CODE="$(grep -oE 'versionCode [0-9]+' "$GRADLE_FILE" | grep -oE '[0-9]+' || true)"
CURRENT_NAME="$(grep -oE 'versionName "[^"]*"' "$GRADLE_FILE" | grep -oE '"[^"]*"' | tr -d '"' || true)"
[[ -n "$CURRENT_CODE" && -n "$CURRENT_NAME" ]] || fail "could not read versionCode/versionName from build.gradle"

if [[ "$CURRENT_NAME" == "$VERSION_NAME" ]]; then
  # Same version name as build.gradle, and the tag does not exist yet:
  # this is a retry of a build that was never published.
  NEW_CODE="$CURRENT_CODE"
  echo "build.gradle already says $VERSION_NAME and it was never published."
  echo "Treating this as a retry: keeping versionCode $NEW_CODE."
else
  NEW_CODE=$((CURRENT_CODE + 1))
  echo "Current version: $CURRENT_NAME (code $CURRENT_CODE)"
  echo "New version:     $VERSION_NAME (code $NEW_CODE)"
fi

# ---------------------------------------------------------------- version
step "Setting version in build.gradle"
sed -i -E "s/versionCode [0-9]+/versionCode $NEW_CODE/; s/versionName \"[^\"]*\"/versionName \"$VERSION_NAME\"/" "$GRADLE_FILE"
grep -n "versionCode\|versionName" "$GRADLE_FILE"

# ---------------------------------------------------------------- build
step "Syncing web files into the Android project"
cd "$APP"
[[ -d node_modules ]] || npm install
npx cap sync android

step "Building signed release APK (takes a few minutes)"
rm -f "$APK"
cd "$APP/android"
if ! ./gradlew assembleRelease --console=plain >"$BUILD_LOG" 2>&1; then
  tail -40 "$BUILD_LOG"
  fail "Gradle build failed. Full log: $BUILD_LOG"
fi
tail -3 "$BUILD_LOG"
[[ -f "$APK" ]] || fail "Gradle finished but no APK was produced"

# ---------------------------------------------------------------- verify
step "Verifying the APK"

BADGING_ALL="$("$BT/aapt2" dump badging "$APK")"
BADGING="$(grep -m1 '^package:' <<<"$BADGING_ALL" || true)"
echo "$BADGING"
[[ "$BADGING" == *"versionCode='$NEW_CODE'"* ]] || fail "versionCode inside the APK is not $NEW_CODE"
[[ "$BADGING" == *"versionName='$VERSION_NAME'"* ]] || fail "versionName inside the APK is not $VERSION_NAME"

echo
echo "Permissions in the APK:"
PERMS="$("$BT/aapt2" dump permissions "$APK" | grep '^uses-permission' | sed -E "s/.*name='([^']+)'.*/\1/" || true)"
echo "$PERMS" | sed 's/^/  - /'
for p in $PERMS; do
  ok=0
  for a in "${ALLOWED_PERMS[@]}"; do
    if [[ "$p" == "$a" ]]; then ok=1; fi
  done
  [[ $ok -eq 1 ]] || fail "unexpected permission in the APK: $p. Nothing was published. Find which plugin adds it and remove it in AndroidManifest.xml with tools:node=\"remove\"."
done

CERTS="$("$BT/apksigner" verify --print-certs "$APK")"
ACTUAL_SHA="$(awk '/certificate SHA-256 digest/ {print $NF; exit}' <<<"$CERTS")"
echo
echo "Signing certificate: $ACTUAL_SHA"
[[ "$ACTUAL_SHA" == "$EXPECTED_SHA" ]] || fail "the APK is signed with a different key than earlier releases, so users could not update. Nothing was published."

echo
echo "APK size: $(du -h "$APK" | cut -f1)"
echo "APK path: $APK"

# ---------------------------------------------------------------- confirm
echo
echo "All checks passed."
echo "To test this exact APK on your phone first, answer n, download the file"
echo "above from the Codespace file explorer, and run this script again later"
echo "with the SAME version number."
read -r -p "Publish $TAG to GitHub now? (y/N) " ANSWER
if [[ "$ANSWER" != "y" && "$ANSWER" != "Y" ]]; then
  echo "Not published. build.gradle keeps version $VERSION_NAME."
  exit 0
fi

# ---------------------------------------------------------------- publish
step "Committing and pushing"
cd "$ROOT"
git add -A
if git diff --cached --quiet; then
  echo "Nothing new to commit."
else
  git commit -q -m "Release $TAG" -m "$NOTES"
fi
git pull --rebase || fail "git pull --rebase failed (conflict?). Fix it, then run this script again with the same version number."
git push || fail "git push failed. Run this script again with the same version number."

step "Creating GitHub release $TAG"
cp "$APK" "$TMP_APK"
gh release create "$TAG" "$TMP_APK" \
  --title "Qaf $VERSION_NAME" \
  --notes "$NOTES" \
  --target "$(git rev-parse HEAD)" || fail "creating the GitHub release failed. Run this script again with the same version number."

echo
echo "Done. Release page:"
gh release view "$TAG" --json url --jq .url
echo
echo "File to upload to Myket:"
echo "$APK"
