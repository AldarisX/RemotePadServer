@echo off
protoc --java_out=src\main\java proto\*.proto
pause