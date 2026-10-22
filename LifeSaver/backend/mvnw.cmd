@REM ----------------------------------------------------------------------------
@REM LifeSaver Maven Wrapper Script for Windows
@REM ----------------------------------------------------------------------------
@echo off
setlocal

set "MAVEN_CMD=C:\Users\Abhishek\tools\apache-maven-3.9.9\bin\mvn.cmd"

if exist "%MAVEN_CMD%" (
    call "%MAVEN_CMD%" %*
) else (
    where mvn >nul 2>nul
    if %ERRORLEVEL% equ 0 (
        call mvn %*
    ) else (
        echo [ERROR] Maven not found. Please install Apache Maven or verify C:\Users\Abhishek\tools\apache-maven-3.9.9\bin\mvn.cmd
        exit /b 1
    )
)
