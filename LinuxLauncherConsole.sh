#!/bin/bash

# делаем файл исполняемым: chmod +x LinuxLauncherConsole.sh

java -classpath ./bin main.Executor

# Оставляем консоль на экране, если возникла ошибка
[ "$?" -ne 0 ] && echo "An error occured. Please send this stack trace to developer." && read
