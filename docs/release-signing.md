# Release Signing

Use a private Android signing key before publishing LMSMOB Chat widely.

The current public GitHub release uses a debug APK. Debug APKs are useful for
testing, but a signed release APK is better for public distribution because
users can update from one version to the next only when future builds use the
same signing key.

## 1. Create A Keystore

Run this once and keep the generated file backed up somewhere private:

```powershell
& "C:\Program Files\Android\Android Studio\jbr\bin\keytool.exe" -genkeypair `
  -v `
  -keystore mindylab-lmsmob-chat.jks `
  -keyalg RSA `
  -keysize 2048 `
  -validity 10000 `
  -alias lmsmob-chat
```

Do not commit the `.jks` file.

## 2. Create keystore.properties

Create `keystore.properties` in the repository root:

```properties
storeFile=mindylab-lmsmob-chat.jks
storePassword=YOUR_STORE_PASSWORD
keyAlias=lmsmob-chat
keyPassword=YOUR_KEY_PASSWORD
```

`keystore.properties`, `.jks`, `.keystore`, and `.p12` files are ignored by
git.

## 3. Build A Signed Release APK

```powershell
$env:JAVA_HOME="C:\Program Files\Android\Android Studio\jbr"
.\gradlew.bat assembleRelease
```

The signed APK is generated under:

```text
app/build/outputs/apk/release/
```

## 4. Verify Signing

```powershell
& "$env:LOCALAPPDATA\Android\Sdk\build-tools\37.0.0\apksigner.bat" verify --verbose `
  app\build\outputs\apk\release\app-release.apk
```

If the output file is still named `app-release-unsigned.apk`, Gradle did not
find `keystore.properties` or one of the required properties is missing.
