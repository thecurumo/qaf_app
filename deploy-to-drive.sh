#!/bin/bash
set -e

cd /workspaces/qaf_app/capacitor-app/android

echo "=========================================="
echo "مرحله ۱: همگام‌سازی فایل‌های وب با اندروید"
echo "=========================================="
cd /workspaces/qaf_app/capacitor-app
npx cap sync android

echo ""
echo "=========================================="
echo "مرحله ۲: بیلد اپلیکیشن"
echo "=========================================="
cd android
./gradlew assembleDebug

APK_PATH="app/build/outputs/apk/debug/app-debug.apk"

if [ ! -f "$APK_PATH" ]; then
    echo "خطا: فایل APK ساخته نشد!"
    exit 1
fi

echo ""
echo "=========================================="
echo "مرحله ۳: آپلود به گوگل درایو"
echo "=========================================="

TIMESTAMP=$(date +"%Y-%m-%d_%H-%M")
DRIVE_FOLDER="qaf_builds"
FILENAME="qaf-debug-${TIMESTAMP}.apk"

rclone mkdir "gdrive:${DRIVE_FOLDER}" 2>/dev/null || true

rclone copy "$APK_PATH" "gdrive:${DRIVE_FOLDER}/" --progress

rclone moveto "gdrive:${DRIVE_FOLDER}/app-debug.apk" "gdrive:${DRIVE_FOLDER}/${FILENAME}"

echo ""
echo "=========================================="
echo "آپلود با موفقیت انجام شد!"
echo "فایل در گوگل درایو، پوشه: ${DRIVE_FOLDER}"
echo "نام فایل: ${FILENAME}"
echo "=========================================="
