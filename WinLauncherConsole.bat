@echo off

:: [�������� ��������� ������] - [��������]

:: lexer_auto_end - �������������� ������ END ; � ����� ��������� ������������������, ����� �� ��������� ��� ������� ��� ������������� �����
:: no_lexer_auto_end
:: lexer_print - ����� ��������� ������
:: no_lexer_print
:: greedy_func - ������ �������: ������ �� �����������, ��, ��� �������� ����� ����� ������� � �� ������ END ; ��������� ���������� �������.
:: no_greedy_func


java -Dfile.encoding=Cp866 -classpath ./bin Executor "set auto_end=false"
::pause