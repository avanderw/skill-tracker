@ECHO off
SET FILENAME=demo.md

ECHO. > %FILENAME%
SETLOCAL

CALL :RECORD "--version"
CALL :RECORD "--help"
CALL :RECORD "achievement"
CALL :RECORD "achievement nemesis"
CALL :RECORD "game"
CALL :RECORD "game ls"
CALL :RECORD "game view AgeOfEmpires2"
CALL :RECORD "game view UnrealTournament"
CALL :RECORD "game view TableTennisVR"
CALL :RECORD "match"
CALL :RECORD "match ls"
CALL :RECORD "match view 9cacfb0b"
CALL :RECORD "match suggest --help"
CALL :RECORD "match suggest Andrew,JK,JDK,Wicus -g=AgeOfEmpires2 -s=2v2"
CALL :RECORD "match quality --help"
CALL :RECORD "match quality Andrew,JK Wicus,JDK -g UnrealTournament"
CALL :RECORD "player"
CALL :RECORD "player ls"
CALL :RECORD "player ls -l"
CALL :RECORD "player view JK"
CALL :RECORD "player view Andrew"
CALL :RECORD "player view Wicus"
CALL :RECORD "player view Wicus -g=AgeOfEmpires2"
CALL :RECORD "player view JK -g=UnrealTournament"
CALL :RECORD "player view JK -g=AgeOfEmpires2"
CALL :RECORD "player view Etienne"
CALL :RECORD "changelog"

EXIT 0
:RECORD

SET COMMAND=%1
SET COMMAND=%COMMAND:"=%
ECHO **$ %COMMAND%** >> %FILENAME%

SET RUN=java -jar ../target/skill-tracker-jar-with-dependencies.jar %COMMAND%
ECHO ``` >> %FILENAME%
%RUN% >> %FILENAME%
ECHO ``` >> %FILENAME%

EXIT /B 0