@ECHO off
SETLOCAL ENABLEDELAYEDEXPANSION
REM set directories
SET inputDir=..\Input\
SET outputDir=..\Output\
SET executableDir=..\Executables\
SET masterDir=..\Master\
SET rankingJAR=TournamentRanking.jar
REM time in seconds
SET timePerInputFile=120

ECHO Solve all problems of directory: %inputDir%

REM use all jar files from the executable directory
FOR %%j IN (%executableDir%*) DO (
	SET jarFileWithoutExtension=%%~nj
	ECHO !jarFileWithoutExtension!
	REM use all input files from the input directory
	FOR %%i IN (%inputDir%*) DO (
		SET inputFileWithoutExtension=%%~ni
		REM create output directory by jar file name if it does not exist
		IF NOT EXIST %outputDir%!jarFileWithoutExtension! (
			MD %outputDir%!jarFileWithoutExtension!
		)
		ECHO !inputFileWithoutExtension!
		REM create Java call in the format "java -jar (jar file path) (input file path) (output file path)"
		SET toCall=java -jar %%j %%i %outputDir%!jarFileWithoutExtension!\!inputFileWithoutExtension!.out
		REM execute Java call
		REM labels that can be used for the START command
			REM /WAIT=execute the commands one after the other
			REM /B=execute the commands in the current window
		START !toCall!
		REM the TIMEOUT and TASKKILL command have to be adapted for Linux operating systems
		REM remove >nul 2>&1 to see the countdown in the console window 
		TIMEOUT /T %timePerInputFile% /NOBREAK >nul 2>&1
		REM TASKKILL kills the java process
		REM remove >nul 2>&1 for more information
		TASKKILL /F /IM java.exe >nul 2>&1
	)
)

REM execute the TournamentRanking.jar 
SET rankingCall=java -jar %rankingJAR% %inputDir% %outputDir% %masterDir%
START /WAIT !rankingCall!

PAUSE