package types.func;

import types.TypedValue;

public class FuncPrim {
	String name=null; // имя ф-ии или переменной
	FuncArgs args=null; // null если name - имя переменной
	
	TypedValue constant=null; // константа или объект функционального типа, который по сути тоже является константой, как например 2.
	// Сумма, например, объектов функционального типа даёт новый объект функционального типа
	
	FuncPrimType type;
	
	boolean equals(){
		return false; // TODO FuncPrim equals()
	}
}
