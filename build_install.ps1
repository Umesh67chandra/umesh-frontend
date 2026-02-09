param(
    [string]$ProjectRoot = "C:\Users\heman\Desktop\FocusGuardian\focusguardian3",
    [string]$ApkPath = "C:\Users\heman\Desktop\FocusGuardian\focusguardian3\app\build\outputs\apk\debug\app-debug.apk",
    [string]$SdkDir = "C:\Users\heman\AppData\Local\Android\Sdk"
)

$ErrorActionPreference = "Stop"

Write-Host "[1/4] Building app..."
Set-Location $ProjectRoot
& .\gradlew.bat build

Write-Host "[2/4] Checking APK..."
if (-not (Test-Path $ApkPath)) {
    throw "APK not found: $ApkPath"
}

Write-Host "[3/4] Installing APK..."
$adb = Join-Path $SdkDir "platform-tools\adb.exe"
if (-not (Test-Path $adb)) {
    throw "adb not found: $adb"
}
& $adb install -r $ApkPath

Write-Host "[4/4] Done. Launch the app on your device."