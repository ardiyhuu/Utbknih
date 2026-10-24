#!/usr/bin/env bash
set -e
mkdir -p www && cp index.html www/index.html
npm install
npx cap add android
D=android/app/src/main/java/id/snbtku/app
cp WpsOpenerPlugin.java MainActivity.java "$D/"
mkdir -p android/app/src/main/res/xml
cp file_paths.xml android/app/src/main/res/xml/file_paths.xml
M=android/app/src/main/AndroidManifest.xml
if ! grep -q "fileprovider" "$M"; then
  sed -i 's#</application>#<provider android:name="androidx.core.content.FileProvider" android:authorities="${applicationId}.fileprovider" android:exported="false" android:grantUriPermissions="true"><meta-data android:name="android.support.FILE_PROVIDER_PATHS" android:resource="@xml/file_paths"/></provider></application>#' "$M"
fi
npx cap sync android
cd android
chmod +x gradlew
./gradlew assembleDebug
