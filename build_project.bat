@ECHO OFF
ECHO Updating build version
START "Increment Version" /wait /min gradlew.bat increment_version ^& exit

ECHO Building .jar
START "Build Jar" /wait /min gradlew.bat build ^& exit

ECHO Moving .jar to "BUILDS"
robocopy "./build/libs" "./BUILDS" * /MOVE > nul

ECHO Updating versions.json
ECHO NOT IMPLEMENTED GOING MANUAL!
START /max notepad++ BUILDS/versions.json
EXIT