@echo off

:: [�������� ��������� ������] - [��������]

:: lexer_auto_end - �������������� ������ END ; � ����� ��������� ������������������, ����� �� ��������� ��� ������� ��� ������������� �����
:: no_lexer_auto_end
:: lexer_print - ����� ��������� ������
:: no_lexer_print
:: interactive_mode - ������������� �����, if ������ � () � {} ������ ���� �� ����� ������
:: no_interactive_mode - �������� �����, ����������� ������ if ������ � () � {} �� ������ �������

java -classpath ./bin Main no_lexer_auto_end no_interactive_mode < "in.txt" > "out.txt"
::java -classpath ./bin Main lexer_print < "in.txt" > "out.txt"
::pause