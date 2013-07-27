package lexer;

/**
 * Перечисление названий всех возможных терминалов
 * */

public enum Tag {
	DOUBLE, 
	WORD, BOOLEAN,
	IF, ELSE,
	
	ASSIGN, PLUSMINUS, MULDIV, POW, FACTORIAL,
	LP, RP, LF, RF,
	SKIPABLE, L_COMMENT, R_COMMENT,
	END,
	EXIT, 
	 	 	
	ILLEGAL_TOKEN;
}
