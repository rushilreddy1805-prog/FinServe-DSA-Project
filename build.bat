@echo off
REM ============================================================
REM  FinServe - build script
REM  Compiles all sources under src\ into out\.
REM  Main.java references every module, so javac compiles the
REM  whole project transitively via -sourcepath.
REM ============================================================
setlocal
cd /d "%~dp0"

set "JAVAC="
where javac >nul 2>nul && set "JAVAC=javac"
if not defined JAVAC if defined JAVA_HOME if exist "%JAVA_HOME%\bin\javac.exe" set "JAVAC=%JAVA_HOME%\bin\javac.exe"
if not defined JAVAC if exist "%ProgramFiles%\Android\Android Studio\jbr\bin\javac.exe" set "JAVAC=%ProgramFiles%\Android\Android Studio\jbr\bin\javac.exe"
if not defined JAVAC if exist "%ProgramFiles%\JetBrains\PyCharm 2026.1\jbr\bin\javac.exe" set "JAVAC=%ProgramFiles%\JetBrains\PyCharm 2026.1\jbr\bin\javac.exe"
if not defined JAVAC if exist "%ProgramFiles%\Eclipse Adoptium\jdk-17\bin\javac.exe" set "JAVAC=%ProgramFiles%\Eclipse Adoptium\jdk-17\bin\javac.exe"

if not defined JAVAC (
  echo [ERROR] Could not find javac. Install a JDK or set JAVA_HOME.
  exit /b 1
)

echo Using compiler: %JAVAC%
if not exist out mkdir out

"%JAVAC%" --release 8 -encoding UTF-8 -d out -sourcepath src "src\finserve\Main.java" 2>nul
if not errorlevel 1 goto built

echo Retrying without --release 8 ...
"%JAVAC%" -encoding UTF-8 -d out -sourcepath src "src\finserve\Main.java"
if errorlevel 1 goto failed

:built
echo [OK] Build successful. Classes are in out\
endlocal
exit /b 0

:failed
echo [FAILED] Build failed.
endlocal
exit /b 1
