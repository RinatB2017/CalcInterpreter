@echo off

:: [ПАРАМЕТР КОМАНДНОЙ СТРОКИ] - [ОПИСАНИЕ]

:: lexer_auto_end - Автодобавление токена END ; в конце считанной последовательности, чтобы не добавлять его вручную при интерактивном вводе
:: no_lexer_auto_end
:: lexer_print - Вывод найденных лексем
:: no_lexer_print


java -Dfile.encoding=Cp866 -classpath ./bin Main
pause