@echo off
echo Building project...
cd d:\Qualys\validator
call mvn clean package
if %ERRORLEVEL% NEQ 0 (
    echo Build failed.
    pause
    exit /b %ERRORLEVEL%
)

echo.
echo Running CSV conversion...
java -cp target/records-validator-1.0-SNAPSHOT.jar com.qualys.validator.RecordConverter "d:\Qualys\records.json"
pause
