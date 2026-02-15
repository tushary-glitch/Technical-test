@echo off
echo Building the project...
cd d:\Qualys\validator
call mvn clean package
if %ERRORLEVEL% NEQ 0 (
    echo Build failed. Please ensure Maven is installed and in your PATH.
    pause
    exit /b %ERRORLEVEL%
)

echo.
echo Running validation against records.json...
java -jar target/records-validator-1.0-SNAPSHOT.jar "d:\Qualys\records.json"
pause
