@echo off

:: [ПАРАМЕТР КОМАНДНОЙ СТРОКИ] - [ОПИСАНИЕ]

:: lexer_auto_end - Автодобавление токена END ; в конце считанной последовательности, чтобы не добавлять его вручную при интерактивном вводе
:: no_lexer_auto_end
:: lexer_print - Вывод найденных лексем
:: no_lexer_print
:: greedy_func - Жадные функции: скобки не обязательны, всё, что написано после имени функции и до токена END ; считается аргументом функции.
:: no_greedy_func


java -Dfile.encoding=Cp866 -classpath ./bin Executor "set auto_end=false"
::pause