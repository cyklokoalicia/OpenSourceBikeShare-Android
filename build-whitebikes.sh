#!/usr/bin/env bash
# Build Android app for https://whitebikes.info/
# Uses ANDROID_HOME if set, otherwise $HOME/android-sdk.

set -e
cd "$(dirname "$0")"
export ANDROID_HOME="${ANDROID_HOME:-$HOME/android-sdk}"

./gradlew assembleDebug \
  -PAPP_NAME="WhiteBikes" \
  -PAPI_BASE_URL="https://whitebikes.info/api/v1/" \
  -PLOGO_URL="https://whitebikes.info/images/logo_small.svg"

echo ""
echo "APK: app/build/outputs/apk/debug/app-debug.apk"
