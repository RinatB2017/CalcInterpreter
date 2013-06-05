@echo off

:: [ПАРАМЕТР КОМАНДНОЙ СТРОКИ] - [ОПИСАНИЕ]

:: lexer_auto_end - Автодобавление токена END ; в конце считанной последовательности, чтобы не добавлять его вручную при интерактивном вводе
:: no_lexer_auto_end
:: lexer_print - Вывод найденных лексем
:: no_lexer_print
:: interactive_mode - Интерактивный режим, if вместе с () и {} должен быть на одной строке
:: no_interactive_mode - Пакетный режим, разрешается писать if вместе с () и {} на разных строках

java -classpath ./bin Main no_lexer_auto_end no_interactive_mode < "in.txt" > "out.txt"
::java -classpath ./bin Main lexer_print < "in.txt" > "out.txt"
::pause