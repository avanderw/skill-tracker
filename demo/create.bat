@ECHO off
SET FILENAME=demo.md

ECHO. > %FILENAME%
SETLOCAL

CALL :RECORD "skilltracker --version" "java -jar ../target/skill-tracker-jar-with-dependencies.jar --version"
CALL :RECORD "skilltracker --help" "java -jar ../target/skill-tracker-jar-with-dependencies.jar --help"
CALL :RECORD "skilltracker game" "java -jar ../target/skill-tracker-jar-with-dependencies.jar game"
CALL :RECORD "skilltracker game ls" "java -jar ../target/skill-tracker-jar-with-dependencies.jar game ls"
CALL :RECORD "skilltracker game view AgeOfEmpires2" "java -jar ../target/skill-tracker-jar-with-dependencies.jar game view AgeOfEmpires2"
CALL :RECORD "skilltracker game view UnrealTournament" "java -jar ../target/skill-tracker-jar-with-dependencies.jar game view UnrealTournament"
CALL :RECORD "skilltracker match" "java -jar ../target/skill-tracker-jar-with-dependencies.jar match"
CALL :RECORD "skilltracker match ls" "java -jar ../target/skill-tracker-jar-with-dependencies.jar match ls"
CALL :RECORD "skilltracker match view 9cacfb0b" "java -jar ../target/skill-tracker-jar-with-dependencies.jar match view 9cacfb0b"
CALL :RECORD "skilltracker match suggest --help" "java -jar ../target/skill-tracker-jar-with-dependencies.jar match suggest --help"
CALL :RECORD "skilltracker match suggest Andrew,JK,JDK,Wicus -g=AgeOfEmpires2 -s=2v2" "java -jar ../target/skill-tracker-jar-with-dependencies.jar match suggest Andrew,JK,JDK,Wicus -g=AgeOfEmpires2 -s=2v2"
CALL :RECORD "skilltracker player" "java -jar ../target/skill-tracker-jar-with-dependencies.jar player"
CALL :RECORD "skilltracker player ls" "java -jar ../target/skill-tracker-jar-with-dependencies.jar player ls"
CALL :RECORD "skilltracker player view JK" "java -jar ../target/skill-tracker-jar-with-dependencies.jar player view JK"
CALL :RECORD "skilltracker player view Andrew" "java -jar ../target/skill-tracker-jar-with-dependencies.jar player view Andrew"
CALL :RECORD "skilltracker changelog" "java -jar ../target/skill-tracker-jar-with-dependencies.jar changelog"

EXIT 0
:RECORD

SET HEADER=%1
SET HEADER=%HEADER:"=%
ECHO **$ %HEADER%** >> %FILENAME%

SET COMMAND=%2
SET COMMAND=%COMMAND:"=%
ECHO ``` >> %FILENAME%
%COMMAND% >> %FILENAME%
ECHO ``` >> %FILENAME%

EXIT /B 0