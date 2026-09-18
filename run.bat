@echo off
echo =======================================================
echo          Launching SmartCampus Application
echo =======================================================

if not exist "target\classes\com\smartcampus\Main.class" (
    echo Binaries not found. Building project first...
    call build.bat
    if %errorlevel% neq 0 exit /b %errorlevel%
)

if "%1"=="--test" (
    echo Running standalone test validation suite...
    java -cp "target\classes;target\test-classes" com.smartcampus.StandaloneTestRunner
) else if "%1"=="--demo" (
    echo Running automated end-to-end demonstration...
    java -cp "target\classes" com.smartcampus.Main --demo
) else (
    java -cp "target\classes" com.smartcampus.Main
)
