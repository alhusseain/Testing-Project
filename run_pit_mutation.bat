@echo off
cd /d "%~dp0"
echo ==============================================
echo Running PIT Mutation Testing (Aggressive Fix)...
echo ==============================================

if exist "C:\Program Files\Java\jdk1.8.0_202" (
    set "JAVA_HOME=C:\Program Files\Java\jdk1.8.0_202"
    set "PATH=C:\Program Files\Java\jdk1.8.0_202\bin;%PATH%"
)

if not exist "apache-maven-3.9.6\bin\mvn.cmd" (
    echo ERROR: Maven not found.
    pause
    exit /b
)

:: CLEANUP
echo Clearing old PIT plugin cache...
if exist "%USERPROFILE%\.m2\repository\org\pitest" (
    rmdir /s /q "%USERPROFILE%\.m2\repository\org\pitest"
)

:: FORCE DOWNLOAD
echo Forcing download of PIT 1.4.3...
call "apache-maven-3.9.6\bin\mvn.cmd" dependency:get -Dartifact=org.pitest:pitest-maven-plugin:1.4.3

echo.
echo Running Mutation Coverage...
call "apache-maven-3.9.6\bin\mvn.cmd" org.pitest:pitest-maven-plugin:mutationCoverage

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo MUTATION TESTING FAILED!
) else (
    echo.
    echo MUTATION TESTING COMPLETED!
    echo Report generated in target/pit-reports/
)

echo.
echo Press any key to close...
pause
