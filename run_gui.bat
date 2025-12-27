@echo off
cd /d "%~dp0"
echo ==============================================
echo Launching CryptoChecker GUI...
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

if not exist "apache-maven-3.9.6\bin\mvn.cmd" (
    echo ERROR: Could not find apache-maven-3.9.6\bin\mvn.cmd
    echo Please verify the folder exists.
    pause
    exit /b
)

call "apache-maven-3.9.6\bin\mvn.cmd" exec:java -Dexec.mainClass="com.cryptochecker.Main"

echo Application closed.
pause
