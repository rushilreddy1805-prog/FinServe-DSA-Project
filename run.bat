@echo off
REM ============================================================
REM  FinServe - build (if needed) and launch the CLI
REM ============================================================
setlocal
cd /d "%~dp0"

call build.bat
if errorlevel 1 exit /b 1

set "JAVA="
where java >nul 2>nul && set "JAVA=java"
if not defined JAVA if defined JAVA_HOME if exist "%JAVA_HOME%\bin\java.exe" set "JAVA=%JAVA_HOME%\bin\java.exe"

if not defined JAVA (
  echo [ERROR] Could not find java.
  exit /b 1
)

"%JAVA%" -cp out finserve.Main
endlocal
