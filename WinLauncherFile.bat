:: Хорошая справка по консольной Jav`е
:: http://habrahabr.ru/post/125210/

@echo off

:: ОПЦИЯ(Значение по умолчанию) // ОПИСАНИЕ

:: args_auto_end(true)// Автодобавление токена END в конце считанной последовательности
:: auto_end(false) // Автодобавление токена END в конце считанной последовательности
:: print_tokens(false) // Вывод найденных токенов для просканированной строки
:: table // Таблица переменных и функций, разрешён только сброс, также см. удаление оператором del и добавление оператором add
:: precision(5) // Отрицательная степень 10, используемая при сравнении малых значений методом doubleCompare()
:: errors(0) // Счётчик возникших ошибок
:: stricted(true) // Запрет автосоздания переменных
:: auto_print(true) // Автоматический вывод значений выражений
:: greedy_func(false) // Жадные функции: скобки не обязательны, всё, что написано после имени функции и до токена 'END' ";" считается аргументом функции.
:: dim(deg) размерность для входа(sin) и выхода(arcsin) тригонометрических ф-ий

java -classpath ./bin main.Executor "set auto_end= false; set greedy_func = false; set dim = deg; set stricted=false" < "in.txt" > "out.txt" 2> "err.txt"

::if ERRORLEVEL 1 pause
if ERRORLEVEL 1 (
start .\err.txt
pause
)