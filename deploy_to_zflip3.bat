@echo off
set DEVICE_ID=RRCW502LCWJ
echo ========================================================
echo  Deploy Langsung ke Samsung Galaxy Z Flip 3 (%DEVICE_ID%)
echo ========================================================
call gradlew.bat assembleDebug
if %ERRORLEVEL% equ 0 (
    echo Menginstal ke Samsung Galaxy Z Flip 3...
    adb -s %DEVICE_ID% install -r -t -d "app\build\outputs\apk\debug\app-debug.apk"
    adb -s %DEVICE_ID% shell am start --user 0 -n com.bca.mobile/.MainActivity
    echo [SUKSES] Aplikasi aktif di Samsung Galaxy Z Flip 3!
) else (
    echo [GAGAL] Kompilasi gagal!
)
