package types;

/**
 * Перечисление всех возможных типов.
 * @author Ник
 *
 */
public enum Types {
	BOOLEAN, INTEGER, DOUBLE, FUNCTION, VECTOR;

	public static int get(Types left) {
		switch(left){
		case BOOLEAN:
			return 1;
		case INTEGER:
			return 2;
		case DOUBLE:
			return 3;
		case FUNCTION:
			return 4;
		case VECTOR:
			return 5;
		}
		return 0;
		//return ordinal();
	}
	
	public static Types set(int t){
		if(t==1)
			return BOOLEAN;
		if(t==2)
			return INTEGER;
		if(t==3)
			return DOUBLE;
		if(t==4)
			return FUNCTION;
		if(t==5)
			return VECTOR;
		
		return null;
	}
}
