@ECHO OFF

REM Parse scala version argument
SET _SCALA_VERSION=%1
IF [%_SCALA_VERSION%]==[] (
	SET _SCALA_VERSION=3.0.2
)
ECHO Scala Version: %_SCALA_VERSION%

REM Setup path variables
SET _TOOLS_DIR=%~d0\tools
SET _INSTALLATION_DIR=%_TOOLS_DIR%\scala3
SET _BIN_DIR=%_INSTALLATION_DIR%\bin

IF NOT EXIST "%_TOOLS_DIR%\" (
	MKDIR %_TOOLS_DIR%
)

REM Download scala3 zip from github and extract to tools\ directory
curl -L --url https://github.com/lampepfl/dotty/releases/download/%_SCALA_VERSION%/scala3-%_SCALA_VERSION%.zip --output "scala3.zip"
tar -C "%_TOOLS_DIR%" -xf "scala3.zip"

IF %ERRORLEVEL% NEQ 0 (
	ECHO Failed to extract files
	GOTO Cleanup
)


REM Delete old installation if it exists and rename new installation directory to scala3
IF EXIST "%_INSTALLATION_DIR%\" (
	RMDIR /S /Q "%_INSTALLATION_DIR%"
)
RENAME "%_TOOLS_DIR%\scala3-%_SCALA_VERSION%" "scala3"


REM Save old path
FOR /F "tokens=2*" %%i IN ('REG QUERY "HKCU\Environment" /v "Path" 2^>NUL ^| FIND /I "Path"') DO (SET "_OLD_PATH=%%j")

REM Add scala directory to path if not already there
ECHO."%Path%" | FIND /I "%_BIN_DIR%">NUL && ( 
	ECHO Path ok, already includes: "%_BIN_DIR%"
) || (
	SET "Path=%Path%;%_BIN_DIR%"

	IF DEFINED _OLD_PATH (
		ECHO Adding "%_BIN_DIR%" to environment Path
		REG ADD "HKCU\Environment" /f /v Path /t REG_SZ /d "%_OLD_PATH%;%_BIN_DIR%" >NUL
	) ELSE (
		ECHO Creating new Path environment variable
		REG ADD "HKCU\Environment" /f /v Path /t REG_SZ /d "%_BIN_DIR%" >NUL
	)

	SETX DUMMY "" >NUL
	REG DELETE "HKCU\Environment" /v DUMMY /f >NUL
)

:Cleanup
REM Delete zip file
DEL "scala3.zip"