package lexer;


/**
 * Перечисление названий всех возможных терминалов
 * */

public enum Tag {
	DOUBLE, BOOLEAN, INTEGER,
	NAME, IF, ELSE,
	
	ASSIGN, PLUS, MINUS, MUL, DIV, POW, FACTORIAL,
	LP, RP, LF, RF,
	SKIPABLE, L_COMMENT, R_COMMENT,
	END,
	EXIT, 
	PRINT, ADD, DEL, RESET, SET, HELP, STATE,	
	ILLEGAL_TOKEN, SIN, COS, COMMA;
}
