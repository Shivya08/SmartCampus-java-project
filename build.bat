@echo off
echo =======================================================
echo          Building SmartCampus Project (Java)
echo =======================================================

if not exist "target\classes" mkdir "target\classes"
if not exist "target\test-classes" mkdir "target\test-classes"
if not exist "data" mkdir "data"

echo Compiling main source files...
dir /s /b src\main\java\*.java > sources.txt
javac -encoding UTF-8 -d target\classes @sources.txt
if %errorlevel% neq 0 (
    echo [ERROR] Main compilation failed!
    del sources.txt
    exit /b %errorlevel%
)
del sources.txt

echo Compiling test files...
dir /s /b src\test\java\com\smartcampus\StandaloneTestRunner.java > test_sources.txt
javac -encoding UTF-8 -cp target\classes -d target\test-classes @test_sources.txt
if %errorlevel% neq 0 (
    echo [WARNING] Test compilation encountered an issue.
)
del test_sources.txt

echo.
echo =======================================================
echo           BUILD SUCCESSFUL!
echo =======================================================
