@echo off
cd /d "%~dp0"
echo ==============================================
echo Running Unit and Integration Tests...
echo ==============================================

:: Try to find Java
if exist "C:\Program Files\Java\jdk1.8.0_202" (
    set "JAVA_HOME=C:\Program Files\Java\jdk1.8.0_202"
) else if exist "C:\Program Files\Java\jre1.8.0_202" (
    set "JAVA_HOME=C:\Program Files\Java\jre1.8.0_202"
)

if defined JAVA_HOME (
    echo Setting JAVA_HOME to %JAVA_HOME%
    set "PATH=%JAVA_HOME%\bin;%PATH%"
) else (
    echo WARNING: Could not find JDK 1.8.0_202. Using system path java...
)

echo Java Version:
java -version
echo.

if not exist "apache-maven-3.9.6\bin\mvn.cmd" (
    echo ERROR: Could not find apache-maven-3.9.6\bin\mvn.cmd
    echo Please verify the folder exists.
    pause
    exit /b
)

call "apache-maven-3.9.6\bin\mvn.cmd" test "-Dtest=MainTest,DebugTest,Test_Settings_Main,TestDriver_PanelPortfolio,MainMockitoTest"

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo TESTS FAILED! Check output above.
) else (
    echo.
    echo TESTS PASSED!
)

echo.
echo Press any key to close...
pause
