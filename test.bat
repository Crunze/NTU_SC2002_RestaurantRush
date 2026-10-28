@echo off
if exist out-test rmdir /s /q out-test
mkdir out-test
javac --release 17 -encoding UTF-8 -d out-test -sourcepath ".;test" test\NTUBurger\TestRunner.java || exit /b 1
java -ea -cp out-test NTUBurger.TestRunner
