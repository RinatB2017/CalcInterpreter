package types;

public class FuncPrim {
	String name; // имя ф-ии или переменной
	FuncArgs args; // null если name - имя переменной
	TypedValue constant; // константа или объект функционального типа, который по сути тоже является константой, как например 2.
	// Сумма, например, объектов функционального типа даёт новый объект функционального типа
	// TODO type Brackets
	boolean equals(){
		return false; // TODO FuncPrim equals()
	}
}
